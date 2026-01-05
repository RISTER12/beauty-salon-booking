package by.kuzmin.beautysalonbooking.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Entity
@Table(
        name = "employee"
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = false)
//TODO нет проверки на null значения полей и не везде где надо указано nullable = false
public class Employee extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "employee")
    private List<Client> clientList;

    @ManyToOne
    @JoinColumn(name = "salon_id")
    private Salon salon;

    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "middle_name")
    private String middleName;
    @Column(name = "job_title")
    private String jobTitle;
    @Column(name = "experience_years")
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
    @JoinColumn(name = "employee_status_id")
    private EmployeeStatus employeeStatus;
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", name = "work_schedule")
    private Map<String, Object> workSchedule;
    @Column(name = "average_rating")
    private BigDecimal averageRating;
    @Column(name = "is_visible_on_website")
    private boolean isVisibleOnWebsite;

    @OneToMany(mappedBy = "employee")
    private List<EmployeeService> employeeServiceList;

    @OneToMany(mappedBy = "employee")
    private List<RevenueReport> revenueReportList;

    @OneToMany(mappedBy = "employee")
    private List<Employee> employeeList;

    @OneToMany(mappedBy = "employee")
    private List<SocialMedia> socialMediaList;

    @OneToMany(mappedBy = "employee")
    private List<Timeslot> timeslotList;
}
