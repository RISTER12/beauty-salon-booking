package by.kuzmin.beautysalonbooking.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class EmployeeAppointmentDto {
    private Long id;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String clientName;
    private String clientPhone;
    private List<String> serviceNames;
    private Double totalAmount;
    private String status;
    private String clientNote;
}