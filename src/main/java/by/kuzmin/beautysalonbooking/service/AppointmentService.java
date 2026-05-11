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
        // ✅ Исправлено: используем правильный метод с GreaterThan и OffsetDateTime
        OffsetDateTime now = OffsetDateTime.now();

        List<Appointment> appointments = appointmentRepository
                .findByClientIdAndStartTimeGreaterThanOrderByStartTimeAsc(clientId, now);

        return appointments.stream()
                .map(appointmentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void cancelAppointment(Long appointmentId, String cancellationReason, Long cancellationRoleId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Запись не найдена"));

        appointment.setCancellationReason(cancellationReason);
        appointment.setCancelledAt(OffsetDateTime.now());

        // Если есть статус "CANCELLED" — установите его
        // appointment.setAppointmentStatus(cancelledStatus);

        appointmentRepository.save(appointment);
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