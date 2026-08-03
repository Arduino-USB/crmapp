package com.crm.controllers;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.crm.TemplateRenderer;
import com.crm.ConnectionLoop;

import java.util.Map;
import java.util.List;


@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class MainController {
	TemplateRenderer renderer = new TemplateRenderer("templates");
	
	@GetMapping("/")
	public String index() {
		return renderer.render("index", Map.of());
	}


    @GetMapping("/alive")
	public Map<String, String> alive_check() {
		return Map.of("status", "ok");
	}

	@GetMapping("/phone_connected")
	public Map<String, Boolean> phone_connected() {
		return Map.of("status", ConnectionLoop.connected());	
	}

}
