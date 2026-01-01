package by.kuzmin.beautysalonbooking.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(
        name = "promotion"
)
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class Promotion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToMany(mappedBy = "promotion")
    private List<Appointment> appointments;
}
