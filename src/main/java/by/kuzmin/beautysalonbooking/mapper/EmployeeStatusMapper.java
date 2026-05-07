package by.kuzmin.beautysalonbooking.mapper;

import by.kuzmin.beautysalonbooking.dto.EmployeeStatusRequestDto;
import by.kuzmin.beautysalonbooking.dto.EmployeeStatusResponseDto;
import by.kuzmin.beautysalonbooking.entity.Employee;
import by.kuzmin.beautysalonbooking.entity.EmployeeStatus;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EmployeeStatusMapper {
    EmployeeStatus toEntity(EmployeeStatusRequestDto employeeStatusRequestDto);
    EmployeeStatusResponseDto toDto(EmployeeStatus employeeStatus);

}
