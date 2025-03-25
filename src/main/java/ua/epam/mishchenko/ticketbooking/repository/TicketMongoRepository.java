package ua.epam.mishchenko.ticketbooking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import ua.epam.mishchenko.ticketbooking.model.Category;
import ua.epam.mishchenko.ticketbooking.model.EventMongo;
import ua.epam.mishchenko.ticketbooking.model.TicketMongo;
import ua.epam.mishchenko.ticketbooking.model.UserMongo;

public interface TicketMongoRepository extends MongoRepository<TicketMongo, String> {

    Page<TicketMongo> findByEvent(EventMongo event, Pageable pageable);

    Page<TicketMongo> findByUser(UserMongo user, Pageable pageable);

    Boolean existsByEventAndPlaceAndCategory(EventMongo event, Integer place, Category category);
}
