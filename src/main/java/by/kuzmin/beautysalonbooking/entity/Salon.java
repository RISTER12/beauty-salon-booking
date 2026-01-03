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
public class Salon extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "salon")
    private List<Client> clients;

    @OneToMany(mappedBy = "salon")
    private List<Employee> employees;

    @OneToMany(mappedBy = "salon")
    private List<RevenueReport> revenueReports;

    @Column(name = "salon_name")
    private String salonName;
    @OneToOne
    @JoinColumn(name = "address_id")
    private Address address;
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb",name = "working_hours")
    private Map<String, Object> workingHours;

    private ArrayList<String> amenities;
    private ArrayList<String> photos;
    @Column(name = "interior_photos")
    private ArrayList<String> interiorPhotos;
    @Column(name = "video_url")
    private ArrayList<String> videoUrl;
    @Column(name = "total_area")
    private BigDecimal totalArea;
    @ManyToOne
    @JoinColumn(name = "status_is")
    private SalonStatus salonStatus;
    @Column(name = "is_booking_available")
    private boolean isBookingAvailable;
    @Column(name = "is_visible_on_website")
    private boolean isVisibleOnWebsite;
    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

    @OneToMany(mappedBy = "salon")
    private List<Service> services;

    @OneToMany(mappedBy = "salon")
    private List<SocialMedia> socialMediaList;

    @OneToMany(mappedBy = "salon")
    private List<Timeslot> timeslots;

}
