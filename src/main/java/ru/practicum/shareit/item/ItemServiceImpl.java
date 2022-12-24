package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.ItemBookingDto;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ItemServiceImpl implements ItemService {
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final BookingRepository bookingRepository;
    private final ItemMapper itemMapper;
    private final BookingMapper bookingMapper;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public Item createItem(Item item) {
        if (!item.getAvailable()) {
            log.warn("Can't create an item unavailable");
            throw new NotAvailableItemException();
        }
        if (userRepository.findAll().stream().noneMatch(x -> x.getId().equals(item.getOwnerId()))) {
            log.warn("User with id " + item.getOwnerId() + " not exist, can't create item");
            throw new UserNotFoundException();
        }
        log.debug("Created item :" + item.getName());
        return itemRepository.save(item);
    }

    @Override
    public Item updateItem(long id, Item item) {
        Item newItem = itemRepository.findById(item.getId()).get();
        if (newItem.getOwnerId() != id) {
            log.warn("User with id " + id + " not exist, can't create item");
            throw new UserNotFoundException();
        }

        if (item.getAvailable() != null) {
            newItem.setAvailable(item.getAvailable());
        }
        if (item.getName() != null) {
            newItem.setName(item.getName());
        }

        if (item.getDescription() != null) {
            newItem.setDescription(item.getDescription());
        }

        log.debug("Updated item with id: " + item.getId());
        return itemRepository.save(newItem);

    }

    @Override
    public ItemDto getItem(Long userId, Long itemId) {
        Optional<Item> newItem = itemRepository.findById(itemId);
        if (newItem.isPresent()) {
            if (newItem.get().getOwnerId() == userId) {
                ItemDto itemDto = itemToItemWithLastAndNextBooking(newItem.get());
                itemDto.setComments(commentRepository.findAllByItem_Id(itemDto.getId()).stream()
                        .map(commentMapper::toUserCommentDto).collect(Collectors.toList()));
                log.debug("Received item with id: " + itemId);
                return itemDto;
            } else {
                ItemDto itemDto = itemMapper.toItemDto(newItem.get());
                itemDto.setComments(commentRepository.findAllByItem_Id(itemDto.getId()).stream()
                        .map(commentMapper::toUserCommentDto).collect(Collectors.toList()));
                log.debug("Received item with id: " + itemId);
                return itemDto;
            }
        } else {
            log.warn("Item with id: " + itemId + " is not exist");
            throw new ItemNotExistsException();
        }
    }

    private ItemDto itemToItemWithLastAndNextBooking(Item item) {
        ItemDto itemDto = itemMapper.toItemDto(item);
        List<Booking> bookingList = bookingRepository.findBookingsByItem(item);
        Booking lastBooking = bookingList
                .stream().filter(x -> x.getStart().isBefore(LocalDateTime.now())).findFirst().orElse(null);
        ItemBookingDto lastBookingDto = bookingMapper.toItemBookingDto(lastBooking);
        Booking nextBooking = bookingList
                .stream().filter(x -> x.getStart().isAfter(LocalDateTime.now())).findFirst().orElse(null);
        ItemBookingDto nextBookingDto = bookingMapper.toItemBookingDto(nextBooking);

        itemDto.setLastBooking(lastBookingDto);
        itemDto.setNextBooking(nextBookingDto);
        return itemDto;
    }

    @Override
    public List<ItemDto> getItems(Long userId) {
        log.debug("Received list of items by user id: " + userId);
        if (userId == null) {
            return itemRepository.findAll().stream().map(itemMapper::toItemDto).collect(Collectors.toList());
        } else {
            return itemRepository.findAllByOwnerId(userId).stream().map(x -> itemToItemWithLastAndNextBooking(x)).collect(Collectors.toList());
        }
    }

    @Override
    public List<Item> searchItem(String text) {
        log.debug("Received a list of things containing symbols:" + text);
        if (!text.isBlank()) {
            return itemRepository.searchByText(text);
        } else {
            return new ArrayList<Item>();
        }
    }

    @Override
    public Comment createComment(Long userId, Long itemId, Comment comment) {
        Item item = itemRepository.findById(itemId).get();
        User user = userRepository.findById(userId).get();
        comment.setItem(item);
        comment.setAuthor(user);
        List<Booking> bookingList = bookingRepository.findBookingsByItem(item);
        if (bookingList.stream().filter(x -> !x.getStatus().equals(Status.REJECTED)).collect(Collectors.toList()).size() == 0) {
            log.warn("Can't create comment because not exists bookings with correct status");
            throw new WrongBookingStatusException();
        }

        if (comment.getText().isBlank()) {
            log.warn("Comment text can't be blank");
            throw new WrongTextOfCommentException();
        }

        if (!bookingList.stream().filter(x -> x.getStart().isAfter(LocalDateTime.now())).sorted(Comparator.comparing(Booking::getStart)).findFirst().get().getBooker().getId().equals(userId) ) {
            log.warn("Can't create comment by booker if his booking after when next booking");
            throw new IncorrectCreatorOfComment();
        }
        log.debug("Created comment by user id: " + userId);
        return commentRepository.save(comment);
    }

}
