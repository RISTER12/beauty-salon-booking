package by.kuzmin.beautysalonbooking.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(
        name = "notification_status"
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {
        "notificationList"
})
@EqualsAndHashCode(callSuper = false, exclude = {
        "notificationList"
})
public class NotificationStatus extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "status_name", nullable = false, unique = true)
    private String statusName;

    @OneToMany(mappedBy = "notificationStatus")
    private List<Notification> notificationList;
}
