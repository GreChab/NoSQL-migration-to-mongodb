package ua.epam.mishchenko.ticketbooking;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ua.epam.mishchenko.ticketbooking.service.DatabaseMigrationService;

@SpringBootApplication
@RequiredArgsConstructor
public class TicketBookingApp implements CommandLineRunner {
    private final DatabaseMigrationService databaseMigrationService;

    public static void main(String[] args) {
        SpringApplication.run(TicketBookingApp.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        databaseMigrationService.migrate();
    }
}
