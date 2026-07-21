package com.crm.repos;

import com.crm.entites.Appointment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.Optional;
import java.time.*;


import java.util.List;         
import java.time.Instant;       

@Repository
public interface AppointmentRepo extends JpaRepository<Appointment, Long> {

    List<Appointment> findByTime(Instant time);
	
	Optional<Appointment> findByUserIdAndTime(long userId, Instant time);	
    // DECLARE THIS METHOD HERE SO THE DEFAULT METHOD CAN CALL IT:
    List<Appointment> findByTimeBetween(Instant start, Instant end);

    default List<Appointment> findByDay(LocalDate day) {
        ZoneId zone = ZoneId.systemDefault();

        Instant start = day.atStartOfDay(zone).toInstant();
        Instant end = day.plusDays(1).atStartOfDay(zone).toInstant();

        return findByTimeBetween(start, end);
    }
}
