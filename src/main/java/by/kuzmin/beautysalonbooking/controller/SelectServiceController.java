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

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/booking/select-service")
@AllArgsConstructor
public class SelectServiceController {
    ServiceService serviceService;
    @GetMapping
    public String selectService(Model model) {

        //todo удалить всё и раскоментировать
        ServiceResponseDto serviceResponseDto1 = new ServiceResponseDto(1L, 1L, "service1");
        ServiceResponseDto serviceResponseDto2 = new ServiceResponseDto(1L, 2L, "service2");
        ServiceWithCategoryDto serviceWithCategoryDto1 = new ServiceWithCategoryDto(
                new ServiceCategoryResponseDto(1L, "Category1"),
                new ArrayList<ServiceResponseDto>() {
                    {
                        add(serviceResponseDto1);
                        add(serviceResponseDto2);
                    }
                }
        );

        ServiceWithCategoryDto serviceWithCategoryDto2 = new ServiceWithCategoryDto(
                new ServiceCategoryResponseDto(1L, "Category2"),
                new ArrayList<ServiceResponseDto>() {
                    {
                        add(serviceResponseDto1);
                        add(serviceResponseDto2);
                    }
                }
        );
        List<ServiceWithCategoryDto> serviceWithCategoryDtoList = new ArrayList<>();
        serviceWithCategoryDtoList.add(serviceWithCategoryDto1);
        serviceWithCategoryDtoList.add(serviceWithCategoryDto2);
        model.addAttribute("servicesGroupByCategory", serviceWithCategoryDtoList);
        //model.addAttribute("servicesGroupByCategory", serviceService.findAllWithCategoryDto());
        return "online-booking-select-service";
    }
}
