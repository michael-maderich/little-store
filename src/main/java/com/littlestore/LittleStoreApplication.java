package com.littlestore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import com.littlestore.config.GmailProperties;

@SpringBootApplication
@EnableConfigurationProperties(GmailProperties.class)
public class LittleStoreApplication extends SpringBootServletInitializer{

	public static void main(String[] args) {
		SpringApplication.run(LittleStoreApplication.class, args);
	}
	
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(LittleStoreApplication.class);
	}

}
