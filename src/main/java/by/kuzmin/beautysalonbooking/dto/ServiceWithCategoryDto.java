package by.kuzmin.beautysalonbooking.dto;

import by.kuzmin.beautysalonbooking.dto.admin.ServiceResponseDto;
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
