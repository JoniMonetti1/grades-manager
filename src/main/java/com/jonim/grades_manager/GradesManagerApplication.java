package com.jonim.grades_manager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = "com.jonim.grades_manager.models")
public class GradesManagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(GradesManagerApplication.class, args);
	}

}
