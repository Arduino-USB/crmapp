package com.crm.repos;

import com.crm.entites.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {

    Optional<User> findById(Long id);
    Optional<User> findByName(String name);
    Optional<User> findByPhone(String phone);

    // Finds users where phone is NULL, empty, or whitespace only
    @Query("""
        SELECT u
        FROM User u
        WHERE u.phone IS NULL
           OR TRIM(u.phone) = ''
    """)
    List<User> findUsersPhoneBlank();

}
