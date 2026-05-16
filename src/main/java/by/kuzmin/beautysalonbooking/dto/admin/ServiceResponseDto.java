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
public class ServiceResponseDto {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private String categoryName;
    private String salonName;
    private List<String> photoUrlList;
    private List<String> employeeNames;
    private boolean isActive;
}