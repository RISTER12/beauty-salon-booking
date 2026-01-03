package by.kuzmin.beautysalonbooking.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Date;

@Entity
@Table(
        name = "revenue_report"
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = false)
//TODO проверить
public class RevenueReport extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "report_type_id")
    private ReportType reportType;

    @Column(name = "period_start_date")
    private LocalDate periodStartDate;
    @Column(name = "period_end_date")
    private LocalDate periodEndDate;
    @Column(name = "generated_date")
    private LocalDate generatedDate;

    @ManyToOne
    @JoinColumn(name = "salon_id")
    private Salon salon;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @Column(name = "total_revenue")
    private BigDecimal totalRevenue;
    @Column(name = "net_revenue")
    private BigDecimal net_revenue;
    @Column(name = "total_appointments")
    private Long totalAppointments;
    @Column(name = "completed_appointments")
    private Long completedAppointments;
    @Column(name = "cancelled_appointments")
    private Long cancelledAppointments;
    @Column(name = "new_clients")
    private Long newClients;
    @Column(name = "returning_clients")
    private Long returningClients;
    @Column(name = "average_check")
    private BigDecimal averageCheck;
    @Column(name = "average_rating")
    private BigDecimal averageRating;





}
