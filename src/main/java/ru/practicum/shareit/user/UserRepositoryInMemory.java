package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class UserRepositoryInMemory implements UserRepository {
    private final Map<Integer, User> users = new HashMap<>();
    private Integer nextId = 1;

    @Override
    public List<User> findAll() {
        return users.values().stream().toList();
    }

    @Override
    public Optional<User> findById(Integer id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return users.values().stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst();
    }

    @Override
    public User save(User user) {
        if (user.getId() == null) {
            user.setId(nextId);
            users.put(nextId, user);
            nextId++;
        } else {
            users.put(user.getId(), user);
        }
        return user;
    }

    @Override
    public void deleteById(Integer id) {
        users.remove(id);
    }
}
