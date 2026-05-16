package by.kuzmin.beautysalonbooking.controller.admin;

import by.kuzmin.beautysalonbooking.dto.admin.*;
import by.kuzmin.beautysalonbooking.entity.Salon;
import by.kuzmin.beautysalonbooking.repository.SalonRepository;
import by.kuzmin.beautysalonbooking.service.admin.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.Collections;
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


    @GetMapping("/api/services")
    @ResponseBody
    public ResponseEntity<List<ServiceResponseDto>> getServices() {
        return ResponseEntity.ok(adminService.getAllServices());
    }

    @GetMapping("/api/services/{id}")
    @ResponseBody
    public ResponseEntity<ServiceResponseDto> getService(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.getServiceById(id));
    }

    @PostMapping("/api/services")
    @ResponseBody
    public ResponseEntity<ServiceResponseDto> createService(@RequestBody ServiceRequestDto dto) {
        try {
            ServiceResponseDto created = adminService.createService(dto);
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/api/services/{id}")
    @ResponseBody
    public ResponseEntity<ServiceResponseDto> updateService(@PathVariable Long id, @RequestBody ServiceRequestDto dto) {
        try {
            ServiceResponseDto updated = adminService.updateService(id, dto);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/api/services/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteService(@PathVariable Long id) {
        try {
            adminService.deleteService(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/api/categories")
    @ResponseBody
    public ResponseEntity<List<ServiceCategoryDto>> getCategories() {
        return ResponseEntity.ok(adminService.getAllCategories());
    }

    @GetMapping("/api/categories/{id}")
    @ResponseBody
    public ResponseEntity<ServiceCategoryDto> getCategory(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.getCategoryById(id));
    }

    @PostMapping("/api/categories")
    @ResponseBody
    public ResponseEntity<ServiceCategoryDto> createCategory(@RequestBody ServiceCategoryDto dto) {
        try {
            ServiceCategoryDto created = adminService.createCategory(dto);
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/api/categories/{id}")
    @ResponseBody
    public ResponseEntity<ServiceCategoryDto> updateCategory(@PathVariable Long id, @RequestBody ServiceCategoryDto dto) {
        try {
            ServiceCategoryDto updated = adminService.updateCategory(id, dto);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/api/categories/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        try {
            adminService.deleteCategory(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/api/services/by-salon")
    @ResponseBody
    public ResponseEntity<List<ServiceResponseDto>> getServicesBySalon(@RequestParam Long salonId) {
        return ResponseEntity.ok(adminService.getServicesBySalon(salonId));
    }

    @GetMapping("/api/employees/by-salon")
    @ResponseBody
    public ResponseEntity<List<EmployeeResponseDto>> getEmployeesBySalon(@RequestParam Long salonId) {
        return ResponseEntity.ok(adminService.getEmployeesBySalon(salonId));
    }

    @GetMapping("/api/salons")
    @ResponseBody
    public ResponseEntity<List<SalonDto>> getSalons() {
        return ResponseEntity.ok(adminService.getAllSalons());
    }

    @GetMapping("/select-salon")
    public String selectSalon(@RequestParam(required = false) Long salonId,
                              @RequestParam(required = false) String redirect,
                              HttpSession session) {
        if (salonId == null || salonId == 0) {
            session.removeAttribute("adminSalonId");
        } else {
            session.setAttribute("adminSalonId", salonId);
        }

        if (redirect != null && !redirect.isEmpty()) {
            return "redirect:" + redirect;
        }
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/api/current-salon")
    @ResponseBody
    public ResponseEntity<?> getCurrentSalon(HttpSession session) {
        Long salonId = (Long) session.getAttribute("adminSalonId");
        if (salonId == null) {
            // Возвращаем null для "Все салоны"
            return ResponseEntity.ok(null);
        }
        SalonDto salon = adminService.getCurrentSalon(salonId);
        return ResponseEntity.ok(salon);
    }

    private void addSalonsToModel(Model model, HttpSession session) {
        model.addAttribute("salons", adminService.getAllSalons());
        Long currentSalonId = (Long) session.getAttribute("adminSalonId");
        model.addAttribute("currentSalonId", currentSalonId);
    }


    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) {
        if (session.getAttribute("isAdmin") == null) {
            return "redirect:/admin/set-admin";
        }
        model.addAttribute("currentPage", "dashboard");
        addSalonsToModel(model, session);
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
        addSalonsToModel(model, session);
        return "admin/schedule";
    }

    @GetMapping("/employees")
    public String employees(Model model, HttpSession session) {
        if (session.getAttribute("isAdmin") == null) {
            return "redirect:/admin/set-admin";
        }
        model.addAttribute("currentPage", "employees");
        addSalonsToModel(model, session);
        return "admin/employees";
    }

    @GetMapping("/services")
    public String services(Model model, HttpSession session) {
        if (session.getAttribute("isAdmin") == null) {
            return "redirect:/admin/set-admin";
        }
        model.addAttribute("currentPage", "services");
        addSalonsToModel(model, session);
        return "admin/services";
    }

    @GetMapping("/report")
    public String report(Model model, HttpSession session,
                         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
                         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        if (session.getAttribute("isAdmin") == null) {
            return "redirect:/admin/set-admin";
        }

        if (start == null) {
            start = LocalDate.now().withDayOfMonth(1);
        }
        if (end == null) {
            end = LocalDate.now();
        }

        model.addAttribute("currentPage", "report");
        model.addAttribute("startDate", start);
        model.addAttribute("endDate", end);
        addSalonsToModel(model, session);
        return "admin/report";
    }
}