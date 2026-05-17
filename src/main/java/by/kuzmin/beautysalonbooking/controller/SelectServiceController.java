package by.kuzmin.beautysalonbooking.controller;

import by.kuzmin.beautysalonbooking.dto.admin.SalonDto;
import by.kuzmin.beautysalonbooking.service.ServiceService;
import by.kuzmin.beautysalonbooking.service.admin.AdminService;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/booking/select-service")
@AllArgsConstructor
public class SelectServiceController {
    ServiceService serviceService;
    AdminService adminService;

    @GetMapping
    public String selectService(@RequestParam(required = false, name = "slotId") Long slotId,
                                @RequestParam(required = false, name = "employeeId") Long employeeId,
                                HttpSession session,
                                Model model) {

        // Получаем выбранный салон из сессии
        Long salonId = (Long) session.getAttribute("selectedSalonId");

        // Получаем информацию о салоне для отображения в шапке
        SalonDto selectedSalon = adminService.getCurrentSalon(salonId);
        model.addAttribute("selectedSalon", selectedSalon);

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