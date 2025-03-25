package ua.epam.mishchenko.ticketbooking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import ua.epam.mishchenko.ticketbooking.model.EventMongo;

import java.util.Date;

public interface EventMongoRepository extends MongoRepository<EventMongo, String> {

    Page<EventMongo> getAllByTitle(Pageable pageable, String title);

    Page<EventMongo> getAllByDate(Pageable pageable, Date day);

    Boolean existsByTitleAndDate(String title, Date date);
}
