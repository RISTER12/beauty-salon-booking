package by.kuzmin.beautysalonbooking.controller;

import by.kuzmin.beautysalonbooking.dto.CreateEmployeeRequestDto;
import by.kuzmin.beautysalonbooking.dto.EmployeeStatusRequestDto;
import by.kuzmin.beautysalonbooking.service.EmployeeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping("/admin-panel")
public class AdminPanelController {
    private final EmployeeService employeeService;

    public AdminPanelController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    public String adminPanel(Model model) {
        return "admin-panel";
    }

    @GetMapping("/employees")
    public String getEmployees(Model model) {
        model.addAttribute("employees", employeeService.getEmployees());
        model.addAttribute("section", "employees");
        return "admin-panel";
    }


    @GetMapping("/employees/editMode")
    public String editEmployees(Model model) {
        model.addAttribute("employees", employeeService.getEmployees());
        model.addAttribute("section", "employees");
        model.addAttribute("subsection", "editMode");
        return "admin-panel";
    }

    @GetMapping("/employees/editMode/add")
    public String addEmployee(Model model) {
        model.addAttribute("employees", employeeService.getEmployees());
        model.addAttribute("section", "employees");
        model.addAttribute("subsection", "editMode");
        model.addAttribute("add", "true");
        return "admin-panel";
    }

    @GetMapping("/employees/editMode/{editId}")
    public String editEmployee(@PathVariable Long editId,
                                Model model) {
        model.addAttribute("employees", employeeService.getEmployees());
        model.addAttribute("editId", editId);
        model.addAttribute("employeeDto", new CreateEmployeeRequestDto());
        model.addAttribute("section", "employees");
        model.addAttribute("subsection", "editMode");
        return "/admin-panel";
    }

    @PostMapping("/employees/editMode/{editId}")
    String saveEditedEmployee(@PathVariable Long editId,
                                 @RequestParam String firstName,
                                 @RequestParam String lastName,
                                 @RequestParam(required = false) String middleName) {
        CreateEmployeeRequestDto createEmployeeRequestDto = new CreateEmployeeRequestDto();
        createEmployeeRequestDto.setId(editId);
        createEmployeeRequestDto.setFirstName(firstName);
        createEmployeeRequestDto.setLastName(lastName);
        createEmployeeRequestDto.setMiddleName(middleName);
        employeeService.createEmployee(createEmployeeRequestDto);
        return "redirect:/admin-panel/employees/editMode";
    }

    @PostMapping("/employees/save")
    public String postEmployees(CreateEmployeeRequestDto employeeDto) {
        employeeService.createEmployee(employeeDto);
        return "redirect:/admin-panel/employees/editMode";
    }

    @PostMapping("/employees/delete/{id}")
    public String deleteEmployees(@PathVariable Long id) {
        employeeService.deleteEmployeeById(id);
        return "redirect:/admin-panel/employees/editMode";
    }

    @PostMapping("/employees-statuses")
    public String postEmployeesStatuses(EmployeeStatusRequestDto employeeStatusRequestDto, Model model) {
        employeeService.saveEmployeeStatus(employeeStatusRequestDto);
        model.addAttribute("section", "employees");
        return "redirect:/admin-panel/employees";
    }

    @GetMapping("/salons")
    public String salons(Model model) {
        model.addAttribute("section", "salons");
        return "admin-panel";
    }

    @GetMapping("/services")
    public String services(Model model) {
        model.addAttribute("section", "services");
        return "admin-panel";
    }
}
