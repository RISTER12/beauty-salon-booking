package by.kuzmin.beautysalonbooking.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.cglib.core.Local;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

@Entity
@Table(
        name = "client"
)
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {
        "appointmentList", "notifications", "reviews", "socialMediaList"
})
@EqualsAndHashCode(callSuper = false, exclude = {
        "appointmentList", "notifications", "reviews", "socialMediaList"
})
//TODO нет проверки на null значения полей и не везде где надо указано nullable = false
public class Client extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "client")
    private List<Appointment> appointmentList;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "middle_name")
    private String middleName;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @ManyToOne
    @JoinColumn(name = "gender_id")
    private Gender gender;

    @ManyToOne
    @JoinColumn(name = "preferred_salon_id")
    private Salon preferredSalon;

    @ManyToOne
    @JoinColumn(name = "preferred_employee_id")
    private Employee employee;

    @Column(name = "total_visits", nullable = false)
    private Long totalVisits = 0L;

    @Column(name = "last_visit_date")
    private LocalDate lastVisitDate;

    @Column(name = "first_visit_date")
    private LocalDate firstVisitDate;

    @ManyToOne
    @JoinColumn(name = "source_id")
    private Source source;

    @Column(name = "referral_code")
    private String referralCode;
    @Column(name = "referral_balance")
    private BigDecimal referralBalance;

    @ManyToOne
    @JoinColumn(name = "referred_by_client_id")
    private Client referredBy;

    private String notes;
    private String allergies;
    private String contraindications;

    @ManyToOne
    @JoinColumn(name = "status_id")
    private ClientStatus clientStatus;

    @Column(name = "is_subscribed_to_newsletter", nullable = false)
    private boolean isSubscribedToNewsletter = false;
    @Column(name = "is_verified", nullable = false)
    private Boolean isVerified;

    @OneToMany(mappedBy = "client")
    private List<Notification> notifications;

    @OneToMany(mappedBy = "client")
    private List<Review> reviews;

    @OneToMany(mappedBy = "client")
    private List<SocialMedia> socialMediaList;
}
