package by.kuzmin.beautysalonbooking.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class EmployeeStatusRequestDto {
    private String statusName;
}
