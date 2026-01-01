package by.kuzmin.beautysalonbooking.entity;

import jakarta.persistence.*;
import org.springframework.cglib.core.Local;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

@Entity
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "client")
    private List<Appointment> appointments;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "middle_name")
    private String middleName;

    @Column(name = "birth_date")
    private LocalDate birthDate;
    //TODO может надо сделать гендер отдельным справочником или мб массивом
    private String gender;

    @ManyToOne
    @JoinColumn(name = "preferred_salon_id")
    private Salon salon;

    @ManyToOne
    @JoinColumn(name = "preferred_employee_id")
    private Employee employee;

    @Column(name = "total_visits")
    private Long totalVisits;

    @Column(name = "last_visit_date")
    private LocalDate lastVisitDate;

    @Column(name = "first_visit_date")
    private LocalDate firstVisitDate;
    //TODO проверить может надо сделать как словарь с примерным содержимым: "Интернет, от друзей и еще что-то"
    private String source;
    @Column(name = "referral_code")
    private String referralCode;
    @Column(name = "referral_balance")
    private BigDecimal referralBalance;

    @ManyToOne
    @JoinColumn(name = "referred_by_client_id")
    private Client parentClient;

    @OneToMany(mappedBy = "parentClient")
    private List<Client> clients;

    private String notes;
    private String allergies;
    private String contraindications;

    @ManyToOne
    @JoinColumn(name = "status_id")
    private ClientStatus clientStatus;

    @Column(name = "is_subscribed_to_newsletter")
    private boolean isSubscribedToNewsletter;

    @Column(name = "is_verified")
    private boolean isVerified;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updated_at;

}
