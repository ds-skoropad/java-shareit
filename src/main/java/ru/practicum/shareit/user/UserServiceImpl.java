package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toUserResponseDto)
                .toList();
    }

    @Override
    public UserResponseDto getUserById(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User not found: id = %d", userId)));
        return UserMapper.toUserResponseDto(user);
    }

    @Override
    public UserResponseDto createUser(UserCreateDto userCreateDto) {
        if (userRepository.findByEmail(userCreateDto.email()).isPresent()) {
            throw new ConflictException("Email is taken");
        }
        User createUser = userRepository.save(UserMapper.toUser(userCreateDto));
        log.info("Create user: {}", createUser);
        return UserMapper.toUserResponseDto(createUser);
    }

    @Override
    public UserResponseDto updateUser(UserUpdateDto userUpdateDto) {
        User updateUser = userRepository.findById(userUpdateDto.id())
                .orElseThrow(() -> new NotFoundException(String.format("User not found: id = %d", userUpdateDto.id())));
        if (userUpdateDto.email() != null && userRepository.findByEmail(userUpdateDto.email()).isPresent()) {
            throw new ConflictException("Email is taken");
        }
        UserMapper.updateUser(updateUser, userUpdateDto);
        updateUser = userRepository.save(updateUser);
        log.info("Update user: {}", updateUser);
        return UserMapper.toUserResponseDto(updateUser);
    }

    @Override
    public void deleteUserById(Integer userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User not found: id = %d", userId)));
        itemRepository.deleteByUserId(userId);
        userRepository.deleteById(userId);
        log.info("Delete user: id = {}", userId);
    }
}
