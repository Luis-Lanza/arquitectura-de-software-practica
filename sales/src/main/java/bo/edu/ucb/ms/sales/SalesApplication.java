package bo.edu.ucb.ms.sales;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@EnableTransactionManagement
public class SalesApplication {

	public static void main(String[] args) {
		System.out.println("=== Starting Sales Service - SAGA Orchestrator ===");
		SpringApplication.run(SalesApplication.class, args);
	}

}