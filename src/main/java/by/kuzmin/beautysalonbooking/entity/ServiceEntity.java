package by.kuzmin.beautysalonbooking.entity;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(
        name = "service"
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {
        "promotionList", "employeeList"
})
@EqualsAndHashCode(callSuper = false, exclude = {
        "promotionList", "employeeList"
})
public class ServiceEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private ServiceCategory serviceCategory;
    @Column(nullable = false)
    private String name;
    @Column(name = "short_name")
    private String shortName;
    private String description;
    @Column(name = "short_description")
    private String shortDescription;
    @Column(name = "min_price", nullable = false)
    private BigDecimal minPrice;
    @Column(name = "max_price", nullable = false)
    private BigDecimal maxPrice;
    @Column(name = "price_range_description")
    private String priceRangeDescription;
    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(columnDefinition = "text[]", name = "photo_urls")
    private List<String> photoUrlList;

    @ManyToOne
    @JoinColumn(name = "salon_id", nullable = false)
    private Salon salon;

    @ManyToMany(mappedBy = "serviceList")
    private List<Employee> employeeList;

    @ManyToMany(mappedBy = "serviceList")
    private List<Promotion> promotionList;
}
