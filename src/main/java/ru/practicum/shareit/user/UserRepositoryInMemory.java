package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.ConflictException;

import java.util.*;

@Repository
public class UserRepositoryInMemory implements UserRepository {
    private final Map<Integer, User> users = new HashMap<>();
    private final Set<String> usersEmails = new HashSet<>();
    private int nextId = 1;

    @Override
    public List<User> findAll() {
        return users.values().stream()
                .map(this::getUserClone)
                .toList();
    }

    @Override
    public Optional<User> findById(Integer id) {
        User user = users.get(id);
        return Optional.ofNullable(user == null ? null : getUserClone(user));
    }

    @Override
    public User save(User user) {
        if (user.getId() == null) {
            if (usersEmails.contains(user.getEmail())) {
                throw new ConflictException("Email is taken");
            }
            user.setId(nextId++);
        } else {
            String currentEmail = users.get(user.getId()).getEmail();
            if (!user.getEmail().equals(currentEmail)) {
                if (usersEmails.contains(user.getEmail())) {
                    throw new ConflictException("Email is taken");
                }
                usersEmails.remove(currentEmail);
            }
        }
        usersEmails.add(user.getEmail());
        users.put(user.getId(), user);
        return getUserClone(user);
    }

    @Override
    public void deleteById(Integer id) {
        if (users.containsKey(id)) {
            usersEmails.remove(users.get(id).getEmail());
            users.remove(id);
        }
    }

    private User getUserClone(User user) {
        return new User(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }
}
