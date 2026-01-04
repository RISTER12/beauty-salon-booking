package by.kuzmin.beautysalonbooking.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "service"
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = false)
//TODO проверить
public class Service extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private ServiceCategory serviceCategory;

    private String name;
    @Column(name = "short_name")
    private String shortName;
    private String description;
    @Column(name = "short_description")
    private String shortDescription;
    @Column(name = "min_price")
    private BigDecimal minPrice;
    @Column(name = "max_price")
    private BigDecimal maxPrice;
    @Column(name = "price_range_description")
    private String priceRangeDescription;
    @Column(name = "photo_urls")
    private ArrayList<String> photoUrls;

    @ManyToOne
    @JoinColumn(name = "salon_id")
    private Salon salon;

    @OneToMany(mappedBy = "service")
    private List<EmployeeService> employeeServices;

    @OneToMany(mappedBy = "service")
    private List<AppointmentService> appointmentServices;
}
