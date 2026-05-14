package by.kuzmin.beautysalonbooking.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class EmployeeStatusResponseDto {
    private Long id;
    private String statusName;
}
