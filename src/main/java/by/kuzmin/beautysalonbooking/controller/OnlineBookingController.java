package by.kuzmin.beautysalonbooking.controller;

import by.kuzmin.beautysalonbooking.dto.ClientBookingDto;
import by.kuzmin.beautysalonbooking.dto.CreateEmployeeResponseDto;
import by.kuzmin.beautysalonbooking.dto.TimeslotDto;
import by.kuzmin.beautysalonbooking.dto.admin.SalonDto;
import by.kuzmin.beautysalonbooking.service.BookingService;
import by.kuzmin.beautysalonbooking.service.EmployeeService;
import by.kuzmin.beautysalonbooking.service.ServiceService;
import by.kuzmin.beautysalonbooking.service.TimeslotService;
import by.kuzmin.beautysalonbooking.service.admin.AdminService;
import jakarta.servlet.http.HttpSession;
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
    private AdminService adminService;

    @GetMapping("/select-salon")
    public String selectSalon(Model model) {
        List<SalonDto> salons = adminService.getAllSalons();
        model.addAttribute("salons", salons);
        return "online-booking-select-salon";
    }

    @PostMapping("/select-salon")
    public String saveSalon(@RequestParam Long salonId, HttpSession session) {
        session.setAttribute("selectedSalonId", salonId);
        return "redirect:/booking";
    }

    @GetMapping
    public String booking(@RequestParam(required = false, name = "employeeId") Long employeeId,
                          @RequestParam(required = false, name = "slotId") Long slotId,
                          @RequestParam(required = false, name = "serviceIds") List<Long> serviceIds,
                          HttpSession session,
                          Model model) {

        Long salonId = (Long) session.getAttribute("selectedSalonId");
        if (salonId == null) {
            return "redirect:/booking/select-salon";
        }

        SalonDto selectedSalon = adminService.getCurrentSalon(salonId);
        model.addAttribute("selectedSalon", selectedSalon);

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

        model.addAttribute("clientDto", new ClientBookingDto());

        return "online-booking-menu";
    }

    @PostMapping
    public String createBooking(@ModelAttribute ClientBookingDto bookingDto,
                                @RequestParam(required = false) Long employeeId,
                                @RequestParam(required = false) Long slotId,
                                @RequestParam(required = false) List<Long> serviceIds,
                                HttpSession session) {

        Long salonId = (Long) session.getAttribute("selectedSalonId");

        bookingDto.setEmployeeId(employeeId);
        bookingDto.setSlotId(slotId);
        bookingDto.setServiceIds(serviceIds);
        bookingDto.setSalonId(salonId);

        bookingService.createBooking(bookingDto);

        return "redirect:/booking/success";
    }

    @GetMapping("/success")
    public String success() {
        return "booking-success";
    }
}