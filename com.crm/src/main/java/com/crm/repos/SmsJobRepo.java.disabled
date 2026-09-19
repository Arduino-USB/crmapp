package com.crm.repos;

import com.crm.entites.SmsJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;


@Repository
public interface SmsJobRepo extends JpaRepository<SmsJob, Long> {
	Optional<SmsJob> findFirstByRecipientAndStatusAndSentAtGreaterThanOrderBySentAtAsc(
        String recipient, 
        SmsJob.Status status, 
        Long sentAt
    );

    List<SmsJob> findByStatusOrderByCreatedAtAsc(SmsJob.Status status);
		
	List<SmsJob> findByRecipientAndStatusAndSentAtGreaterThanOrderBySentAtAsc(
		String recipient,
		SmsJob.Status status,
		Long sentAt
	);
}
