package by.kuzmin.beautysalonbooking.dto.admin;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmployeeWorkloadDto {
    private Long employeeId;
    private String employeeName;
    private int totalAppointments;
    private double totalHours;
    private double totalEarnings;
    private double occupancyRate;
}