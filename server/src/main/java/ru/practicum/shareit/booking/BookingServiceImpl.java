package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.UserBookingDto;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class BookingServiceImpl implements BookingSerivce {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final BookingMapper bookingMapper;
    private final UserRepository userRepository;

    @Override
    public UserBookingDto createBooking(Long userId, BookingDto bookingDto) {
        Booking booking = bookingMapper.toBookingModel(bookingDto);
        booking.setBooker(new User());
        booking.getBooker().setId(userId);
        booking.setStatus(Status.WAITING);

        if (userRepository.findById(userId).isEmpty()) {
            log.warn("User with id: " + userId + "not exist");
            throw new UserNotFoundException("User with id: " + userId + "not exist");
        }

        Item item = itemRepository.findById(booking.getItem().getId()).orElseThrow(() -> new ItemNotExistsException("Item with id not found" + booking.getItem().getId()));

        if (item.getOwnerId().equals(userId)) {
            log.warn("Owner can't create booking");
            throw new UserNotFoundException("Owner can't create booking");
        }

        if (!item.getAvailable()) {
            log.warn("Can't create item with unavailable status");
            throw new NotAvailableItemException("Can't create item with unavailable status");
        }

        if (booking.getEnd().isBefore(booking.getStart())) {
            log.warn("End time of booking can't be before start time");
            throw new WrongTimeException("End time of booking can't be before start time");
        }

        booking.setItem(item);
        log.debug("Booking added");
        return bookingMapper.toUserBookingDto(bookingRepository.save(booking));
    }

    @Override
    public UserBookingDto approve(Long userId, Long id, Boolean approve) {
        Booking booking = bookingRepository.findById(id).orElseThrow(() -> new BookingNotExistException("Booking is not exist"));
        if (booking.getStatus().equals(Status.APPROVED)) {
            log.warn("Can't approve booking with status approved");
            throw new WrongBookingStatusException("Can't approve booking with status approved");
        }
        if (booking.getItem().getOwnerId().equals(userId)) {
            if (approve) {
                log.debug("status checkout to approved");
                booking.setStatus(Status.APPROVED);
            } else {
                log.debug("status checkout to rejected");
                booking.setStatus(Status.REJECTED);
            }
        } else {
            log.warn("Booking can't approve by not owner");
            throw new UserNotFoundException("Booking can't approve by not owner");
        }

        log.debug("Booking approved");
        return bookingMapper.toUserBookingDto(bookingRepository.save(booking));
    }

    @Override
    public UserBookingDto getBooking(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new BookingNotExistException("Booking is not exist"));
        if (booking.getBooker().getId().equals(userId) || booking.getItem().getOwnerId().equals(userId)) {
            log.debug("Received booking with id: " + bookingId);
            return bookingMapper.toUserBookingDto(booking);
        } else {
            log.warn("Booking can be received only by booker or owner");
            throw new UserNotFoundException("Booking can be received only by booker or owner");
        }
    }

    @Override
    public List<UserBookingDto> getBookingsByBooker(Long userId, State state, Pageable pageable) {
        if (userRepository.findById(userId).isEmpty()) {
            log.warn("Booker is not found");
            throw new UserNotFoundException("Booker is not found");
        }
        log.debug("Received bookings by booker id: " + userId);

        if (state.equals(State.ALL)) {
            return bookingRepository.findAllByBookerIdOrderByStartDesc(userId, pageable).stream().map(bookingMapper::toUserBookingDto).collect(Collectors.toList());
        }
        return mapListToListWithState(bookingRepository.findBookingsByBookerId(userId, pageable), state)
                .stream().map(bookingMapper::toUserBookingDto).collect(Collectors.toList());

    }

    @Override
    public List<UserBookingDto> getBookingsByOwner(Long ownerId, State state, Pageable pageable) {
        if (userRepository.findById(ownerId).isEmpty()) {
            log.warn("Owner is not found");
            throw new UserNotFoundException("Owner is not found");
        }

        if (state.equals(State.ALL)) {
            return bookingRepository.findAllByItemOwnerIdOrderByStartDesc(ownerId, pageable).stream().map(bookingMapper::toUserBookingDto).collect(Collectors.toList());
        }
        log.debug("Received bookings by owner id: " + ownerId);
        return mapListToListWithState(bookingRepository.findBookingsByOwnerId(ownerId, pageable), state)
                .stream().map(bookingMapper::toUserBookingDto).collect(Collectors.toList());
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
        }
        return new ArrayList<>();
    }

}
