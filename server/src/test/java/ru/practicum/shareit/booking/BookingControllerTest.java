package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.ShareItConstants.REQ_HEAD_USER_ID;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private MockMvc mvc;

    private final Integer userId = 1;
    private final Integer id = 1;
    private final LocalDateTime localDateTime = LocalDateTime.now();
    private final BookingResponseDto bookingResponseDto = new BookingResponseDto(id, localDateTime, localDateTime, null, null, BookingStatus.WAITING);

    @Test
    void createBooking() throws Exception {
        final BookingCreateDto bookingCreateDto = new BookingCreateDto(id, localDateTime, localDateTime);
        when(bookingService.createBooking(userId, bookingCreateDto)).thenReturn(bookingResponseDto);

        mvc.perform(post("/bookings")
                        .header(REQ_HEAD_USER_ID, userId)
                        .content(mapper.writeValueAsString(bookingCreateDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(bookingResponseDto.id()), Integer.class))
                .andExpect(jsonPath("$.status", is(bookingResponseDto.status().name())));
        verify(bookingService, times(1)).createBooking(anyInt(), any());
    }

    @Test
    void updateBookingStatus() throws Exception {
        when(bookingService.updateBookingStatus(userId, id, true)).thenReturn(bookingResponseDto);

        mvc.perform(patch("/bookings/" + id)
                        .header(REQ_HEAD_USER_ID, userId)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingResponseDto.id()), Integer.class))
                .andExpect(jsonPath("$.status", is(bookingResponseDto.status().name())));
        verify(bookingService, times(1)).updateBookingStatus(anyInt(), anyInt(), anyBoolean());
    }

    @Test
    void getBookingById() throws Exception {
        when(bookingService.getBookingById(userId, id)).thenReturn(bookingResponseDto);

        mvc.perform(get("/bookings/" + id)
                        .header(REQ_HEAD_USER_ID, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingResponseDto.id()), Integer.class))
                .andExpect(jsonPath("$.status", is(bookingResponseDto.status().name())));
        verify(bookingService, times(1)).getBookingById(anyInt(), anyInt());
    }

    @Test
    void getBookingsByUserId() throws Exception {
        when(bookingService.getBookingsByUserId(userId, BookingState.ALL)).thenReturn(List.of(bookingResponseDto));

        mvc.perform(get("/bookings")
                        .header(REQ_HEAD_USER_ID, userId)
                        .param("state", BookingState.ALL.name()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingResponseDto.id()), Integer.class))
                .andExpect(jsonPath("$[0].status", is(bookingResponseDto.status().name())));
        verify(bookingService, times(1)).getBookingsByUserId(anyInt(), any());
    }

    @Test
    void getBookingsByOwnerId() throws Exception {
        when(bookingService.getBookingsByOwnerId(userId, BookingState.ALL)).thenReturn(List.of(bookingResponseDto));

        mvc.perform(get("/bookings/owner")
                        .header(REQ_HEAD_USER_ID, userId)
                        .param("state", BookingState.ALL.name()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingResponseDto.id()), Integer.class))
                .andExpect(jsonPath("$[0].status", is(bookingResponseDto.status().name())));
        verify(bookingService, times(1)).getBookingsByOwnerId(anyInt(), any());
    }
}