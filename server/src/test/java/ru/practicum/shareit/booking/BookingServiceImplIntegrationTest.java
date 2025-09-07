package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserCreateDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceImplIntegrationTest {

    private final UserService userService;
    private final ItemService itemService;
    private final BookingService bookingService;

    @Test
    void getBookingsByUserId() {
        final String nameUser1 = "user1";
        final String nameUser2 = "user2";
        final String emailUser1 = "user1@domain.com";
        final String emailUser2 = "user2@domain.com";
        final Integer userId1 = userService.createUser(new UserCreateDto(nameUser1, emailUser1)).id();
        final Integer userId2 = userService.createUser(new UserCreateDto(nameUser2, emailUser2)).id();

        final String itemName1 = "item1";
        final String itemName2 = "item2";
        final String itemDescription1 = "description1";
        final String itemDescription2 = "description2";
        final Integer itemId1 = itemService.createItem(userId1, new ItemCreateDto(itemName1, itemDescription1, true, null)).id();
        final Integer itemId2 = itemService.createItem(userId2, new ItemCreateDto(itemName2, itemDescription2, true, null)).id();

        final LocalDateTime localDateTime = LocalDateTime.of(2025, 1, 1, 12, 0);

        final Integer bookingId1 = bookingService.createBooking(userId1, new BookingCreateDto(itemId2, localDateTime, localDateTime)).id();
        final Integer bookingId2 = bookingService.createBooking(userId2, new BookingCreateDto(itemId1, localDateTime, localDateTime)).id();

        List<BookingResponseDto> result = bookingService.getBookingsByUserId(userId1, BookingState.ALL);

        assertThat(result)
                .hasSize(1)
                .first()
                .hasFieldOrPropertyWithValue("id", bookingId1)
                .hasFieldOrPropertyWithValue("start", localDateTime)
                .hasFieldOrPropertyWithValue("end", localDateTime)
                .hasFieldOrPropertyWithValue("status", BookingStatus.WAITING)
                .extracting("item")
                .hasFieldOrPropertyWithValue("id", itemId2)
                .hasFieldOrPropertyWithValue("name", itemName2);
        assertThat(result)
                .first()
                .extracting("booker")
                .hasFieldOrPropertyWithValue("id", userId1)
                .hasFieldOrPropertyWithValue("name", nameUser1)
                .hasFieldOrPropertyWithValue("email", emailUser1);
    }
}