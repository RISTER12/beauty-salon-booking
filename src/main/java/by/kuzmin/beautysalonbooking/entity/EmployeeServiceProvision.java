package by.kuzmin.beautysalonbooking.entity;

import by.kuzmin.beautysalonbooking.entity.embeddedid.EmployeeServiceId;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(
        name = "employee_service"
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {
        "employee", "service"
})
@EqualsAndHashCode(callSuper = false, exclude = {
        "employee", "service"
})
public class EmployeeServiceProvision extends BaseEntity {
    @EmbeddedId
    private EmployeeServiceId id;

    private BigDecimal price;
    @Column(name = "duration_minutes")
    private Long durationMinutes;

    @MapsId(value = "employeeId")
    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @MapsId(value = "serviceId")
    @ManyToOne
    @JoinColumn(name = "service_id")
    private Service service;
}
