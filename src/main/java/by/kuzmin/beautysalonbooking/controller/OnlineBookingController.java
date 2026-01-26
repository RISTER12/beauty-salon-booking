package by.kuzmin.beautysalonbooking.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/booking")
public class OnlineBookingController {
    @GetMapping
    public String booking() {
        return "online-booking";
    }
}
