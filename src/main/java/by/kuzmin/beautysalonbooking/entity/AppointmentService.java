package by.kuzmin.beautysalonbooking.entity;

import by.kuzmin.beautysalonbooking.entity.embeddedid.AppointmentServiceId;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "appointment_service"
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class AppointmentService {
    @EmbeddedId
    private AppointmentServiceId id;

    @MapsId(value = "appointmentId")
    @ManyToOne
    @JoinColumn(name = "appointment_id")
    private Appointment appointment;

    @MapsId(value = "serviceId")
    @ManyToOne
    @JoinColumn(name = "service_id")
    private Service service;

}
