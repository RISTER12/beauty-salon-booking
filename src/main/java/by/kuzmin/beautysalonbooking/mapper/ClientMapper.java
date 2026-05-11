package by.kuzmin.beautysalonbooking.mapper;

import by.kuzmin.beautysalonbooking.dto.ClientProfileDto;
import by.kuzmin.beautysalonbooking.dto.ClientUpdateDto;
import by.kuzmin.beautysalonbooking.entity.Client;
import org.mapstruct.*;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ClientMapper {

    @Mapping(target = "loyaltyPoints", expression = "java(client.getLoyaltyPoints() != null ? client.getLoyaltyPoints() : 0)")
    ClientProfileDto toProfileDto(Client client);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(ClientUpdateDto dto, @MappingTarget Client client);

    // Если нужно игнорировать null значения при обновлении
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Client updateEntityFromDtoWithReturn(ClientUpdateDto dto, @MappingTarget Client client);
}