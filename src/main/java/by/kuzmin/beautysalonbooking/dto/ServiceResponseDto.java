package by.kuzmin.beautysalonbooking.dto;

import by.kuzmin.beautysalonbooking.entity.ServiceCategory;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Builder
public class ServiceResponseDto {
    private Long categoryId;
    private Long id;
    private String name;
    //todo доделать
}
