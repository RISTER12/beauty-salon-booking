package by.kuzmin.beautysalonbooking.dto;

import by.kuzmin.beautysalonbooking.entity.Salon;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class CreateEmployeeResponseDto {
    private Long id;
//    private Salon salon;
    private String firstName;
    private String lastName;
    private String middleName;
}
