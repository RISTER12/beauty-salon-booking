package by.kuzmin.beautysalonbooking.dto;


import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleDto {
        private LocalDate startDate;
        private Long workingDaysCount;
        private Long weekendDaysCount;
        private LocalTime startTime;
        private LocalTime endTime;
        private BigDecimal hoursPerDay;
        private Long employeeId;

}
