package by.kuzmin.beautysalonbooking.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(
        name = "cancellation_initiated"
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {
        "appointmentList"
})
@EqualsAndHashCode(callSuper = false, exclude = {
        "appointmentList"
})
//TODO нет проверки на null значения полей и не везде где надо указано nullable = false
public class CancellationInitiated extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "person_name", nullable = false)
    private String personName;

    @OneToMany(mappedBy = "cancellationInitiated")
    private List<Appointment> appointmentList;
}
