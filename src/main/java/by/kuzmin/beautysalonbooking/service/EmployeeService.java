package by.kuzmin.beautysalonbooking.service;

import by.kuzmin.beautysalonbooking.dto.CreateEmployeeRequestDto;
import by.kuzmin.beautysalonbooking.dto.CreateEmployeeResponseDto;
import by.kuzmin.beautysalonbooking.dto.EmployeeStatusRequestDto;
import by.kuzmin.beautysalonbooking.dto.EmployeeStatusResponseDto;
import by.kuzmin.beautysalonbooking.entity.Employee;
import by.kuzmin.beautysalonbooking.mapper.EmployeeMapper;
import by.kuzmin.beautysalonbooking.mapper.EmployeeStatusMapper;
import by.kuzmin.beautysalonbooking.repository.EmployeeRepository;
import by.kuzmin.beautysalonbooking.repository.EmployeeStatusRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@AllArgsConstructor
public class EmployeeService {
    EmployeeRepository employeeRepository;
    EmployeeStatusRepository employeeStatusRepository;
    EmployeeMapper employeeMapper;
    EmployeeStatusMapper employeeStatusMapper;

    public CreateEmployeeResponseDto createEmployee(CreateEmployeeRequestDto createEmployeeRequestDto) {
        Employee employee = employeeMapper.toEntity(createEmployeeRequestDto);
        employee.setEmployeeStatus(
                employeeStatusRepository.findById(3L).orElseThrow(() -> new RuntimeException("Employee status not found"))
        );
        return employeeMapper.toDto(
                employeeRepository.save(employee)
        );
    }

    public List<CreateEmployeeResponseDto> getEmployees() {
        return employeeRepository.findAllByOrderByFirstNameAsc().stream()
                .map(employeeMapper::toDto)
                .toList();
    }

    public void deleteEmployeeById(Long id) {
        employeeRepository.deleteById(id);
    }

    public EmployeeStatusResponseDto saveEmployeeStatus(EmployeeStatusRequestDto employeeStatusRequestDto) {
        return  employeeStatusMapper.toDto(
                employeeStatusRepository.save(
                        employeeStatusMapper.toEntity(employeeStatusRequestDto)
                )
        );
    }
}
