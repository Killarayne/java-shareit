package ru.practicum.shareit.request;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto createRequest(ItemRequestDto itemRequestDto, long userId);

    List<ItemRequestDto> getRequestsByRequestor(long userId);

    List<ItemRequestDto> getRequestsAll(Pageable pageable, long userId);

    ItemRequestDto getRequest(long requestId, long userId);

}
