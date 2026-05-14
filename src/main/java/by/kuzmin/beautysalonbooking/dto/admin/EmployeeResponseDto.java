package by.kuzmin.beautysalonbooking.dto.admin;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class EmployeeResponseDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String middleName;
    private String fullName;
    private String salonName;
    private String jobTitle;
    private Long experienceYears;
    private List<String> certificationList;
    private List<String> awardList;
    private String photoUrl;
    private String status;
    private BigDecimal averageRating;
    private Boolean isVisibleOnWebsite;
    private List<String> serviceNames;
}
