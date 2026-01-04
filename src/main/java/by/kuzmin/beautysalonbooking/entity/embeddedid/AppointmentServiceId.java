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
public class AppointmentServiceId implements Serializable {
    private Long appointmentId;
    private Long serviceId;
}
