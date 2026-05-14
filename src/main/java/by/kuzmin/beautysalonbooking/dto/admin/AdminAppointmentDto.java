package by.kuzmin.beautysalonbooking.dto.admin;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@Builder
public class AdminAppointmentDto {
    private Long id;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String clientName;
    private String clientPhone;
    private String employeeName;
    private List<String> serviceNames;
    private Double totalAmount;
    private String status;
}