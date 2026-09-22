package com.crm.repos;

import com.crm.entites.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {

	Optional<User> findById(Long id);

	Optional<User> findByPhone(String phone);


	/*
	 * Finds users where patient information is incomplete,
	 * insurance information is incomplete, or subscriber
	 * information is incomplete.
	 */
	@Query("""
		SELECT u
		FROM User u
		WHERE u.phone IS NULL
		   OR TRIM(u.phone) = ''

		   OR u.primaryInsuranceName IS NULL
		   OR TRIM(u.primaryInsuranceName) = ''

		   OR u.primaryInsuranceId IS NULL

		   OR u.dob IS NULL

		   OR (
				u.primarySubFirstName IS NOT NULL
				AND TRIM(u.primarySubFirstName) != ''
				AND (
					u.primarySubLastName IS NULL
					OR TRIM(u.primarySubLastName) = ''
					OR u.primarySubDob IS NULL
					OR u.primarySubMemberId IS NULL
				)
		   )

		   OR (
				u.secondarySubFirstName IS NOT NULL
				AND TRIM(u.secondarySubFirstName) != ''
				AND (
					u.secondarySubLastName IS NULL
					OR TRIM(u.secondarySubLastName) = ''
					OR u.secondarySubDob IS NULL
					OR u.secondarySubMemberId IS NULL
				)
		   )
	""")
	List<User> findUsersIncompleteData();


	/*
	 * Partial name search with pagination.
	 */
	@Query("""
		SELECT u
		FROM User u
		WHERE LOWER(
			CONCAT(u.firstName, ' ', u.lastName)
		) LIKE LOWER(
			CONCAT('%', :partName, '%')
		)
	""")
	Page<User> findByNameContainingIgnoreCase(
		@Param("partName") String partName,
		Pageable pageable
	);


	/*
	 * Finds users with complete patient DOB and at least
	 * one complete insurance record.
	 */
	@Query("""
		SELECT u
		FROM User u
		WHERE u.dob IS NOT NULL
		  AND (
				(
					u.primaryInsuranceName IS NOT NULL
					AND TRIM(u.primaryInsuranceName) != ''
					AND u.primaryInsuranceId IS NOT NULL
				)
				OR
				(
					u.secondaryInsuranceName IS NOT NULL
					AND TRIM(u.secondaryInsuranceName) != ''
					AND u.secondaryInsuranceId IS NOT NULL
				)
		  )
	""")
	List<User> findUsersWithCompleteInsurance();
}
