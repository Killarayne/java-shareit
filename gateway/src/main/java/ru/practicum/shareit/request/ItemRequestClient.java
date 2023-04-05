package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;

@Service
public class ItemRequestClient extends BaseClient {

    private static final String API_PREFIX = "/requests";

    @Autowired
    public ItemRequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> create(Long requestorId, ItemRequestDto itemDto) {
        return post("", requestorId, null, itemDto);
    }

    public ResponseEntity<Object> getAllByRequestor(Long requestorId) {
        return get("", requestorId);
    }

    public ResponseEntity<Object> getRequestsAll(Long from, Long size, Long userId) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );

        return get("/all/?from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> getById(Long userId, Long requestId) {
        return get("/" + requestId, userId);
    }

}
