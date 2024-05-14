package com.banking.jbs;

import org.springframework.boot.SpringApplication;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;

@SpringBootApplication
@OpenAPIDefinition(
		info = @Info(
				title = "The Java Banking System App",
				description = "Backend Rest APIs fot TBS Bank",
				version = "v1.0",
				contact = @Contact(
						name = "Rohan Nule",
						email = "rknule45@gmail.com",
						url = "https://github.com/RkNule/tbs_bank_app"
						),
				license = @License(
						name = "The Java Banking System",
						url = "https://github.com/RkNule/tbs_bank_app"
						)
				),
		externalDocs = @ExternalDocumentation(
				description = "The Java Bank App Documentation",
				url = "https://github.com/RkNule/tbs_bank_app"
				)
		
		)
	



public class TheJavaBankingSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(TheJavaBankingSystemApplication.class, args);
	}

}
