package by.kuzmin.beautysalonbooking.controller;

import by.kuzmin.beautysalonbooking.dto.ServiceCategoryResponseDto;
import by.kuzmin.beautysalonbooking.dto.ServiceResponseDto;
import by.kuzmin.beautysalonbooking.dto.ServiceWithCategoryDto;
import by.kuzmin.beautysalonbooking.service.ServiceService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/booking/select-service")
@AllArgsConstructor
public class SelectServiceController {
    ServiceService serviceService;
    @GetMapping
    public String selectService(@RequestParam(required = false, name = "slotId") Long slotId,
                                @RequestParam(required = false, name = "employeeId") Long employeeId,
                                Model model) {
        if (slotId != null) {
            model.addAttribute("slotId", slotId);
        }
        if (employeeId != null) {
            model.addAttribute("employeeId", employeeId);
        }

        model.addAttribute("servicesGroupByCategory", serviceService.findAllWithCategoryDto());
        return "online-booking-select-service";
    }
}
