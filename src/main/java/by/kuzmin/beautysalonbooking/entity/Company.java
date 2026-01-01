package by.kuzmin.beautysalonbooking.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(
        name = "company"
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
//TODO проверить
public class Company {
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
    @Column(name = "created_at")
    private OffsetDateTime createAt;
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;
    @ManyToOne
    @JoinColumn(name = "company_address_id")
    private Address address;
}
