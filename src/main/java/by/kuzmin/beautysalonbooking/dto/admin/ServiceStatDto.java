package by.kuzmin.beautysalonbooking.dto.admin;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ServiceStatDto {
    private Long serviceId;
    private String serviceName;
    private int bookingCount;
    private double totalRevenue;
}