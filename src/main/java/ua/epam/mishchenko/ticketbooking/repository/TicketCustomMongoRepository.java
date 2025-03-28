package ua.epam.mishchenko.ticketbooking.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import ua.epam.mishchenko.ticketbooking.dto.TicketDto;
import ua.epam.mishchenko.ticketbooking.model.Category;
import ua.epam.mishchenko.ticketbooking.model.EventMongo;
import ua.epam.mishchenko.ticketbooking.model.TicketMongo;
import ua.epam.mishchenko.ticketbooking.model.UserMongo;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class TicketCustomMongoRepository {

    private final TicketMongoRepository ticketRepository;
    private final EventMongoRepository eventRepository;
    private final UserMongoRepository userRepository;

    public Page<TicketDto> getAllByEventId(Pageable pageable, String eventId) {
        EventMongo event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event does not exist for id: " + eventId));

        Page<TicketMongo> pageResult = ticketRepository.findByEvent(event, pageable);
        List<TicketDto> dtosList = pageResult.getContent().stream()
                .map(ticket -> TicketDto.fromMongoTicket(ticket, event,  ticket.getUser()))
                .toList();

        return new PageImpl<>(dtosList, pageable, pageResult.getTotalElements());
    }

    public Page<TicketDto> getAllByUserId(Pageable pageable, String userId) {
        UserMongo user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User does not exist for id: " + userId));
        Page<TicketMongo> pageResult = ticketRepository.findByUser(user, pageable);
        List<TicketDto> dtosList = pageResult.getContent().stream()
                .map(ticket -> TicketDto.fromMongoTicket(ticket, ticket.getEvent(),  ticket.getUser()))
                .toList();

        return new PageImpl<>(dtosList, pageable, pageResult.getTotalElements());    }

    public Boolean existsByEventAndPlaceAndCategory(String eventId, Integer place, Category category) {
        EventMongo event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event does not exist for id: " + eventId));
        return ticketRepository.existsByEventAndPlaceAndCategory(event, place, category);
    }
}
