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
@EqualsAndHashCode
//TODO нет проверки на null значения полей и не везде где надо указано nullable = false
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "full_address")
    private String fullAddress;
    private String city;
    private String district;
    private String street;
    private String building;
    private String apartment;
    @Column(name = "metro_station")
    private String metroStation;
    @Column(name = "postal_code")
    private String postalCode;
    private BigDecimal latitude;
    private BigDecimal longitude;
    @Column(name = "map_url")
    private String mapUrl;
    private String description;
    //TODO требует явного конвертера в JPA
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;
    //TODO проверить нужна ли вообще связь 1-M(получается так что на 1 адресс много компаний) думаю нужна связь 1-1
    @OneToMany(mappedBy = "address")
    private List<Company> companies;

}
