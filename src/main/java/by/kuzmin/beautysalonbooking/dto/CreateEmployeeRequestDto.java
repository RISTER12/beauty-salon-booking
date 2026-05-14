package by.kuzmin.beautysalonbooking.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class CreateEmployeeRequestDto {
    //    private Salon salon;
    private Long id;
    private String firstName;
    private String lastName;
    private String middleName;


    private String jobTitle = "Test";
    private Long experienceYears = 1L;
    private Long employeeStatusId;
}
