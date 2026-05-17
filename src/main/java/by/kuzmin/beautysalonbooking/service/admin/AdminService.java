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
    private final ServiceCategoryRepository serviceCategoryRepository;

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

        if (dto.getSalonId() == null) {
            throw new RuntimeException("Salon ID is required");
        }

        Salon salon = salonRepository.findById(dto.getSalonId())
                .orElseThrow(() -> new RuntimeException("Салон не найден с id: " + dto.getSalonId()));
        employee.setSalon(salon);

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
            Salon salon1 = salonRepository.findById(dto.getSalonId())
                    .orElseThrow(() -> new RuntimeException("Салон не найден"));
            employee.setSalon(salon1);
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

    @Transactional(readOnly = true)
    public List<ServiceResponseDto> getAllServices() {
        List<ServiceEntity> services = serviceRepository.findAll();
        return services.stream()
                .map(this::toServiceResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ServiceResponseDto getServiceById(Long id) {
        ServiceEntity service = serviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Услуга не найдена с id: " + id));
        return toServiceResponseDto(service);
    }

    @Transactional
    public ServiceResponseDto createService(ServiceRequestDto dto) {
        ServiceEntity service = new ServiceEntity();
        updateServiceFromDto(service, dto);

        ServiceEntity saved = serviceRepository.save(service);
        return toServiceResponseDto(saved);
    }

    @Transactional
    public ServiceResponseDto updateService(Long id, ServiceRequestDto dto) {
        ServiceEntity service = serviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Услуга не найдена с id: " + id));

        updateServiceFromDto(service, dto);

        ServiceEntity saved = serviceRepository.save(service);
        return toServiceResponseDto(saved);
    }

    @Transactional
    public void deleteService(Long id) {
        ServiceEntity service = serviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Услуга не найдена с id: " + id));
        serviceRepository.delete(service);
    }

    private void updateServiceFromDto(ServiceEntity service, ServiceRequestDto dto) {
        service.setName(dto.getName());
        service.setDescription(dto.getDescription());
        service.setPrice(dto.getPrice());
        service.setPhotoUrlList(dto.getPhotoUrlList());

        // Установка категории
        if (dto.getCategoryId() != null) {
            ServiceCategory category = serviceCategoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Категория не найдена"));
            service.setServiceCategory(category);
        }

        // Установка салона
        if (dto.getSalonId() != null) {
            Salon salon = salonRepository.findById(dto.getSalonId())
                    .orElseThrow(() -> new RuntimeException("Салон не найден"));
            service.setSalon(salon);
        }

        // Установка сотрудников
        if (dto.getEmployeeIds() != null && !dto.getEmployeeIds().isEmpty()) {
            List<Employee> employees = employeeRepository.findAllById(dto.getEmployeeIds());
            service.setEmployeeList(employees);
        }
    }

    private ServiceResponseDto toServiceResponseDto(ServiceEntity service) {
        return ServiceResponseDto.builder()
                .id(service.getId())
                .name(service.getName())
                .description(service.getDescription())
                .price(service.getPrice())
                .categoryName(service.getServiceCategory() != null ?
                        service.getServiceCategory().getCategoryName() : null)
                .salonName(service.getSalon() != null ? service.getSalon().getSalonName() : null)
                .photoUrlList(service.getPhotoUrlList())
                .employeeNames(service.getEmployeeList() != null ?
                        service.getEmployeeList().stream()
                                .map(e -> e.getFirstName() + " " + e.getLastName())
                                .collect(Collectors.toList()) : List.of())
                .isActive(true)
                .build();
    }

    @Transactional(readOnly = true)
    public List<ServiceCategoryDto> getAllCategories() {
        List<ServiceCategory> categories = serviceCategoryRepository.findAll();
        return categories.stream()
                .map(this::toCategoryDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ServiceCategoryDto getCategoryById(Long id) {
        ServiceCategory category = serviceCategoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Категория не найдена с id: " + id));
        return toCategoryDto(category);
    }

    @Transactional
    public ServiceCategoryDto createCategory(ServiceCategoryDto dto) {
        ServiceCategory category = new ServiceCategory();
        category.setCategoryName(dto.getCategoryName());
        category.setDescription(dto.getDescription());
        category.setActive(true);

        ServiceCategory saved = serviceCategoryRepository.save(category);
        return toCategoryDto(saved);
    }

    @Transactional
    public ServiceCategoryDto updateCategory(Long id, ServiceCategoryDto dto) {
        ServiceCategory category = serviceCategoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Категория не найдена с id: " + id));

        category.setCategoryName(dto.getCategoryName());
        category.setDescription(dto.getDescription());

        ServiceCategory saved = serviceCategoryRepository.save(category);
        return toCategoryDto(saved);
    }

    @Transactional
    public void deleteCategory(Long id) {
        ServiceCategory category = serviceCategoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Категория не найдена с id: " + id));
        category.setActive(false);  // мягкое удаление
        serviceCategoryRepository.save(category);
    }

    private ServiceCategoryDto toCategoryDto(ServiceCategory category) {
        return ServiceCategoryDto.builder()
                .id(category.getId())
                .categoryName(category.getCategoryName())
                .description(category.getDescription())
                .active(category.getActive())
                .build();
    }

    @Transactional(readOnly = true)
    public List<ServiceResponseDto> getServicesBySalon(Long salonId) {
        List<ServiceEntity> services;
        if (salonId == null) {
            services = serviceRepository.findAll();  // все салоны
        } else {
            services = serviceRepository.findBySalonId(salonId);
        }
        return services.stream()
                .map(this::toServiceResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EmployeeResponseDto> getEmployeesBySalon(Long salonId) {
        List<Employee> employees;
        if (salonId == null) {
            employees = employeeRepository.findByIsActiveTrue();
        } else {
            employees = employeeRepository.findBySalonIdAndIsActiveTrue(salonId);
        }
        System.out.println("Found employees for salon " + salonId + ": " + employees.size());
        return employees.stream()
                .map(this::toEmployeeResponseDto)
                .collect(Collectors.toList());
    }

    public SalonDto toSalonDto(Salon salon) {
        String fullAddress = null;
        String shortAddress = null;

        if (salon.getAddress() != null) {
            StringBuilder addr = new StringBuilder();
            if (salon.getAddress().getCity() != null && !salon.getAddress().getCity().isEmpty()) {
                addr.append(salon.getAddress().getCity());
            }
            if (salon.getAddress().getStreet() != null && !salon.getAddress().getStreet().isEmpty()) {
                if (addr.length() > 0) addr.append(", ");
                addr.append(salon.getAddress().getStreet());
            }
            if (salon.getAddress().getBuilding() != null && !salon.getAddress().getBuilding().isEmpty()) {
                if (addr.length() > 0) addr.append(" ");
                addr.append(salon.getAddress().getBuilding());
            }
            fullAddress = addr.toString();

            // Короткий адрес для компактного отображения
            shortAddress = (salon.getAddress().getStreet() != null ? salon.getAddress().getStreet() : "") +
                    " " + (salon.getAddress().getBuilding() != null ? salon.getAddress().getBuilding() : "");
        }

        return SalonDto.builder()
                .id(salon.getId())
                .salonName(salon.getSalonName())
                .address(shortAddress != null && !shortAddress.isBlank() ? shortAddress : null)
                .fullAddress(fullAddress)
                .build();
    }

    @Transactional(readOnly = true)
    public SalonDto getCurrentSalon(Long salonId) {
        if (salonId == null) {
            return null;
        }
        Salon salon = salonRepository.findById(salonId)
                .orElseThrow(() -> new RuntimeException("Салон не найден с id: " + salonId));
        return toSalonDto(salon);
    }

    @Transactional(readOnly = true)
    public List<SalonDto> getAllSalons() {
        List<Salon> salons = salonRepository.findAll();
        return salons.stream()
                .map(this::toSalonDto)
                .collect(Collectors.toList());
    }


}