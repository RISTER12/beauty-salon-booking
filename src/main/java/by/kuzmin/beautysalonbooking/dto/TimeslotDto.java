package by.kuzmin.beautysalonbooking.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class TimeslotDto {
    private Long id;
    private Long employeeId;
    private Long salonId;
    private LocalDate slotDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private Long durationMinutes;
    private Long timeslotStatusId;
    private Long appointmentId;
}
