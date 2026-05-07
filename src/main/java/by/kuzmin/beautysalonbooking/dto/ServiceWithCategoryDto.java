package by.kuzmin.beautysalonbooking.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class ServiceWithCategoryDto {
    private ServiceCategoryResponseDto serviceCategoryResponseDto;
    private List<ServiceResponseDto> serviceResponseDto;
}
