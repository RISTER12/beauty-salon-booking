package by.kuzmin.beautysalonbooking.controller;

import by.kuzmin.beautysalonbooking.service.ServiceService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/booking/select-service")
@AllArgsConstructor
public class SelectServiceController {
    ServiceService serviceService;
    @GetMapping
    public String selectService(Model model) {
        model.addAttribute("services", serviceService.findAll());
        return "online-booking-select-service";
    }
}
