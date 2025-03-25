package ua.epam.mishchenko.ticketbooking.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import ua.epam.mishchenko.ticketbooking.dto.EventDto;
import ua.epam.mishchenko.ticketbooking.dto.TicketDto;
import ua.epam.mishchenko.ticketbooking.dto.UserDto;
import ua.epam.mishchenko.ticketbooking.model.*;
import ua.epam.mishchenko.ticketbooking.repository.*;
import ua.epam.mishchenko.ticketbooking.service.TicketService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Profile(value = "mongo")
@Service
@RequiredArgsConstructor
public class TicketMongoServiceImpl implements TicketService {

    private final UserMongoRepository userRepository;

    private final EventMongoRepository eventRepository;

    private final TicketMongoRepository ticketRepository;

    private final TicketCustomMongoRepository ticketCustomMongoRepository;

    private final UserAccountCustomMongoRepository userAccountCustomRepository;

    /**
     * Book ticket.
     *
     * @param userId   the user id
     * @param eventId  the event id
     * @param place    the place
     * @param category the category
     * @return the ticket
     */
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public TicketDto bookTicket(String userId, String eventId, int place, Category category) {
        try {
            return processBookingTicket(userId, eventId, place, category);
        } catch (RuntimeException e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return null;
        }
    }

    private TicketDto processBookingTicket(String userId, String eventId, int place, Category category) {
        throwRuntimeExceptionIfUserNotExist(userId);
        throwRuntimeExceptionIfEventNotExist(eventId);
        throwRuntimeExceptionIfTicketAlreadyBooked(eventId, place, category);
        UserAccountMongo userAccount = getUserAccount(userId);
        EventDto event = getEvent(eventId);
        throwRuntimeExceptionIfUserNotHaveEnoughMoney(userAccount, event);
        buyTicket(userAccount, event);
        return saveBookedTicket(userId, eventId, place, category);
    }

    private TicketDto saveBookedTicket(String userId, String eventId, int place, Category category) {
        UserMongo user = userRepository.findById(userId).get();
        EventMongo event = eventRepository.findById(eventId).get();
        TicketMongo saveTicked = ticketRepository.save(createNewTicket(user, event, place, category));
        return TicketDto.fromMongoTicket(saveTicked, event, user);
    }

    private void buyTicket(UserAccountMongo userAccount, EventDto event) {
        userAccount.setMoney(subtractTicketPriceFromUserMoney(userAccount, event));
    }

    private BigDecimal subtractTicketPriceFromUserMoney(UserAccountMongo userAccount, EventDto event) {
        return userAccount.getMoney().subtract(event.getTicketPrice());
    }

    private void throwRuntimeExceptionIfUserNotHaveEnoughMoney(UserAccountMongo userAccount, EventDto event) {
        if (!userHasEnoughMoneyForTicket(userAccount, event)) {
            throw new RuntimeException();
        }
    }

    private void throwRuntimeExceptionIfTicketAlreadyBooked(String eventId, int place, Category category) {
        if (ticketCustomMongoRepository.existsByEventAndPlaceAndCategory(String.valueOf(eventId), place, category)) {
            throw new RuntimeException("This ticket already booked");
        }
    }

    private EventDto getEvent(String eventId) {
        return eventRepository.findById(String.valueOf(eventId))
                .map(EventDto::fromEventMongoToEventDto)
                .orElseThrow(() -> new RuntimeException("Can not to find an event by id: " + eventId));
    }

    private UserAccountMongo getUserAccount(String userId) {
        return userAccountCustomRepository.findByUserId(String.valueOf(userId))
                .orElseThrow(() -> new RuntimeException("Can not to find a user account by user id: " + userId));
    }

    private void throwRuntimeExceptionIfEventNotExist(String eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new RuntimeException("The event with id " + eventId + " does not exist");
        }
    }

    private void throwRuntimeExceptionIfUserNotExist(String userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("The user with id " + userId + " does not exist");
        }
    }

    private boolean userHasEnoughMoneyForTicket(UserAccountMongo userAccount, EventDto event) {
        return userAccount.getMoney().compareTo(event.getTicketPrice()) > -1;
    }

    /**
     * Create new ticket.
     *
     * @param user     the user id
     * @param event    the event id
     * @param place    the place
     * @param category the category
     * @return the ticket
     */
    private TicketMongo createNewTicket(UserMongo user, EventMongo event, int place, Category category) {
        TicketMongo ticket = new TicketMongo();
        ticket.setUser(user);
        ticket.setEvent(event);
        ticket.setPlace(place);
        ticket.setCategory(category);
        return ticket;
    }

    /**
     * Gets booked tickets.
     *
     * @param user     the user
     * @param pageSize the page size
     * @param pageNum  the page num
     * @return the booked tickets
     */
    @Override
    public List<TicketDto> getBookedTickets(UserDto user, int pageSize, int pageNum) {
        try {
            if (isUserNull(user)) {
                return new ArrayList<>();
            }
            Page<TicketDto> ticketsByUser = ticketCustomMongoRepository.getAllByUserId(
                    PageRequest.of(pageNum - 1, pageSize),
                    (user.getId()));
            if (!ticketsByUser.hasContent()) {
                throw new RuntimeException("Can not to fina a list of booked tickets by user with id: " + user.getId());
            }
            return ticketsByUser.getContent();
        } catch (RuntimeException e) {
            return new ArrayList<>();
        }
    }

    /**
     * Is user null boolean.
     *
     * @param user the user
     * @return the boolean
     */
    private boolean isUserNull(UserDto user) {
        return user == null;
    }

    /**
     * Gets booked tickets.
     *
     * @param event    the event
     * @param pageSize the page size
     * @param pageNum  the page num
     * @return the booked tickets
     */
    @Override
    public List<TicketDto> getBookedTickets(EventDto event, int pageSize, int pageNum) {
        try {
            if (isEventNull(event)) {
                return new ArrayList<>();
            }
            Page<TicketDto> ticketsByEvent = ticketCustomMongoRepository.getAllByEventId(
                    PageRequest.of(pageNum - 1, pageSize), event.getId());
            if (!ticketsByEvent.hasContent()) {
                throw new RuntimeException("Can not to fina a list of booked tickets by event with id: " + event.getId());
            }
            return ticketsByEvent.getContent();
        } catch (RuntimeException e) {
            return new ArrayList<>();
        }
    }

    /**
     * Is event null boolean.
     *
     * @param event the event
     * @return the boolean
     */
    private boolean isEventNull(EventDto event) {
        return event == null;
    }

    /**
     * Cancel ticket boolean.
     *
     * @param ticketId the ticket id
     * @return the boolean
     */
    @Override
    public boolean cancelTicket(String ticketId) {
        try {
            ticketRepository.deleteById(String.valueOf(ticketId));
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }
}
