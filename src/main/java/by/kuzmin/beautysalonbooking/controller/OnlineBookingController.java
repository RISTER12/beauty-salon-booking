package by.kuzmin.beautysalonbooking.controller;

import by.kuzmin.beautysalonbooking.dto.CreateEmployeeResponseDto;

import by.kuzmin.beautysalonbooking.dto.TimeslotDto;
import by.kuzmin.beautysalonbooking.service.EmployeeService;
import by.kuzmin.beautysalonbooking.service.ServiceService;
import by.kuzmin.beautysalonbooking.service.TimeslotService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/booking")
@AllArgsConstructor
public class OnlineBookingController {
    private EmployeeService employeeService;
    private TimeslotService timeslotService;
    private ServiceService serviceService;

    @GetMapping
    public String booking(@RequestParam(required = false, name = "employeeId") Long employeeId,
                          @RequestParam(required = false, name = "slotId") Long slotId,
                          @RequestParam(required = false, name = "serviceIds") List<Long> serviceIds,
                          Model model) {
        if (slotId != null) {
            TimeslotDto timeslotDto = timeslotService.findById(slotId);
            model.addAttribute("timeslot", timeslotDto);
            model.addAttribute("slotId", slotId);
        }
        if (employeeId != null && employeeId > 0) {
            CreateEmployeeResponseDto employeeResponseDto = employeeService.findEmployeeById(employeeId);
            model.addAttribute("employee", employeeResponseDto);
            model.addAttribute("employeeId", employeeId);
        } else if (employeeId != null) {
            model.addAttribute("employeeId", employeeId);
        }

        if (serviceIds != null && !serviceIds.isEmpty()) {
            model.addAttribute("serviceIds", serviceIds);
            model.addAttribute("serviceList", serviceService.findAllByIds(serviceIds)) ;
        }
        return "online-booking-menu";
    }
}
