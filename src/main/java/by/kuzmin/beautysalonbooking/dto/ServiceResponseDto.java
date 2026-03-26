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
    private Long categoryId;
    private String name;
    //todo доделать
}
