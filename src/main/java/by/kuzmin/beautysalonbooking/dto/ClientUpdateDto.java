package by.kuzmin.beautysalonbooking.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class ClientUpdateDto {
    private String name;
    private String phone;
    private String email;
    private LocalDate birthDate;
}