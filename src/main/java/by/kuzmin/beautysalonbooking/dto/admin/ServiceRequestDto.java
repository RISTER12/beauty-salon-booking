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
public class ServiceRequestDto {
    private String name;
    private String shortName;
    private String description;
    private String shortDescription;
    private BigDecimal price;
    private Long categoryId;
    private Long salonId;
    private List<String> photoUrlList;
    private List<Long> employeeIds;
}