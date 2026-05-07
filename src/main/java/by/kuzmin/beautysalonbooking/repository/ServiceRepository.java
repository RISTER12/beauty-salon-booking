package by.kuzmin.beautysalonbooking.repository;

import by.kuzmin.beautysalonbooking.entity.ServiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceRepository extends JpaRepository<ServiceEntity,Long> {
}
