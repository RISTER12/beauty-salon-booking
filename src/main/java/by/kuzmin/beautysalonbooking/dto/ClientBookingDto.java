package by.kuzmin.beautysalonbooking.dto;



import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class ClientBookingDto {
    private String name;
    private String phone;
    private Long employeeId;
    private Long slotId;
    private List<Long> serviceIds;
    private Long salonId;
}