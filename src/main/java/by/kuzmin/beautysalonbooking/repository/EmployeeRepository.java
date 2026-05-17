package by.kuzmin.beautysalonbooking.repository;


import by.kuzmin.beautysalonbooking.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    List<Employee> findAllByOrderByFirstNameAsc();

    List<Employee> findByIsActiveTrue();

    List<Employee> findBySalonIdAndIsActiveTrue(Long salonId);

}
