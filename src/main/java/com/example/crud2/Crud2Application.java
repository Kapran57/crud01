package com.example.crud2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.example.crud2.repository")
public class Crud2Application {

	public static void main(String[] args) {
		SpringApplication.run(Crud2Application.class, args);
	}

}
