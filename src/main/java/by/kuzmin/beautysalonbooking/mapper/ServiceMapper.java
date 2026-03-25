package by.kuzmin.beautysalonbooking.mapper;

import by.kuzmin.beautysalonbooking.dto.ServiceRequestDto;
import by.kuzmin.beautysalonbooking.dto.ServiceResponseDto;
import by.kuzmin.beautysalonbooking.dto.TimeslotDto;
import by.kuzmin.beautysalonbooking.entity.Service;
import by.kuzmin.beautysalonbooking.entity.Timeslot;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ServiceMapper {
    Service toEntity(ServiceRequestDto serviceDto);
    ServiceResponseDto toDto(Service service);
}
