package bo.edu.ucb.ms.accounting;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableDiscoveryClient
@EnableTransactionManagement
public class AccountingApplication {

	public static void main(String[] args) {
		System.out.println("=== Starting Accounting Service - Journal Management ===");
		SpringApplication.run(AccountingApplication.class, args);
	}

}