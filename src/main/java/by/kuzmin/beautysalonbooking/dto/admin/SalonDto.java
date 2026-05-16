package by.kuzmin.beautysalonbooking.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalonDto {
    private Long id;
    private String salonName;
    private String address;
    private String phone;
    private String email;
}