package by.kuzmin.beautysalonbooking.dto.admin;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class EmployeeWorkloadDto {
    private Long employeeId;
    private String employeeName;
    private int totalAppointments;
    private double totalHours;
    private double totalEarnings;
    private double occupancyRate;
}