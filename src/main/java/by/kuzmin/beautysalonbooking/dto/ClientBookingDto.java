package by.kuzmin.beautysalonbooking.dto;



import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ClientBookingDto {
    private String name;
    private String phone;
    private Long employeeId;
    private Long slotId;
    private List<Long> serviceIds;
}