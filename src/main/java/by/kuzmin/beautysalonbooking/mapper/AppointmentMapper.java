package by.kuzmin.beautysalonbooking.mapper;

import by.kuzmin.beautysalonbooking.dto.AppointmentDto;
import by.kuzmin.beautysalonbooking.entity.Appointment;
import by.kuzmin.beautysalonbooking.entity.Employee;
import by.kuzmin.beautysalonbooking.entity.ServiceEntity;
import by.kuzmin.beautysalonbooking.entity.Timeslot;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@Mapper(componentModel = "spring", uses = {ClientMapper.class})
public interface AppointmentMapper {

    @Mapping(target = "dateTime", source = "startTime")
    @Mapping(target = "status", source = "appointmentStatus.statusName")
    @Mapping(target = "serviceName", source = "serviceList", qualifiedByName = "getFirstServiceName")
    @Mapping(target = "servicePrice", source = "serviceList", qualifiedByName = "getFirstServicePrice")
    @Mapping(target = "employeeName", source = "timeslotList", qualifiedByName = "getEmployeeNameFromTimeslot")
    @Mapping(target = "salonAddress", source = "timeslotList", qualifiedByName = "getSalonAddressFromTimeslot")
    @Mapping(target = "finalAmount", source = "finalAmount")
    AppointmentDto toDto(Appointment appointment);

    @Named("getFirstServiceName")
    default String getFirstServiceName(List<ServiceEntity> serviceList) {
        if (serviceList == null || serviceList.isEmpty()) {
            return null;
        }
        return serviceList.get(0).getName();
    }

    @Named("getFirstServicePrice")
    default BigDecimal getFirstServicePrice(List<ServiceEntity> serviceList) {
        if (serviceList == null || serviceList.isEmpty()) {
            return null;
        }
        return serviceList.get(0).getPrice();
    }

    @Named("getEmployeeNameFromTimeslot")
    default String getEmployeeNameFromTimeslot(List<Timeslot> timeslotList) {
        if (timeslotList == null || timeslotList.isEmpty()) {
            return null;
        }
        Timeslot timeslot = timeslotList.get(0);
        if (timeslot.getEmployee() == null) {
            return null;
        }
        Employee employee = timeslot.getEmployee();
        // ✅ Формируем имя из firstName, middleName, lastName
        String name = employee.getFirstName();
        if (employee.getMiddleName() != null && !employee.getMiddleName().isBlank()) {
            name += " " + employee.getMiddleName();
        }
        name += " " + employee.getLastName();
        return name;
    }

    @Named("getSalonAddressFromTimeslot")
    default String getSalonAddressFromTimeslot(List<Timeslot> timeslotList) {
        if (timeslotList == null || timeslotList.isEmpty()) {
            return null;
        }
        Timeslot timeslot = timeslotList.get(0);
        if (timeslot.getSalon() == null || timeslot.getSalon().getAddress() == null) {
            return null;
        }
        return timeslot.getSalon().getAddress().getCity() + ", " +
                timeslot.getSalon().getAddress().getStreet();
    }
}