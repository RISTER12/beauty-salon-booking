package by.kuzmin.beautysalonbooking.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(
        name = "review"
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = false)
//TODO нет проверки на null значения полей и не везде где надо указано nullable = false
public class Review extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "comment_status_id")
    private CommentStatus commentStatus;

    @ManyToOne
    @JoinColumn(name = "appointment_id")
    private Appointment appointment;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    //Рейтинг от 1 до 5
    @Column(name = "rating_overall")
    private byte ratingOverall;
    @Column(name = "rating_service")
    private byte ratingService;
    @Column(name = "rating_employee")
    private byte ratingEmployee;
    @Column(name = "rating_atmosphere")
    private byte ratingAtmosphere;
    private String comment;
    @Column(name = "positive_aspects")
    private String positiveAspects;
    @Column(name = "negative_aspects")
    private String negativeAspects;
    @Column(name = "admin_response")
    private String adminResponse;
    //TODO тут реализована только односторонняя связь, проверить нужна ли вообще двусторонняя
    @ManyToOne
    @JoinColumn(name = "responded_by_employee_id")
    private Employee respondedEmployee;

    @Column(name = "responded_at")
    private OffsetDateTime respondedAt;

    //TODO тут реализована только односторонняя связь, проверить нужна ли вообще двусторонняя
    @ManyToOne
    @JoinColumn(name = "moderated_by_employee_id")
    private Employee moderatedEmployee;

    @Column(name = "moderated_at")
    private OffsetDateTime moderatedAt;

    @Column(name = "moderation_nodes")
    private String moderationNodes;
    // Показывать ли комментарий на главной странице
    @Column(name = "is_features")
    private Boolean isFeatures;

    @Column(name = "is_anonymous")
    private Boolean isAnonymous;


}
