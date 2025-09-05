package ru.practicum.shareit.comment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {

    List<Comment> findByItemId(Integer itemId);

    List<Comment> findByItemIdIn(List<Integer> itemIds);
}
