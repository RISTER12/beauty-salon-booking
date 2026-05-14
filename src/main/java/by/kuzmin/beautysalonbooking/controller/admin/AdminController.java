package by.kuzmin.beautysalonbooking.controller.admin;

import by.kuzmin.beautysalonbooking.dto.admin.*;
import by.kuzmin.beautysalonbooking.service.admin.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/set-admin")
    public String setAdmin(HttpSession session) {
        session.setAttribute("isAdmin", true);
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) {
        if (session.getAttribute("isAdmin") == null) {
            return "redirect:/admin/set-admin";
        }
        model.addAttribute("currentPage", "dashboard");
        return "admin/dashboard";
    }

    @GetMapping("/schedule")
    public String schedule(Model model, HttpSession session,
                           @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        if (session.getAttribute("isAdmin") == null) {
            return "redirect:/admin/set-admin";
        }

        if (date == null) {
            date = LocalDate.now();
        }

        List<AdminAppointmentDto> appointments = adminService.getAppointmentsByDate(date);

        model.addAttribute("appointments", appointments);
        model.addAttribute("selectedDate", date);
        model.addAttribute("currentPage", "schedule");
        return "admin/schedule";
    }

    @GetMapping("/schedule/range")
    @ResponseBody
    public ResponseEntity<List<AdminAppointmentDto>> getScheduleRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return ResponseEntity.ok(adminService.getAllAppointments(start, end));
    }

    @PostMapping("/appointments/{id}/cancel")
    @ResponseBody
    public ResponseEntity<?> cancelAppointment(@PathVariable Long id, @RequestParam String reason) {
        try {
            adminService.cancelAppointment(id, reason);
            return ResponseEntity.ok().body("{\"success\": true}");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @GetMapping("/workload")
    @ResponseBody
    public ResponseEntity<List<EmployeeWorkloadDto>> getWorkload(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return ResponseEntity.ok(adminService.getEmployeeWorkload(start, end));
    }

    @GetMapping("/service-stats")
    @ResponseBody
    public ResponseEntity<List<ServiceStatDto>> getServiceStats(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return ResponseEntity.ok(adminService.getServiceStats(start, end));
    }

    @GetMapping("/employees")
    public String employees(Model model, HttpSession session) {
        if (session.getAttribute("isAdmin") == null) {
            return "redirect:/admin/set-admin";
        }
        model.addAttribute("currentPage", "employees");
        return "admin/employees";
    }

    @GetMapping("/api/employees")
    @ResponseBody
    public ResponseEntity<List<EmployeeResponseDto>> getEmployees() {
        return ResponseEntity.ok(adminService.getAllEmployees());
    }

    @GetMapping("/api/employees/{id}")
    @ResponseBody
    public ResponseEntity<EmployeeResponseDto> getEmployee(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.getEmployeeById(id));
    }

    @PostMapping("/api/employees")
    @ResponseBody
    public ResponseEntity<EmployeeResponseDto> createEmployee(@RequestBody EmployeeRequestDto dto) {
        try {
            EmployeeResponseDto created = adminService.createEmployee(dto);
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/api/employees/{id}")
    @ResponseBody
    public ResponseEntity<EmployeeResponseDto> updateEmployee(@PathVariable Long id, @RequestBody EmployeeRequestDto dto) {
        try {
            EmployeeResponseDto updated = adminService.updateEmployee(id, dto);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/api/employees/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        try {
            adminService.deleteEmployee(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}