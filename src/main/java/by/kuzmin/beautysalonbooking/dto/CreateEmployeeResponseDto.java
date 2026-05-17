package by.kuzmin.beautysalonbooking.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class CreateEmployeeResponseDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String middleName;
    private String jobTitle;
    private Long experienceYears;
    private List<String> certificationList;
    private List<String> awardList;
    private String photoUrl;
    private List<String> portfolioPhotosUrlList;
    private List<String> portfolioVideoUrlList;
    private String status;
    private BigDecimal averageRating;
    private boolean isVisibleOnWebsite;
    private List<String> serviceNames;
}