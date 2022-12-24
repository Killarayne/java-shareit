package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
@Transactional
public class BookingServiceImpl implements BookingSerivce {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    @Override

    public Booking createBooking(Long userId, Booking booking) {
        booking.setBooker(new User());
        booking.getBooker().setId(userId);
        booking.setStatus(Status.WAITING);

        if (!userRepository.findById(userId).isPresent()) {
            log.warn("User with id: " + userId + "not exist");
            throw new UserNotFoundException();
        }

        Optional<Item> item = itemRepository.findById(booking.getItem().getId());

        if (!item.isPresent()) {
            log.warn("Item is not exist");
            throw new ItemNotExistsException();
        }

        if (item.get().getOwnerId() == userId) {
            log.warn("Owner can't create booking");
            throw new UserNotFoundException();
        }


        if (!item.get().getAvailable()) {
            log.warn("Can't create item with unavailable status");
            throw new NotAvailableItemException();
        }

        if (booking.getEnd().isBefore(booking.getStart())) {
            log.warn("End time of booking can't be before start time");
            throw new WrongTimeException();
        }

        booking.setItem(item.get());
        log.debug("Booking added");
        return bookingRepository.save(booking);
    }

    @Override
    public Booking approve(Long userId, Long id, Boolean approve) {
        Optional<Booking> booking = bookingRepository.findById(id);


        if (booking.isPresent()) {
            if (booking.get().getStatus().equals(Status.APPROVED)) {
                log.warn("Can't approve booking with status approved");
                throw new WrongBookingStatusException();
            }
            if (booking.get().getItem().getOwnerId() == userId) {
                if (approve) {
                    log.debug("status checkout to approved");
                    booking.get().setStatus(Status.APPROVED);
                } else {
                    log.debug("status checkout to rejected");
                    booking.get().setStatus(Status.REJECTED);
                }
            } else {
                log.warn("Booking can't approve by not owner");
                throw new UserNotFoundException();
            }
        } else {
            log.warn("Booking is not exist");
            throw new BookingNotExistException();
        }
        log.debug("Booking approved");
        return bookingRepository.save(booking.get());


    }


    @Override
    public Booking getBooking(Long userId, Long bookingId) {
        Optional<Booking> booking = bookingRepository.findById(bookingId);
        if (!booking.isPresent()) {
            log.warn("Booking is not exist");
            throw new BookingNotExistException();
        }

        if (booking.get().getBooker().getId() == userId || booking.get().getItem().getOwnerId() == userId) {
            log.debug("Received booking with id: " + bookingId);
            return booking.get();
        } else {
            log.warn("Booking can be received only by booker or owner");
            throw new UserNotFoundException();
        }

    }


    @Override
    public List<Booking> getBookingsByBooker(Long userId, State state) {
        if (!userRepository.findById(userId).isPresent()) {
            log.warn("Booker is not found");
            throw new UserNotFoundException();
        }
        log.debug("Received bookings by booker id: " + userId);
        return mapListToListWithState(bookingRepository.findBookingsByBooker_Id(userId), state);

    }

    @Override
    public List<Booking> getBookingsByOwner(Long ownerId, State state) {
        if (!userRepository.findById(ownerId).isPresent()) {
            log.warn("Owner is not found");
            throw new UserNotFoundException();
        }
        log.debug("Received bookings by owner id: " + ownerId);
        return mapListToListWithState(bookingRepository.findBookingsByOwner_Id(ownerId), state);

    }


    private List<Booking> mapListToListWithState(List<Booking> listToMap, State state) {
        switch (state) {
            case CURRENT:
                return listToMap.stream()
                        .filter(x -> x.getStart().isBefore(LocalDateTime.now()) && x.getEnd().isAfter(LocalDateTime.now()))
                        .sorted(Comparator.comparing(Booking::getStart).reversed()).collect(Collectors.toList());
            case FUTURE:
                return listToMap.stream()
                        .filter(x -> x.getStart().isAfter(LocalDateTime.now()))
                        .sorted(Comparator.comparing(Booking::getStart).reversed()).collect(Collectors.toList());
            case PAST:
                return listToMap.stream()
                        .filter(x -> x.getEnd().isBefore(LocalDateTime.now()))
                        .sorted(Comparator.comparing(Booking::getStart).reversed()).collect(Collectors.toList());
            case WAITING:
                return listToMap.stream()
                        .filter(x -> x.getStatus().equals(Status.WAITING))
                        .sorted(Comparator.comparing(Booking::getStart).reversed()).collect(Collectors.toList());
            case REJECTED:
                return listToMap.stream()
                        .filter(x -> x.getStatus().equals(Status.REJECTED))
                        .sorted(Comparator.comparing(Booking::getStart).reversed()).collect(Collectors.toList());
            default:
                return listToMap.stream()
                        .sorted(Comparator.comparing(Booking::getStart).reversed()).collect(Collectors.toList());

        }
    }

}
