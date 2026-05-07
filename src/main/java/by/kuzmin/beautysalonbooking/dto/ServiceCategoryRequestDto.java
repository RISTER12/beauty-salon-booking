package by.kuzmin.beautysalonbooking.dto;

import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class ServiceCategoryRequestDto {
    //todo подумать о том, чтобы сделать один dto и для запроса, и для ответа.
    private String categoryName;
}
