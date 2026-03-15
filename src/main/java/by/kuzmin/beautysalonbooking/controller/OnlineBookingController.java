package by.kuzmin.beautysalonbooking.controller;

import by.kuzmin.beautysalonbooking.dto.CreateEmployeeResponseDto;

import by.kuzmin.beautysalonbooking.service.EmployeeService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/booking")
@AllArgsConstructor
public class OnlineBookingController {
    private EmployeeService employeeService;
    //todo надо будет добавить дату и время в параметры
    @GetMapping
    public String booking(@RequestParam(required = false, name = "employee") Long employeeId, Model model) {
        if (employeeId != null && employeeId > 0) {
            CreateEmployeeResponseDto employeeResponseDto = employeeService.findEmployeeById(employeeId);
            model.addAttribute("employee", employeeResponseDto);
        } else if (employeeId != null) {
            model.addAttribute("employee", employeeId);
        }
        return "online-booking-menu";
    }


    @GetMapping("/select-service")
    public String selectService() {
        return "online-booking-service-selection";
    }
}
