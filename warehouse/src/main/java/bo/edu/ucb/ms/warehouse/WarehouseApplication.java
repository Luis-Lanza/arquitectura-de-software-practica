package bo.edu.ucb.ms.warehouse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableDiscoveryClient
@EnableTransactionManagement
public class WarehouseApplication {

	public static void main(String[] args) {
		System.out.println("=== Starting Warehouse Service ===");
		SpringApplication.run(WarehouseApplication.class, args);
	}

}