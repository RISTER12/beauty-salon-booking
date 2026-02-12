package by.kuzmin.beautysalonbooking.dto;

import by.kuzmin.beautysalonbooking.entity.Salon;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateEmployeeResponseDto {
    private Long id;
//    private Salon salon;
    private String firstName;
    private String lastName;
    private String middleName;
}
