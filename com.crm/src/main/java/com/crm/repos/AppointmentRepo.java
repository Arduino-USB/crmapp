package com.crm.repos;

import com.crm.entites.Appointment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.time.*;

import java.util.List;          
import java.time.Instant;       

@Repository
public interface AppointmentRepo extends JpaRepository<Appointment, Long> {

    List<Appointment> findAll();
    List<Appointment> findByTime(Instant time);
    
    Optional<Appointment> findByUserIdAndTime(long userId, Instant time);    
    Optional<Appointment> findFirstByUserIdOrderByTimeDesc(long userId);
    List<Appointment> findByTimeBetween(Instant start, Instant end);

    @Query("""
        SELECT a
        FROM Appointment a
        JOIN User u ON u.id = a.userId
        WHERE u.phone IS NOT NULL
          AND TRIM(u.phone) <> ''
    """)
    List<Appointment> findAppointmentPhoneNotBlank();

    @Query("""
        SELECT a FROM Appointment a 
        JOIN User u ON u.id = a.userId 
        WHERE LOWER(CONCAT(u.firstName, ' ', u.lastName)) LIKE LOWER(CONCAT('%', :partName, '%'))
    """)
    Page<Appointment> searchByUserNameContainingIgnoreCase(@Param("partName") String partName, Pageable pageable);

    default List<Appointment> findByDay(LocalDate day) {
        ZoneId zone = ZoneId.systemDefault();

        Instant start = day.atStartOfDay(zone).toInstant();
        Instant end = day.plusDays(1).atStartOfDay(zone).toInstant();

        return findByTimeBetween(start, end);
    }
}
