package by.kuzmin.beautysalonbooking.service.admin;

import by.kuzmin.beautysalonbooking.dto.admin.AdminAppointmentDto;
import by.kuzmin.beautysalonbooking.dto.admin.EmployeeWorkloadDto;
import by.kuzmin.beautysalonbooking.dto.admin.ServiceStatDto;
import by.kuzmin.beautysalonbooking.entity.*;
import by.kuzmin.beautysalonbooking.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AppointmentRepository appointmentRepository;
    private final EmployeeRepository employeeRepository;
    private final ServiceRepository serviceRepository;
    private final TimeslotRepository timeslotRepository;
    private final ClientRepository clientRepository;

    // Получаем фиксированный ZoneOffset (например, UTC+4 для Москвы)
    private static final ZoneOffset DEFAULT_OFFSET = ZoneOffset.ofHours(4);

    @Transactional(readOnly = true)
    public List<AdminAppointmentDto> getAllAppointments(LocalDate startDate, LocalDate endDate) {
        OffsetDateTime start = startDate.atStartOfDay().atOffset(DEFAULT_OFFSET);
        OffsetDateTime end = endDate.plusDays(1).atStartOfDay().atOffset(DEFAULT_OFFSET);

        List<Appointment> appointments = appointmentRepository.findByStartTimeBetweenOrderByStartTimeAsc(start, end);

        return appointments.stream()
                .map(this::toAdminDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AdminAppointmentDto> getAppointmentsByDate(LocalDate date) {
        OffsetDateTime start = date.atStartOfDay().atOffset(DEFAULT_OFFSET);
        OffsetDateTime end = date.plusDays(1).atStartOfDay().atOffset(DEFAULT_OFFSET);

        List<Appointment> appointments = appointmentRepository.findByStartTimeBetweenOrderByStartTimeAsc(start, end);

        return appointments.stream()
                .map(this::toAdminDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void cancelAppointment(Long appointmentId, String reason) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Запись не найдена"));

        // Получаем статус CANCELLED (нужно добавить метод в репозиторий)
        // AppointmentStatus cancelledStatus = appointmentStatusRepository.findByName("CANCELLED")
        //     .orElseThrow(() -> new RuntimeException("Статус CANCELLED не найден"));
        // appointment.setAppointmentStatus(cancelledStatus);

        appointment.setCancellationReason(reason);
        appointment.setCancelledAt(OffsetDateTime.now(DEFAULT_OFFSET));

        appointmentRepository.save(appointment);
    }

    @Transactional(readOnly = true)
    public List<EmployeeWorkloadDto> getEmployeeWorkload(LocalDate startDate, LocalDate endDate) {
        OffsetDateTime start = startDate.atStartOfDay().atOffset(DEFAULT_OFFSET);
        OffsetDateTime end = endDate.plusDays(1).atStartOfDay().atOffset(DEFAULT_OFFSET);

        List<Employee> employees = employeeRepository.findAll();

        return employees.stream()
                .map(emp -> {
                    List<Appointment> appointments = appointmentRepository
                            .findByEmployeeIdAndStartTimeBetween(emp.getId(), start, end);

                    int totalAppointments = appointments.size();
                    double totalHours = appointments.stream()
                            .mapToLong(a -> ChronoUnit.MINUTES.between(a.getStartTime(), a.getEndTime()))
                            .sum() / 60.0;
                    double totalEarnings = appointments.stream()
                            .mapToDouble(a -> a.getFinalAmount().doubleValue())
                            .sum();

                    int workDays = (int) ChronoUnit.DAYS.between(startDate, endDate) + 1;
                    double maxHours = workDays * 8.0;
                    double occupancyRate = maxHours > 0 ? (totalHours / maxHours) * 100 : 0;

                    return EmployeeWorkloadDto.builder()
                            .employeeId(emp.getId())
                            .employeeName(emp.getFirstName() + " " + emp.getLastName())
                            .totalAppointments(totalAppointments)
                            .totalHours(Math.round(totalHours * 10) / 10.0)
                            .totalEarnings(Math.round(totalEarnings))
                            .occupancyRate(Math.round(occupancyRate))
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ServiceStatDto> getServiceStats(LocalDate startDate, LocalDate endDate) {
        OffsetDateTime start = startDate.atStartOfDay().atOffset(DEFAULT_OFFSET);
        OffsetDateTime end = endDate.plusDays(1).atStartOfDay().atOffset(DEFAULT_OFFSET);

        List<ServiceEntity> services = serviceRepository.findAll();

        return services.stream()
                .map(service -> {
                    int bookingCount = appointmentRepository.countByServiceIdAndStartTimeBetween(
                            service.getId(), start, end);

                    double revenue = appointmentRepository.sumRevenueByServiceIdAndStartTimeBetween(
                            service.getId(), start, end);

                    return ServiceStatDto.builder()
                            .serviceId(service.getId())
                            .serviceName(service.getName())
                            .bookingCount(bookingCount)
                            .totalRevenue(revenue)
                            .build();
                })
                .collect(Collectors.toList());
    }

    private AdminAppointmentDto toAdminDto(Appointment appointment) {
        return AdminAppointmentDto.builder()
                .id(appointment.getId())
                .date(appointment.getStartTime().toLocalDate())
                .startTime(appointment.getStartTime().toLocalTime())
                .endTime(appointment.getEndTime().toLocalTime())
                .clientName(getClientFullName(appointment.getClient()))
                .clientPhone(appointment.getClient().getPhone())
                .employeeName(getEmployeeName(appointment))
                .serviceNames(appointment.getServiceList().stream()
                        .map(ServiceEntity::getName)
                        .collect(Collectors.toList()))
                .totalAmount(appointment.getFinalAmount().doubleValue())
                .status(appointment.getAppointmentStatus().getStatusName())
                .build();
    }

    private String getClientFullName(Client client) {
        return client.getFirstName() + " " + client.getLastName();
    }

    private String getEmployeeName(Appointment appointment) {
        if (appointment.getTimeslotList() != null && !appointment.getTimeslotList().isEmpty()) {
            Timeslot timeslot = appointment.getTimeslotList().get(0);
            if (timeslot.getEmployee() != null) {
                return timeslot.getEmployee().getFirstName() + " " + timeslot.getEmployee().getLastName();
            }
        }
        return "Не назначен";
    }
}