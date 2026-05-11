package by.kuzmin.beautysalonbooking.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@Builder
public class AppointmentDto {
    private Long id;
    private OffsetDateTime dateTime;
    private String status;
    private String serviceName;
    private BigDecimal servicePrice;
    private String employeeName;
    private String salonAddress;
    private BigDecimal  finalAmount;      // новое поле из сущности
    private String clientNote;        // можно добавить, если нужно
    private String employeeNote;      // можно добавить, если нужно
}