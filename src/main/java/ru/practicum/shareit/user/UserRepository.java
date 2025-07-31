package ru.practicum.shareit.user;


import java.util.List;
import java.util.Optional;

public interface UserRepository {

    List<User> findAll();

    Optional<User> findById(Integer id);

    Optional<User> findByEmail(String email);

    User save(User user);

    void deleteById(Integer id);
}
