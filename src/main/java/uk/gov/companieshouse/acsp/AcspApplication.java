package uk.gov.companieshouse.acsp;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import java.util.TimeZone;

@SpringBootApplication
@ComponentScan("uk.gov.companieshouse.acsp.*")
public class AcspApplication {

	public static final String APP_NAMESPACE = "acsp-api";
	public static void main(String[] args) {
		SpringApplication.run(AcspApplication.class, args);
	}

	@PostConstruct
	public void init() {
		// This is to prevent times being out of time by an hour during British Summer Time in MongoDB
		// MongoDB stores UTC datetime, and LocalDate doesn't contain timezone
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}
}
