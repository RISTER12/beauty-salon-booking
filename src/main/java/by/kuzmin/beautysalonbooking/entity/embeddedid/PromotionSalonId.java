package by.kuzmin.beautysalonbooking.entity.embeddedid;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class PromotionSalonId implements Serializable {
    private Long promotionId;
    private Long salonId;
}
