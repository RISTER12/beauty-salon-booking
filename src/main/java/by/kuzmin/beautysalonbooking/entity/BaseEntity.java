package by.kuzmin.beautysalonbooking.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.OffsetDateTime;
//TODO нет конструкторов(есть только дефолтный)
@Getter
@Setter
@MappedSuperclass
//TODO првоерить аннотации
@ToString(exclude = {
        "createdBy", "updatedBy", "createdAt", "updatedAt"
})
@EqualsAndHashCode(exclude = {
        "createdBy", "updatedBy", "createdAt", "updatedAt"
})
//TODO нет проверки на null значения полей и не везде где надо указано nullable = false
public class BaseEntity implements Serializable {

    private String createdBy;
    private String updatedBy;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private OffsetDateTime updatedAt;
}
