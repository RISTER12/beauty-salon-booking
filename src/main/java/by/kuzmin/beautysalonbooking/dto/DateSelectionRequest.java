package by.kuzmin.beautysalonbooking.dto;

import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class DateSelectionRequest {
    private int year;
    private int month;
    private int day;
    private String dateString;
    private Long employeeId;
}
