package by.kuzmin.beautysalonbooking.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.List;

@Entity
@Table(
        name = "appointment"
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = false)
//TODO нет проверки на null значения полей и не везде где надо указано nullable = false
public class Appointment extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @Column(name = "start_time", nullable = false)
    private OffsetDateTime startTime;
    @Column(name = "end_time", nullable = false)
    private OffsetDateTime endTime;

    @ManyToOne
    @JoinColumn(name = "appointment_status_id", nullable = false)
    private AppointmentStatus appointmentStatus;

    @Column(name = "amount_without_discount", nullable = false)
    private BigDecimal amountWithoutDiscount;
    @Column(name = "discount_amount", nullable = false)
    private BigDecimal discountAmount;
    @Column(name = "final_amount", nullable = false)
    private BigDecimal finalAmount;
    @Column(name = "prepayment_amount")
    private BigDecimal prepaymentAmount;

    @OneToMany(mappedBy = "appointment")
    private List<Payment> payments;

    @Column(name = "client_notes")
    private String clientNotes;
    @Column(name = "employee_notes")
    private String employeeNotes;
    @Column(name = "cancellation_reason")
    private String cancellationReason;

    @ManyToOne
    @JoinColumn(name = "cancellation_initiated_by_id")
    private CancellationInitiated cancellationInitiated;

    @ManyToOne
    @JoinColumn(name = "promotion_id")
    private Promotion promotion;

    @Column(name = "cancelled_At")
    private OffsetDateTime cancelledAt;
    @Column(name = "completed_at")
    private OffsetDateTime completedAt;

    @OneToMany(mappedBy = "appointment")
    private List<Review> reviews;

    @OneToMany(mappedBy = "appointment")
    private List<Timeslot> timeslots;

    @OneToMany(mappedBy = "appointment")
    private List<AppointmentService> appointmentServices;
}
