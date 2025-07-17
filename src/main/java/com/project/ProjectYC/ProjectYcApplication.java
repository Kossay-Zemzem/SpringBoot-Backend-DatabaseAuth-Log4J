package com.project.ProjectYC;

import jakarta.annotation.PreDestroy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@SpringBootApplication

public class ProjectYcApplication {
	private static final Logger sessionLogger = LogManager.getLogger("com.project.ProjectYC") ;
	private static final String SESSION_ID = generateSessionId();


	public static void main(String[] args) {
		// Set the session ID as a system property so Log4j can use it
		System.setProperty("sessionId", SESSION_ID);
		sessionLogger.info("=== APPLICATION STARTUP ===");
		sessionLogger.info("Session ID: {}", System.getProperty("sessionId"));

		SpringApplication.run(ProjectYcApplication.class, args);
	}
	@PreDestroy
	public void onShutdown() {
		sessionLogger.info("=== APPLICATION SHUTDOWN ===");
	}
	private static String generateSessionId() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
		return LocalDateTime.now().format(formatter);
	}
}
