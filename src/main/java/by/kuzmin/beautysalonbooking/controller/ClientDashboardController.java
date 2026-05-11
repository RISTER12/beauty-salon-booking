package by.kuzmin.beautysalonbooking.controller;

import by.kuzmin.beautysalonbooking.dto.AppointmentDto;
import by.kuzmin.beautysalonbooking.dto.ClientProfileDto;
import by.kuzmin.beautysalonbooking.dto.ClientUpdateDto;
import by.kuzmin.beautysalonbooking.service.AppointmentService;
import by.kuzmin.beautysalonbooking.service.ClientService;
import by.kuzmin.beautysalonbooking.service.ServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

@Controller
@RequestMapping("/client")
@RequiredArgsConstructor
public class ClientDashboardController {

    private final ClientService clientService;
    private final AppointmentService appointmentService;
    private final ServiceService serviceService;

    // Временное решение: храним clientId в сессии
    // В реальном приложении после логина сюда сохраняется ID клиента
    @GetMapping("/set-client")
    public String setClient(@RequestParam Long clientId, HttpSession session) {
        session.setAttribute("clientId", clientId);
        return "redirect:/client/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) {
        Long clientId = getClientId(session);
        Long salonId = 1L; // подставьте свой salonId или получите из сессии

        ClientProfileDto client = clientService.getClientProfile(clientId);
        List<AppointmentDto> upcomingAppointments = appointmentService.getUpcomingAppointments(clientId, salonId);
        long totalVisits = clientService.getTotalVisits(clientId);
        double totalSpent = clientService.getTotalSpent(clientId);
        OffsetDateTime lastVisit = clientService.getLastVisit(clientId);
        long pendingCount = appointmentService.getPendingCount(clientId, salonId);

        model.addAttribute("client", client);
        model.addAttribute("currentPage", "dashboard");
        model.addAttribute("totalVisits", totalVisits);
        model.addAttribute("totalSpent", totalSpent);
        model.addAttribute("lastVisit", lastVisit);
        model.addAttribute("upcomingAppointments", upcomingAppointments);
        model.addAttribute("pendingCount", pendingCount);
        model.addAttribute("loyaltyPoints", client.getLoyaltyPoints());
        model.addAttribute("recommendedServices", serviceService.getRecommendedServices(clientId));

        return "client/dashboard";
    }

    @GetMapping("/profile")
    public String profile(Model model, HttpSession session) {
        Long clientId = getClientId(session);
        ClientProfileDto client = clientService.getClientProfile(clientId);
        model.addAttribute("client", client);
        model.addAttribute("currentPage", "profile");
        return "client/profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@ModelAttribute ClientUpdateDto updateDto, HttpSession session) {
        Long clientId = getClientId(session);
        clientService.updateClientProfile(clientId, updateDto);
        return "redirect:/client/profile?success=true";
    }

    @PostMapping("/appointments/{id}/cancel")
    @ResponseBody
    public String cancelAppointment(@PathVariable Long id,
                                    @RequestParam(required = false) String reason,
                                    @RequestParam(required = false) Long roleId) {
        appointmentService.cancelAppointment(id, reason, roleId);
        return "ok";
    }

    @GetMapping("/appointments")
    public String appointments(Model model, HttpSession session) {
        Long clientId = getClientId(session);
        Long salonId = 1L;

        List<AppointmentDto> upcomingAppointments = appointmentService.getUpcomingAppointments(clientId, salonId);
        List<AppointmentDto> historyAppointments = appointmentService.getHistoryAppointments(clientId, salonId);

        model.addAttribute("upcomingAppointments", upcomingAppointments);
        model.addAttribute("historyAppointments", historyAppointments);
        model.addAttribute("currentPage", "appointments");

        return "client/appointments";
    }

    private Long getClientId(HttpSession session) {
        Long clientId = (Long) session.getAttribute("clientId");
        if (clientId == null) {
            return 1L;
        }
        return clientId;
    }
}