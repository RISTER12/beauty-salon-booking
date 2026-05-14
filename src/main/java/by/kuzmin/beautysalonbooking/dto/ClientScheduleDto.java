package by.kuzmin.beautysalonbooking.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class ClientScheduleDto {
    private String name;
    private String number;
}
