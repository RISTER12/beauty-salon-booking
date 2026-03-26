package by.kuzmin.beautysalonbooking.mapper;

import by.kuzmin.beautysalonbooking.dto.ServiceRequestDto;
import by.kuzmin.beautysalonbooking.dto.ServiceResponseDto;
import by.kuzmin.beautysalonbooking.entity.ServiceEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ServiceMapper {
    ServiceEntity toEntity(ServiceRequestDto serviceDto);
    ServiceResponseDto toDto(ServiceEntity service);
}
