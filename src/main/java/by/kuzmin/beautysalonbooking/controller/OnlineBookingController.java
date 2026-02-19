package by.kuzmin.beautysalonbooking.controller;

import by.kuzmin.beautysalonbooking.service.EmployeeService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;
@Controller
@RequestMapping("/booking")
@AllArgsConstructor
public class OnlineBookingController {
    private EmployeeService employeeService;
    @GetMapping()
    public String booking() {
        return "online-booking-menu";
    }
    @GetMapping("/select-master")
    public String selectMaster(Model model) {
        model.addAttribute("employees", employeeService.getEmployees());
        return "online-booking-select-master";
    }
    @GetMapping("/select-service")
    public String selectService() {
        return "online-booking-service-selection";
    }
}
