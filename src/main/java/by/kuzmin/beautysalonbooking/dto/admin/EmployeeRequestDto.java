package by.kuzmin.beautysalonbooking.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeRequestDto {
    private String firstName;
    private String lastName;
    private String middleName;
    private Long salonId;
    private String jobTitle;
    private Long experienceYears;
    private List<String> certificationList;
    private List<String> awardList;
    private String photoUrl;
    private List<String> portfolioPhotosUrlList;
    private List<String> portfolioVideoUrlList;
    private Long statusId;
    private BigDecimal averageRating;
    private Boolean isVisibleOnWebsite;
    private List<Long> serviceIds;
}