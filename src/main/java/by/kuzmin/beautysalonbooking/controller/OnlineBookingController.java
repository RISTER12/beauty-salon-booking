package by.kuzmin.beautysalonbooking.controller;

import by.kuzmin.beautysalonbooking.dto.ClientBookingDto;
import by.kuzmin.beautysalonbooking.dto.CreateEmployeeResponseDto;
import by.kuzmin.beautysalonbooking.dto.TimeslotDto;
import by.kuzmin.beautysalonbooking.service.BookingService;
import by.kuzmin.beautysalonbooking.service.EmployeeService;
import by.kuzmin.beautysalonbooking.service.ServiceService;
import by.kuzmin.beautysalonbooking.service.TimeslotService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/booking")
@AllArgsConstructor
public class OnlineBookingController {
    private EmployeeService employeeService;
    private TimeslotService timeslotService;
    private ServiceService serviceService;
    private BookingService bookingService;

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
            model.addAttribute("serviceList", serviceService.findAllByIds(serviceIds));
        }

        // Добавляем DTO для формы
        model.addAttribute("clientDto", new ClientBookingDto());

        return "online-booking-menu";
    }

    @PostMapping
    public String createBooking(@ModelAttribute ClientBookingDto bookingDto,
                                @RequestParam(required = false) Long employeeId,
                                @RequestParam(required = false) Long slotId,
                                @RequestParam(required = false) List<Long> serviceIds) {

        bookingDto.setEmployeeId(employeeId);
        bookingDto.setSlotId(slotId);
        bookingDto.setServiceIds(serviceIds);

        // Сохраняем бронирование
        bookingService.createBooking(bookingDto);

        return "redirect:/booking/success";
    }

    @GetMapping("/success")
    public String success() {
        return "booking-success";
    }
}