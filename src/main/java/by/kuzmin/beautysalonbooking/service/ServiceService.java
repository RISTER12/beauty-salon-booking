package by.kuzmin.beautysalonbooking.service;

import by.kuzmin.beautysalonbooking.dto.ServiceResponseDto;
import by.kuzmin.beautysalonbooking.dto.ServiceWithCategoryDto;
import by.kuzmin.beautysalonbooking.entity.ServiceCategory;
import by.kuzmin.beautysalonbooking.entity.ServiceEntity;
import by.kuzmin.beautysalonbooking.mapper.ServiceCategoryMapper;
import by.kuzmin.beautysalonbooking.mapper.ServiceMapper;
import by.kuzmin.beautysalonbooking.repository.ServiceCategoryRepository;
import by.kuzmin.beautysalonbooking.repository.ServiceRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor
public class ServiceService {
    ServiceRepository serviceRepository;
    ServiceMapper serviceMapper;
    ServiceCategoryMapper serviceCategoryMapper;

    public List<ServiceResponseDto> findAll() {
        return serviceRepository.findAll()
                .stream()
                .map(service -> serviceMapper.toDto(service))
                .toList();
    }

    public List<ServiceWithCategoryDto> findAllWithCategoryDto() {
        Map<ServiceCategory, List<ServiceEntity>> serviceGroupByCategory = serviceRepository.findAll().stream()
                .collect(Collectors.groupingBy(ServiceEntity::getServiceCategory));

        return serviceGroupByCategory.entrySet().stream()
                .map(entry -> new ServiceWithCategoryDto(
                        serviceCategoryMapper.toDto(entry.getKey()),
                        entry.getValue().stream()
                                .map(serviceMapper::toDto)
                                .toList()
                ))
                .toList();
    }

    public List<ServiceResponseDto> findAllByIds(List<Long> ids) {
        return serviceRepository.findAllById(ids).stream()
                .map(serviceMapper::toDto)
                .toList();
    }
}