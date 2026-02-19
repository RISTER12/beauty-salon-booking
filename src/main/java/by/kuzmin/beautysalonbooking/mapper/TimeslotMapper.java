package by.kuzmin.beautysalonbooking.mapper;

import by.kuzmin.beautysalonbooking.dto.TimeslotDto;
import by.kuzmin.beautysalonbooking.entity.Timeslot;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TimeslotMapper {
    Timeslot toEntity(TimeslotDto timeslotDto);
    TimeslotDto toDto(Timeslot timeslot);
}
