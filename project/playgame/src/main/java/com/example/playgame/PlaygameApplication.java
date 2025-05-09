package com.example.playgame;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.example.playgame.repository")
public class PlaygameApplication {

	public static void main(String[] args) {
		SpringApplication.run(PlaygameApplication.class, args);
	}
}
