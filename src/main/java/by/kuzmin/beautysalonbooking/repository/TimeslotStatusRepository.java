package by.kuzmin.beautysalonbooking.repository;

import by.kuzmin.beautysalonbooking.entity.TimeslotStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TimeslotStatusRepository extends JpaRepository<TimeslotStatus, Long> {

    Optional<TimeslotStatus> findByStatusName(String name);
}