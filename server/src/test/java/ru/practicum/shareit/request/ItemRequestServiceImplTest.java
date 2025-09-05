package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    private final LocalDateTime localDateTime = LocalDateTime.now();
    private User user;
    private ItemRequest itemRequest;
    private ItemRequestCreateDto itemRequestCreateDto;

    @BeforeEach
    void setUp() {
        Integer id = 1;
        String name = "name";
        String description = "test-description";
        String email = "email@domain.com";
        user = new User(id, name, email);
        itemRequest = new ItemRequest(id, description, user, localDateTime, List.of());
        itemRequestCreateDto = new ItemRequestCreateDto(description);
    }


    @Test
    void createItemRequest() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);
        ItemRequestResponseDto result = itemRequestService.createItemRequest(user.getId(), itemRequestCreateDto);

        assertThat(result)
                .hasFieldOrPropertyWithValue("id", itemRequest.getId())
                .hasFieldOrPropertyWithValue("description", itemRequest.getDescription())
                .hasFieldOrPropertyWithValue("created", itemRequest.getCreated());
        verify(userRepository, times(1)).findById(any());
        verify(itemRequestRepository, times(1)).save(any());
    }

    @Test
    void createItemRequest_whenUserIdNotFound_thenException() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemRequestService.createItemRequest(user.getId(), itemRequestCreateDto))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("User not found");
        verify(userRepository, times(1)).findById(any());
        verify(itemRequestRepository, never()).save(any());
    }

    @Test
    void getItemRequestsByUserId() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findByRequestorIdOrderByCreatedDesc(user.getId())).thenReturn(List.of(itemRequest));
        List<ItemRequestResponseDto> result = itemRequestService.getItemRequestsByUserId(user.getId());

        assertThat(result)
                .first()
                .hasFieldOrPropertyWithValue("id", itemRequest.getId())
                .hasFieldOrPropertyWithValue("description", itemRequest.getDescription())
                .hasFieldOrPropertyWithValue("created", itemRequest.getCreated());
        verify(userRepository, times(1)).findById(any());
        verify(itemRequestRepository, times(1)).findByRequestorIdOrderByCreatedDesc(any());

    }

    @Test
    void getItemRequestsByUserId_whenUserIdNotFound_thenException() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemRequestService.getItemRequestsByUserId(user.getId()))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("User not found");
        verify(userRepository, times(1)).findById(any());
        verify(itemRequestRepository, never()).findByRequestorIdOrderByCreatedDesc(any());
    }

    @Test
    void getItemRequests() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findByRequestorIdNotOrderByCreatedDesc(user.getId())).thenReturn(List.of(itemRequest));
        List<ItemRequestResponseDto> result = itemRequestService.getItemRequests(user.getId());

        assertThat(result)
                .first()
                .hasFieldOrPropertyWithValue("id", itemRequest.getId())
                .hasFieldOrPropertyWithValue("description", itemRequest.getDescription())
                .hasFieldOrPropertyWithValue("created", itemRequest.getCreated());
        verify(userRepository, times(1)).findById(any());
        verify(itemRequestRepository, times(1)).findByRequestorIdNotOrderByCreatedDesc(any());
    }

    @Test
    void getItemRequests_whenUserIdNotFound_thenException() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemRequestService.getItemRequests(user.getId()))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("User not found");
        verify(userRepository, times(1)).findById(any());
        verify(itemRequestRepository, never()).findByRequestorIdNotOrderByCreatedDesc(any());
    }

    @Test
    void getItemRequestById() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(itemRequest.getId())).thenReturn(Optional.ofNullable(itemRequest));
        ItemRequestResponseDto result = itemRequestService.getItemRequestById(user.getId(), itemRequest.getId());

        assertThat(result)
                .hasFieldOrPropertyWithValue("id", itemRequest.getId())
                .hasFieldOrPropertyWithValue("description", itemRequest.getDescription())
                .hasFieldOrPropertyWithValue("created", itemRequest.getCreated());
        verify(userRepository, times(1)).findById(any());
        verify(itemRequestRepository, times(1)).findById(any());
    }

    @Test
    void getItemRequestById_whenUserIdNotFound_thenException() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemRequestService.getItemRequestById(user.getId(), itemRequest.getId()))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("User not found");
        verify(userRepository, times(1)).findById(any());
        verify(itemRequestRepository, never()).findById(any());
    }

    @Test
    void getItemRequestById_whenItemRequestIdNotFound_thenException() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.ofNullable(user));
        when(itemRequestRepository.findById(itemRequest.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemRequestService.getItemRequestById(user.getId(), itemRequest.getId()))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("ItemRequest not found");
        verify(userRepository, times(1)).findById(any());
        verify(itemRequestRepository, times(1)).findById(any());
    }
}