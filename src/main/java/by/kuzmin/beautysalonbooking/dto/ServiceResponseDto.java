package by.kuzmin.beautysalonbooking.dto;

import by.kuzmin.beautysalonbooking.entity.ServiceCategory;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class ServiceResponseDto {
    ServiceCategory serviceCategory;
    private String name;
    //todo доделать после изменения структуры бд
}
