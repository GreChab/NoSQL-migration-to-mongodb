package ua.epam.mishchenko.ticketbooking.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Reference;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "tickets")
public class TicketMongo {
    @Id
    private String id;
    @Reference
    private EventMongo event;
    private UserMongo user;
    private int place;
    private Category category;
}
