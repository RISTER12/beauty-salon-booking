package by.kuzmin.beautysalonbooking.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/booking")
public class OnlineBookingController {
    @GetMapping()
    public String booking() {
        return "online-booking-menu";
    }
    @GetMapping("select-master")
    public String selectMaster() {
        return "online-booking-select-master";
    }
    @GetMapping("select-service")
    public String selectService() {
        return "online-booking-service-selection";
    }
    @GetMapping("select-date-time")
    public String selectDateTime() {
        return "online-booking-select-date-time";
    }
}
