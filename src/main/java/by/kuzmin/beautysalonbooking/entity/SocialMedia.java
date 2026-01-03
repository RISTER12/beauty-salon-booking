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
@EqualsAndHashCode
public class SocialMedia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "social_media_type_id")
    private SocialMediaType socialMediaType;

    private String value;
    private String url;
    @Column(name = "owner_type")
    private String ownerType;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private Client client;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private Salon salon;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private Company company;

    @Column(name = "is_primary")
    private boolean isPrimary;

    @Column(name = "is_visible")
    private boolean isVisible;

    @Column(name = "is_notification_channel")
    private boolean isNotificationChannel;
}
