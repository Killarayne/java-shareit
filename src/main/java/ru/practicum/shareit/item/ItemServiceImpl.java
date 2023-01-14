package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.ItemBookingDto;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UserCommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    public ItemDto createItem(ItemDto itemDto) {
        Item item = itemMapper.toModel(itemDto);
        if (!item.getAvailable()) {
            log.warn("Can't create an item unavailable");
            throw new NotAvailableItemException();
        }
        if (!userRepository.findById(item.getOwnerId()).isPresent()) {
            log.warn("User with id " + item.getOwnerId() + " not exist, can't create item");
            throw new UserNotFoundException();
        }
        log.debug("Created item :" + item.getName());
        return itemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto updateItem(long id, ItemDto itemDto) {
        Optional<Item> newItem = itemRepository.findById(itemDto.getId());
        if (!newItem.isPresent()) {
            log.warn("Item is not exist");
            throw new ItemNotExistsException();
        }
        if (newItem.get().getOwnerId() != id) {
            log.warn("User with id " + id + " not exist, can't create item");
            throw new UserNotFoundException();
        }

        if (itemDto.getAvailable() != null) {
            newItem.get().setAvailable(itemDto.getAvailable());
        }
        if (itemDto.getName() != null) {
            newItem.get().setName(itemDto.getName());
        }

        if (itemDto.getDescription() != null) {
            newItem.get().setDescription(itemDto.getDescription());
        }

        log.debug("Updated item with id: " + itemDto.getId());
        return itemMapper.toItemDto(itemRepository.save(newItem.get()));

    }

    @Override
    public ItemDto getItem(Long userId, Long itemId) {
        Optional<Item> newItem = itemRepository.findById(itemId);
        if (newItem.isPresent()) {
            if (newItem.get().getOwnerId().equals(userId)) {
                ItemDto itemDto = itemToItemWithLastAndNextBooking(newItem.get());
                itemDto.setComments(commentRepository.findAllByItemId(itemDto.getId()).stream()
                        .map(commentMapper::toUserCommentDto).collect(Collectors.toList()));
                log.debug("Received item with id: " + itemId);
                return itemDto;
            } else {
                ItemDto itemDto = itemMapper.toItemDto(newItem.get());
                itemDto.setComments(commentRepository.findAllByItemId(itemDto.getId()).stream()
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
    public List<ItemDto> getItems(Long userId, Pageable pageable) {
        log.debug("Received list of items by user id: " + userId);
        if (userId == null) {
            return itemRepository.findAll(pageable).stream().map(itemMapper::toItemDto).collect(Collectors.toList());
        } else {
            return itemRepository.findByOwnerId(userId, pageable).stream().map(x -> itemToItemWithLastAndNextBooking(x)).collect(Collectors.toList());
        }
    }

    @Override
    public List<ItemDto> searchItem(String text, Pageable pageable) {
        log.debug("Received a list of things containing symbols:" + text);
        if (!text.isBlank()) {
            return itemRepository.searchByText(text, pageable).stream().map(itemMapper::toItemDto).collect(Collectors.toList());
        } else {
            return new ArrayList<ItemDto>();
        }
    }

    @Override
    public UserCommentDto createComment(Long userId, Long itemId, CommentDto commentDto) {
        Comment comment = commentMapper.toCommentModel(commentDto);

        Optional<Item> item = itemRepository.findById(itemId);
        Optional<User> user = userRepository.findById(userId);
        if (!item.isPresent()) {
            throw new ItemNotExistsException();
        }
        if (!user.isPresent()) {
            throw new UserNotFoundException();
        }
        comment.setAuthor(user.get());
        comment.setItem(item.get());

        if (bookingRepository.findBookingsCountByItemIdWhereStatusIsNotRejected(itemId) == 0) {
            log.warn("Can't create comment because not exists bookings with correct status");
            throw new WrongBookingStatusException();
        }

        if (comment.getText().isBlank()) {
            log.warn("Comment text can't be blank");
            throw new WrongTextOfCommentException();
        }

        if (!bookingRepository.findBookingsByItemIdAndStartIsAfterOrderByStartAsc(itemId, LocalDateTime.now()).stream().findFirst().get().getBooker().getId().equals(userId)) {
            log.warn("Can't create comment by booker if his booking after when next booking");
            throw new IncorrectCreatorOfComment();
        }
        log.debug("Created comment by user id: " + userId);
        return commentMapper.toUserCommentDto(commentRepository.save(comment));
    }

}
