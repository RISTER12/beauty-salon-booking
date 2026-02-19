package by.kuzmin.beautysalonbooking.repository;

import by.kuzmin.beautysalonbooking.entity.EmployeeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeStatusRepository extends JpaRepository<EmployeeStatus,Long> {
}
