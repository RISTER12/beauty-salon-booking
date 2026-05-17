package by.kuzmin.beautysalonbooking.controller;

import by.kuzmin.beautysalonbooking.dto.DateSelectionRequest;
import by.kuzmin.beautysalonbooking.dto.MonthRequest;
import by.kuzmin.beautysalonbooking.dto.TimeslotDto;
import by.kuzmin.beautysalonbooking.dto.admin.SalonDto;
import by.kuzmin.beautysalonbooking.service.TimeslotService;
import by.kuzmin.beautysalonbooking.service.admin.AdminService;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/booking/select-date-time")
@AllArgsConstructor
public class SelectDateTimeController {
    private TimeslotService timeslotService;
    private AdminService adminService;

    @GetMapping
    public String selectDateTime(@RequestParam(required = false) Long employeeId,
                                 @RequestParam(required = false, name = "serviceIds") List<Long> serviceIds,
                                 HttpSession session,
                                 Model model) {

        // Получаем выбранный салон из сессии
        Long salonId = (Long) session.getAttribute("selectedSalonId");

        // Получаем информацию о салоне для отображения в шапке
        SalonDto selectedSalon = adminService.getCurrentSalon(salonId);
        model.addAttribute("selectedSalon", selectedSalon);

        model.addAttribute("employeeId", employeeId);
        if (serviceIds != null && !serviceIds.isEmpty()) {
            model.addAttribute("serviceIds", serviceIds);
        }
        return "online-booking-select-date-time";
    }

    @PostMapping
    @ResponseBody
    public ResponseEntity<?> selectDateTime(@RequestBody DateSelectionRequest request) {
        LocalDate localDate = LocalDate.of(
                request.getYear(),
                request.getMonth(),
                request.getDay()
        );
        List<TimeslotDto> timeslots;
        if (request.getEmployeeId() != null) {
            timeslots = timeslotService.findAllByDayAndEmployeeId(
                    request.getEmployeeId(),
                    localDate,
                    1L
            );
        } else {
            timeslots = timeslotService.findAllByDay(localDate, 1L);
        }
        return ResponseEntity.ok(timeslots);
    }

    @PostMapping("/month-slots")
    @ResponseBody
    public ResponseEntity<?> selectDateTimeByMonth(@RequestBody MonthRequest request) {
        List<TimeslotDto> timeslots;
        if (request.getEmployeeId() != null) {
            timeslots = timeslotService.findAllByMonthAndEmployeeId(
                    request.getEmployeeId(),
                    request.getYear(),
                    request.getMonth(),
                    1L
            );
        } else {
            timeslots = timeslotService.findAllByMonth(
                    request.getYear(),
                    request.getMonth(),
                    1L
            );
        }
        return ResponseEntity.ok(timeslots);
    }
}