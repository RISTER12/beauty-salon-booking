package by.kuzmin.beautysalonbooking.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(
        name = "promotion"
)
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = false)
//TODO проверить
//TODO ManyToMany с service и salon
//TODO нет проверки на null значения полей и не везде где надо указано nullable = false
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
    @Column(name = "start_date")
    private LocalDate startDate;
    @Column(name = "end_date")
    private LocalDate endDate;
    @Column(name = "used_count")
    private Long usedCount;
    @Column(name = "is_active")
    private Boolean isActive;

    @OneToMany(mappedBy = "promotion")
    private List<Appointment> appointments;
    //TODO проверить
    @ManyToMany
    @JoinTable(
            name = "promotion_service",
            joinColumns = @JoinColumn(name = "promotion_id"),
            inverseJoinColumns = @JoinColumn(name = "service_id")
    )
    private List<Service> serviceList;
    //TODO проверить сязь м-м
    @ManyToMany
    @JoinTable(
            name = "promotion_salon",
            joinColumns = @JoinColumn(name = "promotion_id"),
            inverseJoinColumns = @JoinColumn(name = "salon_id")
    )
    private List<Salon> salonList;


}
