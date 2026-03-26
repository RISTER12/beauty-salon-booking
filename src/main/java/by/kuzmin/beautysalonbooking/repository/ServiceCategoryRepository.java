package by.kuzmin.beautysalonbooking.repository;

import by.kuzmin.beautysalonbooking.entity.ServiceCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceCategoryRepository extends JpaRepository<ServiceCategory,Long> {
}
