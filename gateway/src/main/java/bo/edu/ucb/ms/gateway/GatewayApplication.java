package bo.edu.ucb.ms.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class GatewayApplication {

	public static void main(String[] args) {
		System.out.println("=== Starting API Gateway ===");
		SpringApplication.run(GatewayApplication.class, args);
	}

}