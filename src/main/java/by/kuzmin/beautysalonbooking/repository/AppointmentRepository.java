package by.kuzmin.beautysalonbooking.repository;

import by.kuzmin.beautysalonbooking.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    //Базовые методы (должны работать)
    List<Appointment> findByClientId(Long clientId);

    //Сортировка по startTime
    List<Appointment> findByClientIdOrderByStartTimeAsc(Long clientId);


    @Query("SELECT a FROM Appointment a " +
            "WHERE a.client.id = :clientId " +
            "AND a.startTime > :now " +
            "AND a.appointmentStatus.statusName != 'CANCELLED' " +
            "ORDER BY a.startTime ASC")
    List<Appointment> findUpcomingActiveByClientId(
            @Param("clientId") Long clientId,
            @Param("now") OffsetDateTime now
    );

    //С фильтром по времени (GreaterThan)
    List<Appointment> findByClientIdAndStartTimeGreaterThanOrderByStartTimeAsc(Long clientId, OffsetDateTime startTime);

    //Подсчёт (используем @Query для сложных случаев)
    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.client.id = :clientId AND a.appointmentStatus.statusName = :status AND a.startTime > :now")
    long countPending(@Param("clientId") Long clientId, @Param("status") String status, @Param("now") OffsetDateTime now);

    //Для истории (менее текущего времени)
    List<Appointment> findByClientIdAndStartTimeLessThanOrderByStartTimeDesc(Long clientId, OffsetDateTime startTime);

    // AppointmentRepository.java
    @Query("SELECT a FROM Appointment a WHERE a.startTime BETWEEN :start AND :end ORDER BY a.startTime ASC")
    List<Appointment> findByStartTimeBetweenOrderByStartTimeAsc(@Param("start") OffsetDateTime start, @Param("end") OffsetDateTime end);

    @Query("SELECT a FROM Appointment a JOIN a.timeslotList t WHERE t.employee.id = :employeeId AND a.startTime BETWEEN :start AND :end")
    List<Appointment> findByEmployeeIdAndStartTimeBetween(@Param("employeeId") Long employeeId, @Param("start") OffsetDateTime start, @Param("end") OffsetDateTime end);

    @Query("SELECT COUNT(a) FROM Appointment a JOIN a.serviceList s WHERE s.id = :serviceId AND a.startTime BETWEEN :start AND :end")
    int countByServiceIdAndStartTimeBetween(@Param("serviceId") Long serviceId, @Param("start") OffsetDateTime start, @Param("end") OffsetDateTime end);

    @Query("SELECT SUM(a.finalAmount) FROM Appointment a JOIN a.serviceList s WHERE s.id = :serviceId AND a.startTime BETWEEN :start AND :end")
    double sumRevenueByServiceIdAndStartTimeBetween(@Param("serviceId") Long serviceId, @Param("start") OffsetDateTime start, @Param("end") OffsetDateTime end);
}