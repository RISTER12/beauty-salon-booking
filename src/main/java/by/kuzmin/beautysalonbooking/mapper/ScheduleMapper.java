package by.kuzmin.beautysalonbooking.mapper;

import by.kuzmin.beautysalonbooking.dto.ScheduleDto;
import by.kuzmin.beautysalonbooking.entity.Schedule;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ScheduleMapper {
    Schedule  toEntity(ScheduleDto scheduleDto);
    ScheduleDto toDto(Schedule schedule);
}
