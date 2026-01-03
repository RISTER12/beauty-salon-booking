package by.kuzmin.beautysalonbooking.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
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
public class Promotion extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "promotion_name")
    private String promotionName;
    private String description;
    private String code;
    @Column(name = "banner_url")
    private String bannerUrl;
    //Скидка в процентах
    @Column(name = "discount_count")
    private int discountCount;
    @Column(name = "min_order_amount")
    private BigDecimal minOrderAmount;
    @Column(name = "applicable_service_ids")
    private ArrayList<Long> applicableServiceIds;
    @Column(name = "applicable_category_ids")
    private ArrayList<Long> applicableCategoryIds;
    @Column(name = "applicable_salon_ids")
    private ArrayList<Long> applicableSalonIds;
    @Column(name = "usage_limit")
    private Long usageLimit;
    @Column(name = "usage_limit_per_client")
    private Long usageLimitPerClient;
    @Column(name = "start_date")
    private Date startDate;
    @Column(name = "end_date")
    private Date endDate;
    @Column(name = "used_count")
    private Long usedCount;
    //TODO проверить нужен здесь объект Boolean или нет
    @Column(name = "is_active")
    private boolean isActive;

    @OneToMany(mappedBy = "promotion")
    private List<Appointment> appointments;
}
