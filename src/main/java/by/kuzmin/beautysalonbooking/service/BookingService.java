package by.kuzmin.beautysalonbooking.service;

import by.kuzmin.beautysalonbooking.dto.ClientBookingDto;
import by.kuzmin.beautysalonbooking.entity.*;
import by.kuzmin.beautysalonbooking.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final ClientRepository clientRepository;
    private final AppointmentRepository appointmentRepository;
    private final TimeslotRepository timeslotRepository;
    private final ServiceRepository serviceRepository;
    private final AppointmentStatusRepository appointmentStatusRepository;
    private final TimeslotStatusRepository timeslotStatusRepository;

    @Transactional
    public void createBooking(ClientBookingDto bookingDto) {
        // 1. Найти или создать клиента
        Client client = clientRepository.findByPhone(bookingDto.getPhone())
                .orElseGet(() -> createNewClient(bookingDto));

        // 2. Найти временной слот
        Timeslot timeslot = timeslotRepository.findById(bookingDto.getSlotId())
                .orElseThrow(() -> new RuntimeException("Слот не найден"));

        // 3. Найти услуги
        List<ServiceEntity> services = serviceRepository.findAllById(bookingDto.getServiceIds());

        // 4. Получить статус "Подтверждено" или "Новая"
        AppointmentStatus status = appointmentStatusRepository.findByStatusName("PENDING")
                .orElseThrow(() -> new RuntimeException("Статус не найден"));

        // 5. Рассчитать сумму
        BigDecimal totalAmount = services.stream()
                .map(ServiceEntity::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 6. Создать запись (Appointment)
        Appointment appointment = new Appointment();
        appointment.setClient(client);

// Объединяем дату и время
        OffsetDateTime startDateTime = OffsetDateTime.of(
                timeslot.getSlotDate(),           // LocalDate
                timeslot.getStartTime(),          // LocalTime
                OffsetDateTime.now().getOffset()  // текущий offset
        );
        appointment.setStartTime(startDateTime);

        OffsetDateTime endDateTime = OffsetDateTime.of(
                timeslot.getSlotDate(),
                timeslot.getEndTime(),
                OffsetDateTime.now().getOffset()
        );
        appointment.setEndTime(endDateTime);

        appointment.setAppointmentStatus(status);
        appointment.setAmountWithoutDiscount(totalAmount);
        appointment.setDiscountAmount(BigDecimal.ZERO);
        appointment.setFinalAmount(totalAmount);
        // 7. Связать услуги с записью
        appointment.setServiceList(services);
        appointmentRepository.save(appointment);

        TimeslotStatus bookedStatus = timeslotStatusRepository.findById(2L)  // 2L = ID статуса "занят"
                .orElseThrow(() -> new RuntimeException("Статус временного слота не найден"));

        timeslot.setTimeslotStatus(bookedStatus);  // ✅ вместо setStatusId
        timeslotRepository.save(timeslot);
    }

    private Client createNewClient(ClientBookingDto bookingDto) {
        Client client = new Client();

        // Разбиваем имя на имя и фамилию
        String[] nameParts = bookingDto.getName().split(" ", 2);
        client.setFirstName(nameParts[0]);
        if (nameParts.length > 1) {
            client.setLastName(nameParts[1]);
        } else {
            client.setLastName("");
        }

        client.setPhone(bookingDto.getPhone());
        client.setTotalVisits(0L);
        client.setReferralCode(generateReferralCode());
        client.setSubscribedToNewsletter(false);
        client.setIsVerified(true);

        return clientRepository.save(client);
    }

    private String generateReferralCode() {
        return "REF" + System.currentTimeMillis();
    }
}