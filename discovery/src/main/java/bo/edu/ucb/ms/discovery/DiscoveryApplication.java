package bo.edu.ucb.ms.discovery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class DiscoveryApplication {

	public static void main(String[] args) {
		System.out.println("=== Starting Eureka Service Discovery Server ===");
		SpringApplication.run(DiscoveryApplication.class, args);
	}

}