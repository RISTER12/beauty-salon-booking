package by.kuzmin.beautysalonbooking.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "payment"
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "payment")
    private Appointment appointment;
}
