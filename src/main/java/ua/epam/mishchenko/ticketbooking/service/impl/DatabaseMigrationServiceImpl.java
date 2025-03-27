package ua.epam.mishchenko.ticketbooking.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.epam.mishchenko.ticketbooking.model.*;
import ua.epam.mishchenko.ticketbooking.repository.EventRepository;
import ua.epam.mishchenko.ticketbooking.service.DatabaseMigrationService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DatabaseMigrationServiceImpl implements DatabaseMigrationService {
    @Value("${properties.migration_enabled}")
    private boolean migrationEnabled;
    private final EventRepository eventRepository;
    private final MongoTemplate mongoTemplate;

    @Override
    @Transactional
    public void migrate() {
        if (Boolean.FALSE.equals(migrationEnabled)) {
            return;
        }

        Iterable<Event> events = eventRepository.findAll();

        List<EventMongo> mongoEvents = new ArrayList<>();

        events.forEach(event -> {
            EventMongo mongoEvent = createMongoEvent(event);
            mongoEvents.add(mongoEvent);
            mongoTemplate.save(mongoEvent, "events");
        });
    }

    private EventMongo createMongoEvent(Event sqlEvent) {
        EventMongo mongoEvent = new EventMongo();
        mongoEvent.setTitle(sqlEvent.getTitle());
        mongoEvent.setTicketPrice(sqlEvent.getTicketPrice());
        mongoEvent.setDate(sqlEvent.getDate());
        mongoEvent.setTicketPrice(sqlEvent.getTicketPrice());

        var mongoTickets = sqlEvent.getTickets().stream()
                .map(this::createMongoTicket)
                .toList();

        mongoEvent.setTickets(mongoTickets);
        return mongoEvent;
    }

    private TicketMongo createMongoTicket(Ticket sqlTicket) {
        var mongoTicket = new TicketMongo();
        mongoTicket.setPlace(sqlTicket.getPlace());
        mongoTicket.setCategory(sqlTicket.getCategory());

        var user = sqlTicket.getUser();
        var mongoUser = new UserMongo();
        mongoUser.setName(user.getName());
        mongoUser.setEmail(user.getEmail());

        mongoTicket.setUser(mongoUser);

        var userAccount = Optional.ofNullable(user.getUserAccount());
        if (userAccount.isPresent()) {
            var mongoUserAccount = new UserAccountMongo();
            mongoUserAccount.setMoney(userAccount.get().getMoney());
            mongoUser.setUserAccount(mongoUserAccount);
        }
        return mongoTicket;
    }
}
