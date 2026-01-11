package by.kuzmin.beautysalonbooking.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(
        name = "gender"
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {
        "clientList"
})
@EqualsAndHashCode(callSuper = false, exclude = {
        "clientList"
})
public class Gender extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String code;
    @Column(name = "display_name", nullable = false)
    private String displayName;

    @OneToMany(mappedBy = "gender")
    private List<Client> clientList;
}
