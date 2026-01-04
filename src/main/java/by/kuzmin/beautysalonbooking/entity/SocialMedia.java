package by.kuzmin.beautysalonbooking.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(
        name = "social_media"
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = false)
public class SocialMedia extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "social_media_type_id")
    private SocialMediaType socialMediaType;

    private String value;
    private String url;
    //TODO нужен ли вообще owner_type если я добавил 4 поля для всех видов сущностей
    @Column(name = "owner_type")
    private String ownerType;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "salon_id")
    private Salon salon;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

    @Column(name = "is_primary")
    private boolean isPrimary;

    @Column(name = "is_visible")
    private boolean isVisible;

    @Column(name = "is_notification_channel")
    private boolean isNotificationChannel;
}
