package by.kuzmin.beautysalonbooking.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class MonthRequest {
    private int year;
    private int month;
    private Long employeeId;
}
