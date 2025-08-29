package ru.practicum.shareit.comment;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.comment.dto.CommentCreateDto;
import ru.practicum.shareit.comment.dto.CommentResponseDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;

    @Override
    public CommentResponseDto createComment(Integer userId, CommentCreateDto commentCreateDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User not found: id = %d", userId)));
        Item item = itemRepository.findById(commentCreateDto.itemId())
                .orElseThrow(() -> new NotFoundException(String.format("Item not found: id = %d",
                        commentCreateDto.itemId())));

        if (!bookingRepository.existsByBookerIdAndItemIdAndEndBefore(userId, commentCreateDto.itemId(),
                LocalDateTime.now())) {
            throw new ValidationException("User not booking or booking not completed");
        }

        Comment comment = new Comment();
        comment.setText(commentCreateDto.text());
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());

        Comment createComment = commentRepository.save(comment);
        log.info("Create comment: {}", createComment);

        return CommentMapper.toCommentResponseDto(createComment);
    }
}
