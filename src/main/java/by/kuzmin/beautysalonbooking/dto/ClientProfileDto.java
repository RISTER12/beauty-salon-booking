package by.kuzmin.beautysalonbooking.dto;


import lombok.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class ClientProfileDto {
    private Long id;
    private String name;
    private String phone;
    private String email;
    private LocalDate birthDate;
    private OffsetDateTime createdAt;
    private Long statusId;
    private Integer loyaltyPoints;
}