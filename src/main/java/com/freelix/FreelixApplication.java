package com.freelix;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class FreelixApplication {
    public static void main(String[] args) {
        SpringApplication.run(FreelixApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onReady() {
        System.out.println("\n=================================================");
        System.out.println("🚀 Freelix Application Started Successfully!");
        System.out.println("👉 Click here to browse: http://localhost:8080");
        System.out.println("=================================================\n");
    }
}
