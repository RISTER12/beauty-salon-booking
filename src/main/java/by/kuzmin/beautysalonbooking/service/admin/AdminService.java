package by.kuzmin.beautysalonbooking.service.admin;

import by.kuzmin.beautysalonbooking.dto.admin.*;
import by.kuzmin.beautysalonbooking.entity.*;
import by.kuzmin.beautysalonbooking.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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
    private final SalonRepository salonRepository;
    private final EmployeeStatusRepository employeeStatusRepository;

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


    @Transactional(readOnly = true)
    public List<EmployeeResponseDto> getAllEmployees() {
        List<Employee> employees = employeeRepository.findByIsActiveTrue();
        return employees.stream()
                .map(this::toEmployeeResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EmployeeResponseDto getEmployeeById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Мастер не найден с id: " + id));
        return toEmployeeResponseDto(employee);
    }

    @Transactional
    public EmployeeResponseDto createEmployee(EmployeeRequestDto dto) {
        Employee employee = new Employee();

        // Основные поля
        employee.setFirstName(dto.getFirstName());
        employee.setLastName(dto.getLastName());
        employee.setMiddleName(dto.getMiddleName());
        employee.setJobTitle(dto.getJobTitle());
        employee.setExperienceYears(dto.getExperienceYears() != null ? dto.getExperienceYears() : 0L);

        // Обязательные поля с значениями по умолчанию
        employee.setVisibleOnWebsite(dto.getIsVisibleOnWebsite() != null ? dto.getIsVisibleOnWebsite() : true);
        employee.setActive(true);  // is_active = true

        // Рейтинг по умолчанию
        if (dto.getAverageRating() != null) {
            employee.setAverageRating(dto.getAverageRating());
        } else {
            employee.setAverageRating(BigDecimal.ZERO);
        }

        // Установка статуса (если не указан, берём статус "ACTIVE" с id=1)
        if (dto.getStatusId() != null) {
            EmployeeStatus status = employeeStatusRepository.findById(dto.getStatusId())
                    .orElseThrow(() -> new RuntimeException("Статус не найден"));
            employee.setEmployeeStatus(status);
        } else {
            // Статус по умолчанию (предполагаем, что есть статус с id=1)
            EmployeeStatus defaultStatus = employeeStatusRepository.findById(1L)
                    .orElseThrow(() -> new RuntimeException("Статус по умолчанию не найден"));
            employee.setEmployeeStatus(defaultStatus);
        }

        // Установка салона (если указан)
        if (dto.getSalonId() != null) {
            Salon salon = salonRepository.findById(dto.getSalonId())
                    .orElseThrow(() -> new RuntimeException("Салон не найден"));
            employee.setSalon(salon);
        }

        // Установка услуг (если указаны)
        if (dto.getServiceIds() != null && !dto.getServiceIds().isEmpty()) {
            List<ServiceEntity> services = serviceRepository.findAllById(dto.getServiceIds());
            employee.setServiceList(services);
        }

        Employee saved = employeeRepository.save(employee);
        return toEmployeeResponseDto(saved);
    }

    @Transactional
    public EmployeeResponseDto updateEmployee(Long id, EmployeeRequestDto dto) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Мастер не найден с id: " + id));

        updateEmployeeFromDto(employee, dto);

        Employee saved = employeeRepository.save(employee);
        return toEmployeeResponseDto(saved);
    }

    @Transactional
    public void deleteEmployee(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Мастер не найден с id: " + id));

        employee.setActive(false);
        employeeRepository.save(employee);
    }

    private void updateEmployeeFromDto(Employee employee, EmployeeRequestDto dto) {
        employee.setFirstName(dto.getFirstName());
        employee.setLastName(dto.getLastName());
        employee.setMiddleName(dto.getMiddleName());
        employee.setJobTitle(dto.getJobTitle());
        employee.setExperienceYears(dto.getExperienceYears());
        employee.setCertificationList(dto.getCertificationList());
        employee.setAwardList(dto.getAwardList());
        employee.setPhotoUrl(dto.getPhotoUrl());
        employee.setPortfolioPhotosUrlList(dto.getPortfolioPhotosUrlList());
        employee.setPortfolioVideoUrlList(dto.getPortfolioVideoUrlList());
        employee.setAverageRating(dto.getAverageRating());
        employee.setVisibleOnWebsite(dto.getIsVisibleOnWebsite());

        // Установка салона
        if (dto.getSalonId() != null) {
            Salon salon = salonRepository.findById(dto.getSalonId())
                    .orElseThrow(() -> new RuntimeException("Салон не найден"));
            employee.setSalon(salon);
        }

        // Установка услуг
        if (dto.getServiceIds() != null && !dto.getServiceIds().isEmpty()) {
            List<ServiceEntity> services = serviceRepository.findAllById(dto.getServiceIds());
            employee.setServiceList(services);
        }
    }

    private EmployeeResponseDto toEmployeeResponseDto(Employee employee) {
        return EmployeeResponseDto.builder()
                .id(employee.getId())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .middleName(employee.getMiddleName())
                .fullName(getEmployeeFullName(employee))
                .salonName(employee.getSalon() != null ? employee.getSalon().getSalonName() : null)
                .jobTitle(employee.getJobTitle())
                .experienceYears(employee.getExperienceYears())
                .certificationList(employee.getCertificationList())
                .awardList(employee.getAwardList())
                .photoUrl(employee.getPhotoUrl())
                .status(employee.getEmployeeStatus() != null ? employee.getEmployeeStatus().getStatusName() : "ACTIVE")
                .averageRating(employee.getAverageRating())
                .isVisibleOnWebsite(employee.isVisibleOnWebsite())
                .serviceNames(employee.getServiceList() != null ?
                        employee.getServiceList().stream()
                                .map(ServiceEntity::getName)
                                .collect(Collectors.toList()) :
                        List.of())
                .build();
    }

    private String getEmployeeFullName(Employee employee) {
        StringBuilder name = new StringBuilder();
        name.append(employee.getFirstName());
        if (employee.getMiddleName() != null && !employee.getMiddleName().isBlank()) {
            name.append(" ").append(employee.getMiddleName());
        }
        name.append(" ").append(employee.getLastName());
        return name.toString();
    }
}