package com.gettasksdone.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan({"com.gettasksdone.controller", "com.gettasksdone.configuration", "com.gettasksdone.auth", "com.gettasksdone.jwt"})
@EntityScan("com.gettasksdone.model")
@EnableJpaRepositories("com.gettasksdone.repository")
public class ServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServerApplication.class, args);
	}

}
