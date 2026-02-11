package by.kuzmin.beautysalonbooking.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("admin-panel")
public class AdminPanelController {
    @GetMapping
    public String adminPanel(Model model) {
        return "admin-panel";
    }

    @GetMapping("employees")
    public String employees(Model model) {
        model.addAttribute("section", "employees");
        return "admin-panel";
    }

    @GetMapping("salons")
    public String salons(Model model) {
        model.addAttribute("section", "salons");
        return "admin-panel";
    }

    @GetMapping("services")
    public String services(Model model) {
        model.addAttribute("section", "services");
        return "admin-panel";
    }


}
