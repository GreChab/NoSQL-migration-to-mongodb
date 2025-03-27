package ua.epam.mishchenko.ticketbooking.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Reference;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketMongo {
    private String id;
    @Reference
    private EventMongo event;
    private UserMongo user;
    private int place;
    private Category category;
}
