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
@ToString
@EqualsAndHashCode(callSuper = false)
//TODO проверить
public class Company extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "display_name")
    private String displayName;
    @Column(name = "company_legal_name")
    private String companyLegalName;
    private String description;
    @Column(name = "short_description")
    private String shortDescription;
    @Column(name = "tax_number")
    private String taxNumber;
    @Column(name = "registration_number")
    private String registrationNumber;

    @OneToOne
    @JoinColumn(name = "address_id")
    private Address address;

    @OneToMany(mappedBy = "company")
    private List<Salon> salons;

    @OneToMany(mappedBy = "company")
    private List<SocialMedia> socialMediaList;
}
