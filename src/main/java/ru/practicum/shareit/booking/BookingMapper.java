package ru.practicum.shareit.booking;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.ItemBookingDto;
import ru.practicum.shareit.booking.dto.UserBookingDto;

@Mapper(componentModel = "spring")
public interface BookingMapper {

    BookingDto toBookingDto(Booking booking);

    @Mapping(source = "booker.id", target = "bookerId")
    @Mapping(source = "id", target = "id")
    ItemBookingDto toItemBookingDto(Booking booking);

    UserBookingDto toUserBookingDto(Booking booking);

    @Mapping(source = "itemId", target = "item.id")
    Booking toBookingModel(BookingDto bookingDto);

}
