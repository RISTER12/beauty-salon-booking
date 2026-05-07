package by.kuzmin.beautysalonbooking.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * Сущность адреса для хранения местоположения
 */
@Entity
@Table(
        name = "address"
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = false)
public class Address extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "full_address")
    private String fullAddress;
    @Column(nullable = false)
    private String city;
    private String district;
    @Column(nullable = false)
    private String street;
    @Column(nullable = false)
    private String building;
    private String apartment;
    @Column(name = "metro_station")
    private String metroStation;
    @Column(name = "postal_code", nullable = false)
    private String postalCode;
    private BigDecimal latitude;
    private BigDecimal longitude;
    @Column(name = "map_url")
    private String mapUrl;
    private String description;
}
