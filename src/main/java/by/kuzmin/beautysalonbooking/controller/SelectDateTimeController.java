package by.kuzmin.beautysalonbooking.controller;

import by.kuzmin.beautysalonbooking.dto.TimeslotDto;
import by.kuzmin.beautysalonbooking.service.TimeslotService;
import lombok.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping("/booking/select-date-time")
public class SelectDateTimeController {
    private TimeslotService timeslotService;

    @GetMapping
    public String selectDateTime() {
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
        List<TimeslotDto> timeslots = timeslotService.findAllByDay(localDate, 1L);
        timeslots.forEach(System.out::println);
        return ResponseEntity.ok(timeslots);
    }

    @PostMapping("/month-slots")
    @ResponseBody
    public ResponseEntity<?> selectDateTimeByMonth(@RequestBody MonthRequest request) {
        List<TimeslotDto> timeslots = timeslotService.findAllByMonth(request.getYear(), request.getMonth(), 1L);
        timeslots.forEach(System.out::println);
        System.out.println("selectDateTimeByMonth(request)");
        return ResponseEntity.ok(timeslots);
    }
}

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
// Класс, соответствующий JSON из запроса
class DateSelectionRequest {
    private int year;
    private int month;
    private int day;
    private String dateString;

    // геттеры и сеттеры обязательны!
}

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
class MonthRequest {
    private int year;
    private int month;
}
