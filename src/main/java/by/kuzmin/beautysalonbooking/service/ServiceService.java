package by.kuzmin.beautysalonbooking.service;

import by.kuzmin.beautysalonbooking.dto.ServiceResponseDto;
import by.kuzmin.beautysalonbooking.mapper.ServiceMapper;
import by.kuzmin.beautysalonbooking.repository.ServiceRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ServiceService {
    ServiceRepository serviceRepository;
    ServiceMapper serviceMapper;
    public List<ServiceResponseDto> findAll() {
        return serviceRepository.findAll()
                        .stream()
                        .map(service -> serviceMapper.toDto(service))
                        .toList();
    }

}
