package by.kuzmin.beautysalonbooking.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.sql.Time;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Entity
@Table(
        name = "salon"
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = false)
//TODO нет проверки на null значения полей и не везде где надо указано nullable = false
public class Salon extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "preferredSalon")
    private List<Client> preferredClientList;

    @OneToMany(mappedBy = "salon")
    private List<Employee> employeeList;

    @OneToMany(mappedBy = "salon")
    private List<RevenueReport> revenueReportList;

    @Column(name = "salon_name")
    private String salonName;
    @ManyToOne
    @JoinColumn(name = "address_id")
    private Address address;
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb",name = "working_hours")
    private Map<String, Object> workingHours;
    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(columnDefinition = "text[]", name = "amenities")
    private List<String> amenitiesList;
    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(columnDefinition = "text[]", name = "photos")
    private List<String> photoList;
    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(columnDefinition = "text[]", name = "interior_photos")
    private List<String> interiorPhotoList;
    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(columnDefinition = "text[]", name = "video_urls")
    private List<String> videoUrlList;
    @Column(name = "total_area")
    private BigDecimal totalArea;
    @ManyToOne
    @JoinColumn(name = "status_id")
    private SalonStatus salonStatus;
    @Column(name = "is_booking_available")
    private boolean isBookingAvailable;
    @Column(name = "is_visible_on_website")
    private boolean isVisibleOnWebsite;
    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

    @OneToMany(mappedBy = "salon")
    private List<Service> serviceList;

    @OneToMany(mappedBy = "salon")
    private List<SocialMedia> socialMediaList;

    @OneToMany(mappedBy = "salon")
    private List<Timeslot> timeslotList;

    @ManyToMany(mappedBy = "salonList")
    private List<Promotion> promotionList;
}
