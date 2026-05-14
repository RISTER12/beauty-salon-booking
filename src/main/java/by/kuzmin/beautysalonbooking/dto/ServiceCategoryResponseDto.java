package by.kuzmin.beautysalonbooking.dto;

import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceCategoryResponseDto {
    private Long id;
    private String categoryName;
}
