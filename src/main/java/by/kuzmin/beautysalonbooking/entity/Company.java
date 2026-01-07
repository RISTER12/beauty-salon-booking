package by.kuzmin.beautysalonbooking.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.List;

@Entity
@Table(
        name = "company"
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {
        "salonList", "socialMediaList"
})
@EqualsAndHashCode(callSuper = false, exclude = {
        "salonList", "socialMediaList", "address"
})
//TODO нет проверки на null значения полей и не везде где надо указано nullable = false
public class Company extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "company_name", nullable = false)
    private String companyName;
    @Column(name = "company_legal_name", nullable = false)
    private String companyLegalName;
    private String description;
    @Column(name = "short_description")
    private String shortDescription;
    @Column(name = "tax_number", nullable = false)
    private String taxNumber;
    @Column(name = "registration_number", nullable = false)
    private String registrationNumber;

    @ManyToOne
    @JoinColumn(name = "address_id", nullable = false)
    private Address address;

    @OneToMany(mappedBy = "company")
    private List<Salon> salonList;

    @OneToMany(mappedBy = "company")
    private List<SocialMedia> socialMediaList;
}
