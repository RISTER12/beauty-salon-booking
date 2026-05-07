package by.kuzmin.beautysalonbooking.repository;

import by.kuzmin.beautysalonbooking.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule,Long> {
    List<Schedule> findAllByEmployeeId(Long employeeId);

}
