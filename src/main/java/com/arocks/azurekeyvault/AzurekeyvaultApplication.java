package com.arocks.azurekeyvault;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Value;

@SpringBootApplication
@RestController
public class AzurekeyvaultApplication {

	// connectionString mapped to Name in Azure key vault
	@Value("${connectionString}")
    private String connectionString = "defaultValue\n";
	
	public static void main(String[] args) {
		SpringApplication.run(AzurekeyvaultApplication.class, args);
	}
	
	// get method
	@GetMapping("get")
    public String get() {
        return connectionString;
    }


    public void run(String... varl) throws Exception {
        System.out.println(String.format("\nConnection String stored in Azure Key Vault:\n%s\n",connectionString));
    }

}
