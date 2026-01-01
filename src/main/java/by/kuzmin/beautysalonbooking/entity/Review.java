package by.kuzmin.beautysalonbooking.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "review"
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "comment_status_id")
    private CommentStatus commentStatus;
}
