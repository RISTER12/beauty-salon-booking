package by.kuzmin.beautysalonbooking.mapper;

import by.kuzmin.beautysalonbooking.dto.ServiceCategoryRequestDto;
import by.kuzmin.beautysalonbooking.dto.ServiceCategoryResponseDto;
import by.kuzmin.beautysalonbooking.entity.ServiceCategory;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ServiceCategoryMapper {
    ServiceCategory toEntity(ServiceCategoryRequestDto serviceCategoryRequestDto);
    ServiceCategoryResponseDto toDto(ServiceCategory service);
}
