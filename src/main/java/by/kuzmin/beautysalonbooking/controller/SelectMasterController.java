package by.kuzmin.beautysalonbooking.controller;

import by.kuzmin.beautysalonbooking.service.EmployeeService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/booking/select-master")
@AllArgsConstructor
public class SelectMasterController {
    EmployeeService employeeService;

    @GetMapping
    public String selectMaster(@RequestParam(required = false, name = "slotId") Long slotId,
                               @RequestParam(required = false, name = "employeeId") Long employeeId,
                               @RequestParam(required = false, name = "serviceIds") List<Long> serviceIds,
                               Model model) {
        if (slotId != null) {
            model.addAttribute("slotId", slotId);
        }
        if (employeeId != null) {
            model.addAttribute("employeeId", employeeId);
        }
        if (serviceIds != null && !serviceIds.isEmpty()) {
            model.addAttribute("serviceIds", serviceIds);
        }

        model.addAttribute("employees", employeeService.getEmployees());
        return "online-booking-select-master";
    }
}
