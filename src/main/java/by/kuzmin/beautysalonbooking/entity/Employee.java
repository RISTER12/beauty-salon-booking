package by.kuzmin.beautysalonbooking.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
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
@EqualsAndHashCode
//TODO проверить
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "employee")
    private List<Client> clients;

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
    private String experienceYears;
    //TODO проверить правильно ли хранить сертификаты в виде текста
    private ArrayList<String> certifications;
    //TODO проверить правильно ли хранить награды в виде текста
    private ArrayList<String> awards;
    @Column(name = "photo_url")
    private String photoUrl;
    //TODO проверить правильно ли хранить портфолио в виде текста
    @Column(name = "portfolio_photos_urls")
    private ArrayList<String> portfolioPhotosUrls;
    //TODO проверить правильно ли хранить портфолио в виде текста
    @Column(name = "portfolio_video_urls")
    private ArrayList<String> portfolioVideoUrls;
    @ManyToOne
    @JoinColumn(name = "status_employee_id")
    private StatusEmployee statusEmployee;
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", name = "work_schedule")
    private Map<String, Object> workSchedule;
    @Column(name = "average_rating")
    private BigDecimal averageRating;
    @Column(name = "is_viible_on_website")
    private boolean isVisibleOnWebsite;
    @Column(name = "created_at")
    private OffsetDateTime createdAt;
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @OneToMany(mappedBy = "employee")
    private List<EmployeeService> employeeServices;


}
