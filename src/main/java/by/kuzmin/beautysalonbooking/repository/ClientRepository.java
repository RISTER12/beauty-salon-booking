package by.kuzmin.beautysalonbooking.repository;


import by.kuzmin.beautysalonbooking.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.client.id = :clientId")
    long countAppointmentsByClientId(@Param("clientId") Long clientId);

    @Query("SELECT SUM(a.finalAmount) FROM Appointment a WHERE a.client.id = :clientId AND a.appointmentStatus.statusName = 'COMPLETED'")
    Double sumSpentByClientId(@Param("clientId") Long clientId);
    @Query("SELECT MAX(a.startTime) FROM Appointment a WHERE a.client.id = :clientId AND a.appointmentStatus.statusName = 'COMPLETED'")
    OffsetDateTime getLastVisitDate(@Param("clientId") Long clientId);
}