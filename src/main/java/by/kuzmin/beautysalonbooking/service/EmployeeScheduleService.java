package by.kuzmin.beautysalonbooking.service;

import by.kuzmin.beautysalonbooking.dto.EmployeeAppointmentDto;
import by.kuzmin.beautysalonbooking.entity.Appointment;
import by.kuzmin.beautysalonbooking.entity.Client;
import by.kuzmin.beautysalonbooking.entity.Timeslot;
import by.kuzmin.beautysalonbooking.repository.AppointmentRepository;
import by.kuzmin.beautysalonbooking.repository.EmployeeRepository;
import by.kuzmin.beautysalonbooking.repository.TimeslotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeScheduleService {

    private final AppointmentRepository appointmentRepository;
    private final TimeslotRepository timeslotRepository;
    private final EmployeeRepository employeeRepository;



    @Transactional(readOnly = true)
    public List<EmployeeAppointmentDto> getAppointmentsByDate(Long employeeId, LocalDate date) {
        List<Timeslot> timeslots = timeslotRepository.findByEmployeeIdAndSlotDate(employeeId, date);

        return timeslots.stream()
                .filter(t -> t.getAppointment() != null)
                .map(t -> toDto(t.getAppointment()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EmployeeAppointmentDto> getUpcomingAppointments(Long employeeId, LocalDate fromDate) {
        List<Timeslot> timeslots = timeslotRepository.findByEmployeeIdAndSlotDateGreaterThanEqual(employeeId, fromDate);

        return timeslots.stream()
                .filter(t -> t.getAppointment() != null)
                .filter(t -> !"CANCELLED".equals(t.getAppointment().getAppointmentStatus().getStatusName()))
                .map(t -> toDto(t.getAppointment()))
                .limit(10) // Только ближайшие 10
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EmployeeAppointmentDto> getAppointmentsByMonth(Long employeeId, int year, int month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1);

        List<Timeslot> timeslots = timeslotRepository.findByEmployeeIdAndSlotDateBetween(employeeId, startDate, endDate);

        return timeslots.stream()
                .filter(t -> t.getAppointment() != null)
                .map(t -> toDto(t.getAppointment()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EmployeeAppointmentDto getAppointmentDetail(Long employeeId, Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Запись не найдена"));

        // Проверяем, что запись принадлежит этому мастеру
        boolean belongsToEmployee = appointment.getTimeslotList().stream()
                .anyMatch(t -> t.getEmployee().getId().equals(employeeId));

        if (!belongsToEmployee) {
            throw new RuntimeException("Доступ запрещён");
        }

        return toDto(appointment);
    }

    @Transactional
    public void markAsCompleted(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Запись не найдена"));

        appointment.setCompletedAt(java.time.OffsetDateTime.now());
        // Если есть статус COMPLETED
        // appointment.setAppointmentStatus(completedStatus);

        appointmentRepository.save(appointment);
    }

    private EmployeeAppointmentDto toDto(Appointment appointment) {
        return EmployeeAppointmentDto.builder()
                .id(appointment.getId())
                .date(appointment.getStartTime().toLocalDate())
                .startTime(appointment.getStartTime().toLocalTime())
                .endTime(appointment.getEndTime().toLocalTime())
                .clientName(getClientFullName(appointment.getClient()))
                .clientPhone(appointment.getClient().getPhone())
                .serviceNames(appointment.getServiceList().stream()
                        .map(s -> s.getName())
                        .collect(Collectors.toList()))
                .totalAmount(appointment.getFinalAmount().doubleValue())
                .status(appointment.getAppointmentStatus().getStatusName())
                .clientNote(appointment.getClientNote())
                .build();
    }

    private String getClientFullName(Client client) {
        StringBuilder name = new StringBuilder();
        name.append(client.getFirstName());
        if (client.getLastName() != null && !client.getLastName().isEmpty()) {
            name.append(" ").append(client.getLastName());
        }
        return name.toString();
    }
}