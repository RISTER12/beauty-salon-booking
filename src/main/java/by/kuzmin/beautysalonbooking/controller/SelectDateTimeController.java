package by.kuzmin.beautysalonbooking.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@AllArgsConstructor
@RequestMapping("/booking/select-date-time")
public class SelectDateTimeController {
    @GetMapping
    public String selectDateTime() {
        return "online-booking-select-date-time";
    }
}
