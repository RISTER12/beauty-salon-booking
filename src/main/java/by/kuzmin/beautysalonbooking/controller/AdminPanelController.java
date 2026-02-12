package by.kuzmin.beautysalonbooking.controller;

import by.kuzmin.beautysalonbooking.dto.CreateEmployeeRequestDto;
import by.kuzmin.beautysalonbooking.dto.EmployeeStatusRequestDto;
import by.kuzmin.beautysalonbooking.dto.EmployeeStatusResponseDto;
import by.kuzmin.beautysalonbooking.service.EmployeeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("admin-panel")
public class AdminPanelController {
    private final EmployeeService employeeService;

    public AdminPanelController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    public String adminPanel(Model model) {
        return "admin-panel";
    }

    @GetMapping("employees")
    public String getEmployees(Model model) {
        model.addAttribute("employeeDto", new CreateEmployeeRequestDto());
        model.addAttribute("employeeStatusDto", new EmployeeStatusRequestDto());
        model.addAttribute("section", "employees");
        return "admin-panel";
    }

    @PostMapping("employees")
    public String postEmployees(CreateEmployeeRequestDto employeeDto, Model model) {
        employeeService.createEmployee(employeeDto);

        model.addAttribute("section", "employees");
        return "redirect:/admin-panel/employees";
    }

    @PostMapping("employees-statuses")
    public String postEmployeesStatuses(EmployeeStatusRequestDto employeeStatusRequestDto, Model model) {

        employeeService.saveEmployeeStatus(employeeStatusRequestDto);

        model.addAttribute("section", "employees");
        return "redirect:/admin-panel/employees";
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
