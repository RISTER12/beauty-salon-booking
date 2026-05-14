package by.kuzmin.beautysalonbooking.controller;

import by.kuzmin.beautysalonbooking.dto.EmployeeAppointmentDto;
import by.kuzmin.beautysalonbooking.service.EmployeeScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/employee")
@RequiredArgsConstructor
public class EmployeeDashboardController {

    private final EmployeeScheduleService scheduleService;

    @GetMapping("/set-employee")
    public String setEmployee(@RequestParam Long employeeId, HttpSession session) {
        session.setAttribute("employeeId", employeeId);
        return "redirect:/employee/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) {
        Long employeeId = getEmployeeId(session);

        // Текущая неделя
        LocalDate today = LocalDate.now();
        List<EmployeeAppointmentDto> todayAppointments = scheduleService.getAppointmentsByDate(employeeId, today);
        List<EmployeeAppointmentDto> upcomingAppointments = scheduleService.getUpcomingAppointments(employeeId, today);

        model.addAttribute("todayAppointments", todayAppointments);
        model.addAttribute("upcomingAppointments", upcomingAppointments);
        model.addAttribute("currentPage", "dashboard");
        model.addAttribute("today", today);

        return "employee/dashboard";
    }

    @GetMapping("/appointment/{id}")
    public String appointmentDetail(@PathVariable Long id, Model model, HttpSession session) {
        Long employeeId = getEmployeeId(session);
        EmployeeAppointmentDto appointment = scheduleService.getAppointmentDetail(employeeId, id);

        model.addAttribute("appointment", appointment);
        model.addAttribute("currentPage", "appointment");

        return "employee/appointment-detail";
    }

    @PostMapping("/appointment/{id}/complete")
    @ResponseBody
    public ResponseEntity<?> completeAppointment(@PathVariable Long id) {
        try {
            scheduleService.markAsCompleted(id);
            return ResponseEntity.ok().body("{\"success\": true}");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    private Long getEmployeeId(HttpSession session) {
        Long employeeId = (Long) session.getAttribute("employeeId");
        if (employeeId == null) {
            return 1L; // временное решение
        }
        return employeeId;
    }

    @GetMapping("/schedule")
    public String schedule(Model model, HttpSession session,
                           @RequestParam(required = false) Integer year,
                           @RequestParam(required = false) Integer month) {
        Long employeeId = getEmployeeId(session);

        LocalDate currentDate = LocalDate.now();
        int currentYear = year != null ? year : currentDate.getYear();
        int currentMonth = month != null ? month : currentDate.getMonthValue();

        List<EmployeeAppointmentDto> appointments = scheduleService.getAppointmentsByMonth(employeeId, currentYear, currentMonth);

        model.addAttribute("appointments", appointments);
        model.addAttribute("currentYear", currentYear);
        model.addAttribute("currentMonth", currentMonth);
        model.addAttribute("currentPage", "schedule");

        return "employee/schedule";
    }

    @GetMapping("/schedule/date")
    @ResponseBody
    public ResponseEntity<List<EmployeeAppointmentDto>> getAppointmentsByDate(
            @RequestParam String date,
            HttpSession session) {
        Long employeeId = getEmployeeId(session);
        LocalDate selectedDate = LocalDate.parse(date);

        List<EmployeeAppointmentDto> appointments = scheduleService.getAppointmentsByDate(employeeId, selectedDate);
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/schedule/month-data")
    @ResponseBody
    public ResponseEntity<List<EmployeeAppointmentDto>> getMonthData(
            @RequestParam int year,
            @RequestParam int month,
            HttpSession session) {
        Long employeeId = getEmployeeId(session);

        List<EmployeeAppointmentDto> appointments = scheduleService.getAppointmentsByMonth(employeeId, year, month);
        return ResponseEntity.ok(appointments);
    }
}