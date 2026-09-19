package com.crm.repos;

import com.crm.entites.SmsLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SmsLogRepo extends JpaRepository<SmsLog, Long> {
    List<SmsLog> findByAppointmentIdOrderByTimestampAsc(Long appointmentId);
    List<SmsLog> findByUserIdOrderByTimestampAsc(Long userId);
    Page<SmsLog> findAllByOrderByTimestampDesc(Pageable pageable);
}
