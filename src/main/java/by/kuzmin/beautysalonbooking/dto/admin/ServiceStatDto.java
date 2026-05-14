package by.kuzmin.beautysalonbooking.dto.admin;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class ServiceStatDto {
    private Long serviceId;
    private String serviceName;
    private int bookingCount;
    private double totalRevenue;
}