package by.kuzmin.beautysalonbooking.controller;

import by.kuzmin.beautysalonbooking.dto.admin.SalonDto;
import by.kuzmin.beautysalonbooking.service.EmployeeService;
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
@RequestMapping("/booking/select-master")
@AllArgsConstructor
public class SelectMasterController {
    EmployeeService employeeService;
    AdminService adminService;

    @GetMapping
    public String selectMaster(@RequestParam(required = false, name = "slotId") Long slotId,
                               @RequestParam(required = false, name = "employeeId") Long employeeId,
                               @RequestParam(required = false, name = "serviceIds") List<Long> serviceIds,
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
        if (serviceIds != null && !serviceIds.isEmpty()) {
            model.addAttribute("serviceIds", serviceIds);
        }

        // Получаем только мастеров выбранного салона
        model.addAttribute("employees", employeeService.getEmployeesBySalon(salonId));

        return "online-booking-select-master";
    }
}