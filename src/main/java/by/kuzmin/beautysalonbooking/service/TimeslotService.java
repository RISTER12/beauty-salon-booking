package by.kuzmin.beautysalonbooking.service;

import by.kuzmin.beautysalonbooking.dto.TimeslotDto;
import by.kuzmin.beautysalonbooking.mapper.TimeslotMapper;
import by.kuzmin.beautysalonbooking.repository.TimeslotRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public class TimeslotService {
    private TimeslotRepository timeslotRepository;
    private TimeslotMapper timeslotMapper;

    public List<TimeslotDto> findAllByMonth(int year, int month, Long freeStatusId) {
        return timeslotRepository.findAllByMonth(year, month, freeStatusId).stream()
                .map(timeslot -> timeslotMapper.toDto(timeslot))
                .toList();
    }

    public List<TimeslotDto> findAllByDay(LocalDate date, Long freeStatusId) {
        return timeslotRepository.findAllByDay(date, freeStatusId).stream()
                .map(timeslot -> timeslotMapper.toDto(timeslot))
                .toList();
    }

    public List<TimeslotDto> findAllByMonthAndEmployeeId(Long employeeId,
                                                         int year,
                                                         int month,
                                                         Long freeStatusId) {
        return timeslotRepository.findAllByMonthAndEmployeeId(employeeId, year, month, freeStatusId).stream()
                .map(timeslot -> timeslotMapper.toDto(timeslot))
                .toList();
    }

    public List<TimeslotDto> findAllByDayAndEmployeeId(Long employeeId,
                                                       LocalDate date,
                                                       Long freeStatusId) {
        return timeslotRepository.findAllByDayAndEmployeeId(employeeId, date, freeStatusId).stream()
                .map(timeslot -> timeslotMapper.toDto(timeslot))
                .toList();
    }
}
