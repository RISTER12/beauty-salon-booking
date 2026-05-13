package by.kuzmin.beautysalonbooking.repository;

import by.kuzmin.beautysalonbooking.entity.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppointmentStatusRepository extends JpaRepository<AppointmentStatus, Long> {
    Optional<AppointmentStatus> findByStatusName(String name);
}