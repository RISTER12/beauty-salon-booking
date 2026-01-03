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
//TODO не забыть о том что comment_status_id уже есть
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

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;
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
    //TODO проверить надо ли использовать объекст Boolean
    @Column(name = "is_features")
    private boolean isFeatures;

    @Column(name = "is_anonymous")
    private boolean isAnonymous;


}
