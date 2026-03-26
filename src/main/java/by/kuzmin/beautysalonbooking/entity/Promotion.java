package by.kuzmin.beautysalonbooking.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(
        name = "promotion"
)
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {
        "appointments", "serviceList", "salonList"
})
@EqualsAndHashCode(callSuper = false, exclude = {
        "appointments", "serviceList", "salonList"
})
public class Promotion extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "promotion_name", nullable = false)
    private String promotionName;
    private String description;
    private String code;
    @Column(name = "banner_url")
    private String bannerUrl;
    //Скидка в процентах
    @Column(name = "discount_count")
    private Long discountCount;
    @Column(name = "min_order_amount")
    private BigDecimal minOrderAmount;
    @Column(name = "usage_limit")
    private Long usageLimit;
    @Column(name = "usage_limit_per_client")
    private Long usageLimitPerClient;
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;
    @Column(name = "end_date")
    private LocalDate endDate;
    @Column(name = "used_count", nullable = false)
    private Long usedCount = 0L;
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @OneToMany(mappedBy = "promotion")
    private List<Appointment> appointments;
    @ManyToMany
    @JoinTable(
            name = "promotion_service",
            joinColumns = @JoinColumn(name = "promotion_id"),
            inverseJoinColumns = @JoinColumn(name = "service_id")
    )
    private List<ServiceEntity> serviceList;
    @ManyToMany
    @JoinTable(
            name = "promotion_salon",
            joinColumns = @JoinColumn(name = "promotion_id"),
            inverseJoinColumns = @JoinColumn(name = "salon_id")
    )
    private List<Salon> salonList;


}
