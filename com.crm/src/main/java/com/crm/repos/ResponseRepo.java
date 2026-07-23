package com.crm.repos;

import com.crm.entites.Response;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResponseRepo
	extends JpaRepository<Response, Long> {

}
