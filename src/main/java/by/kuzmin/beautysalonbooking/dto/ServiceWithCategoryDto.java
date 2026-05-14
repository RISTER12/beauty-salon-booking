package by.kuzmin.beautysalonbooking.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceWithCategoryDto {
    private ServiceCategoryResponseDto serviceCategoryResponseDto;
    private List<ServiceResponseDto> serviceResponseDto;
}
