package by.kuzmin.beautysalonbooking.repository;

import by.kuzmin.beautysalonbooking.entity.Timeslot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TimeslotRepository extends JpaRepository<Timeslot, Long> {

    /**
     * Найти все свободные слоты за указанный месяц для всех сотрудников
     * @param freeStatusId ID статуса "FREE"
     */
    @Query("SELECT t FROM Timeslot t WHERE " +
            "YEAR(t.slotDate) = :year AND MONTH(t.slotDate) = :month " +
            "AND t.timeslotStatus.id = :freeStatusId " +
            "ORDER BY t.slotDate ASC, t.employee.id ASC, t.startTime ASC")
    List<Timeslot> findAllByMonth(
            @Param("year") int year,
            @Param("month") int month,
            @Param("freeStatusId") Long freeStatusId);

    /**
     * Найти все свободные слоты за конкретный день для всех сотрудников
     * @param date конкретная дата
     * @param freeStatusId ID статуса "FREE"
     */
    @Query("SELECT t FROM Timeslot t WHERE " +
            "t.slotDate = :date " +
            "AND t.timeslotStatus.id = :freeStatusId " +
            "ORDER BY t.employee.id ASC, t.startTime ASC")
    List<Timeslot> findAllByDay(
            @Param("date") LocalDate date,
            @Param("freeStatusId") Long freeStatusId);

    /**
     * Найти все свободные слоты за месяц для конкретного сотрудника
     * @param employeeId ID сотрудника
     * @param freeStatusId ID статуса "FREE"
     */
    @Query("SELECT t FROM Timeslot t WHERE " +
            "t.employee.id = :employeeId " +
            "AND YEAR(t.slotDate) = :year AND MONTH(t.slotDate) = :month " +
            "AND t.timeslotStatus.id = :freeStatusId " +
            "ORDER BY t.slotDate ASC, t.startTime ASC")
    List<Timeslot> findAllByMonthAndEmployeeId(
            @Param("employeeId") Long employeeId,
            @Param("year") int year,
            @Param("month") int month,
            @Param("freeStatusId") Long freeStatusId);

    /**
     * Найти все свободные слоты за конкретный день для конкретного сотрудника
     * @param employeeId ID сотрудника
     * @param date конкретная дата
     * @param freeStatusId ID статуса "FREE"
     */
    @Query("SELECT t FROM Timeslot t WHERE " +
            "t.employee.id = :employeeId " +
            "AND t.slotDate = :date " +
            "AND t.timeslotStatus.id = :freeStatusId " +
            "ORDER BY t.startTime ASC")
    List<Timeslot> findAllByDayAndEmployeeId(
            @Param("employeeId") Long employeeId,
            @Param("date") LocalDate date,
            @Param("freeStatusId") Long freeStatusId);

    Timeslot findById(long id);

    List<Timeslot> findByEmployeeIdAndSlotDate(Long employeeId, LocalDate date);

    List<Timeslot> findByEmployeeIdAndSlotDateGreaterThanEqual(Long employeeId, LocalDate date);

    List<Timeslot> findByEmployeeIdAndSlotDateBetween(Long employeeId, LocalDate startDate, LocalDate endDate);
}
