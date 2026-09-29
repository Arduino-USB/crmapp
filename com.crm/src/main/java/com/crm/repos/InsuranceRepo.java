package com.crm.repos;
import com.crm.entites.Insurance;
import com.crm.entites.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

public interface InsuranceRepo extends JpaRepository<Insurance, Long> {
	List<Insurance> findByStatus(Insurance.InsuranceStatus status);
		
	@Query("""
		SELECT u
		FROM User u
		WHERE u.primaryInsuranceFile.id = :insuranceId
		   OR u.secondaryInsuranceFile.id = :insuranceId
	""")
	Optional<User> findUserByInsuranceId(@Param("insuranceId") Long insuranceId);


	@Query("""
		SELECT CASE
			WHEN u.primaryInsuranceFile.id = :insuranceId THEN 'primary'
			WHEN u.secondaryInsuranceFile.id = :insuranceId THEN 'secondary'
			ELSE NULL
		END
		FROM User u
		WHERE u.primaryInsuranceFile.id = :insuranceId
		   OR u.secondaryInsuranceFile.id = :insuranceId
	""")
	Optional<String> findInsuranceSlot(@Param("insuranceId") Long insuranceId);

}



