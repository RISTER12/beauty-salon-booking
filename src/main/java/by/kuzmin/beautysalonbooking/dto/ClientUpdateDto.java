package by.kuzmin.beautysalonbooking.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

@Data
@Builder
public class ClientUpdateDto {
    private String name;
    private String phone;
    private String email;
    private LocalDate birthDate;
}