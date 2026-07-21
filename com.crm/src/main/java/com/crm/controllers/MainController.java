package com.crm.controllers;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;
import java.util.List;


@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class MainController {

    @GetMapping("/alive")
	public Map<String, String> alive_check() {
		return Map.of("status", "ok");
	}

}
