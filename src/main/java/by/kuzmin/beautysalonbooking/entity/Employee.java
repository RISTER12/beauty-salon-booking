package by.kuzmin.beautysalonbooking.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(
        name = "employee"
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"clientList", "employeeServiceList", "revenueReportList", "socialMediaList", "timeslotList", "portfolioPhotosUrlList", "portfolioVideoUrlList"})
@EqualsAndHashCode(callSuper = false, exclude = {"clientList", "employeeServiceList", "revenueReportList", "socialMediaList", "timeslotList", "portfolioPhotosUrlList", "portfolioVideoUrlList"})
public class Employee extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "employee")
    private List<Client> clientList;

    @ManyToOne
    @JoinColumn(name = "salon_id")
    private Salon salon;

    @Column(name = "first_name", nullable = false)
    private String firstName;
    @Column(name = "last_name", nullable = false)
    private String lastName;
    @Column(name = "middle_name")
    private String middleName;
    @Column(name = "job_title", nullable = false)
    private String jobTitle;
    @Column(name = "experience_years", nullable = false)
    private Long experienceYears;
    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(columnDefinition = "text[]", name = "certifications")
    private List<String> certificationList;
    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(columnDefinition = "text[]", name = "awards")
    private List<String> awardList;
    private String photoUrl;
    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(columnDefinition = "text[]", name = "portfolio_photos_urls")
    private List<String> portfolioPhotosUrlList;
    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(columnDefinition = "text[]", name = "portfolio_video_urls")
    private List<String> portfolioVideoUrlList;
    @ManyToOne
    @JoinColumn(name = "employee_status_id", nullable = false)
    private EmployeeStatus employeeStatus;
    @Column(name = "average_rating")
    private BigDecimal averageRating;
    @Column(name = "is_visible_on_website", nullable = false)
    private boolean isVisibleOnWebsite = false;

    @OneToMany(mappedBy = "employee")
    private List<EmployeeServiceProvision> employeeServiceList;

    @OneToMany(mappedBy = "employee")
    private List<RevenueReport> revenueReportList;

    @OneToMany(mappedBy = "employee")
    private List<SocialMedia> socialMediaList;

    @OneToMany(mappedBy = "employee")
    private List<Timeslot> timeslotList;
}
