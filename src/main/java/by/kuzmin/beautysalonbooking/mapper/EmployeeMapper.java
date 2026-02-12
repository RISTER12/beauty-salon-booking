package by.kuzmin.beautysalonbooking.mapper;

import by.kuzmin.beautysalonbooking.dto.CreateEmployeeRequestDto;
import by.kuzmin.beautysalonbooking.dto.CreateEmployeeResponseDto;
import by.kuzmin.beautysalonbooking.entity.Employee;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {
    Employee toEntity(CreateEmployeeRequestDto createEmployeeRequestDto);
    CreateEmployeeResponseDto toDto(Employee employee);
}
