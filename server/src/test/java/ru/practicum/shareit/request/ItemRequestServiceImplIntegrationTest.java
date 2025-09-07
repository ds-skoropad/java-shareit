package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserCreateDto;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestServiceImplIntegrationTest {

    private final UserService userService;
    private final ItemRequestService itemRequestService;

    @Test
    void getItemRequestsByUserId() {
        final String nameUser1 = "user1";
        final String nameUser2 = "user2";
        final String emailUser1 = "user1@domain.com";
        final String emailUser2 = "user2@domain.com";
        final Integer userId1 = userService.createUser(new UserCreateDto(nameUser1, emailUser1)).id();
        final Integer userId2 = userService.createUser(new UserCreateDto(nameUser2, emailUser2)).id();

        final String requestDescription1 = "description1";
        final String requestDescription2 = "description2";
        final Integer requestId1 = itemRequestService.createItemRequest(userId1, new ItemRequestCreateDto(requestDescription1)).id();
        final Integer requestId2 = itemRequestService.createItemRequest(userId2, new ItemRequestCreateDto(requestDescription2)).id();

        List<ItemRequestResponseDto> result = itemRequestService.getItemRequestsByUserId(userId2);

        assertThat(result)
                .hasSize(1)
                .first()
                .hasFieldOrPropertyWithValue("id", requestId2)
                .hasFieldOrPropertyWithValue("description", requestDescription2);
    }
}