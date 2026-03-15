package by.kuzmin.beautysalonbooking.controller;

import by.kuzmin.beautysalonbooking.service.EmployeeService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/select-master")
@AllArgsConstructor
public class SelectMasterController {
    EmployeeService employeeService;
    @GetMapping
    public String selectMaster(Model model) {
        model.addAttribute("employees", employeeService.getEmployees());
        return "online-booking-select-master";
    }
}
