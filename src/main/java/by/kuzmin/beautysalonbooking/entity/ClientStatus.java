package by.kuzmin.beautysalonbooking.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(
        name = "client_status"
)
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {
        "clientList"
})
@EqualsAndHashCode(callSuper = false, exclude = {
        "clientList"
})
//TODO нет проверки на null значения полей и не везде где надо указано nullable = false
public class ClientStatus extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "status_name", nullable = false)
    private String statusName;
    @OneToMany(mappedBy = "clientStatus")
    private List<Client> clientList;

}
