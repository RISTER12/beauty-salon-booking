package by.kuzmin.beautysalonbooking.service;

import by.kuzmin.beautysalonbooking.dto.AppointmentDto;
import by.kuzmin.beautysalonbooking.entity.Appointment;
import by.kuzmin.beautysalonbooking.mapper.AppointmentMapper;
import by.kuzmin.beautysalonbooking.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final AppointmentMapper appointmentMapper;

    @Transactional(readOnly = true)
    public List<AppointmentDto> getUpcomingAppointments(Long clientId, Long salonId) {
        OffsetDateTime now = OffsetDateTime.now();

        List<Appointment> appointments = appointmentRepository
                .findUpcomingActiveByClientId(clientId, now);

        return appointments.stream()
                .map(appointmentMapper::toDto)
                .collect(Collectors.toList());
    }

    // В AppointmentService
    @Transactional(readOnly = true)
    public long getPendingCount(Long clientId, Long salonId) {
        OffsetDateTime now = OffsetDateTime.now();
        // ✅ Используем существующий метод countPending
        return appointmentRepository.countPending(clientId, "PENDING", now);
    }

    // ✅ ДОБАВЬТЕ ЭТОТ МЕТОД
    @Transactional(readOnly = true)
    public List<AppointmentDto> getHistoryAppointments(Long clientId, Long salonId) {
        OffsetDateTime now = OffsetDateTime.now();

        List<Appointment> appointments = appointmentRepository
                .findByClientIdAndStartTimeLessThanOrderByStartTimeDesc(clientId, now);

        return appointments.stream()
                .map(appointmentMapper::toDto)
                .collect(Collectors.toList());
    }
}