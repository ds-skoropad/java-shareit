package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Integer> {

    @Query("SELECT i FROM Item i WHERE i.available = TRUE AND "
            + "(LOWER(i.name) LIKE LOWER(CONCAT('%', :text, '%')) OR "
            + "LOWER(i.description) LIKE LOWER(CONCAT('%', :text, '%')))")
    List<Item> searchAvailableByText(@Param("text") String text);

    List<Item> findByOwnerId(Integer ownerId);
}
