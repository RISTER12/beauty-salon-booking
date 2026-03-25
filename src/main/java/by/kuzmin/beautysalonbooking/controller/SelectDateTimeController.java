package by.kuzmin.beautysalonbooking.controller;

import by.kuzmin.beautysalonbooking.dto.DateSelectionRequest;
import by.kuzmin.beautysalonbooking.dto.MonthRequest;
import by.kuzmin.beautysalonbooking.dto.TimeslotDto;
import by.kuzmin.beautysalonbooking.service.TimeslotService;
import lombok.*;
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

    @GetMapping
    public String selectDateTime(@RequestParam(required = false) Long employeeId, Model model) {
        model.addAttribute("employeeId", employeeId);
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


