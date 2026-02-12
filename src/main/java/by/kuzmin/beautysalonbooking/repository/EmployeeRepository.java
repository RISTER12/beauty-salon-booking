package by.kuzmin.beautysalonbooking.repository;


import by.kuzmin.beautysalonbooking.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

}
