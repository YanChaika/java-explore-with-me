package org.example.ewm.event;

import org.example.ewm.event.model.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    Page<Event> findAllByInitiatorId(Long userId, Pageable pageable);

    Event findByIdAndInitiatorId(Long eventId, Long userId);

    List<Event> findAllByInitiatorId(Long userId);

    @Query(
            "select i from Event i " +
                    "where upper(i.annotation) like upper(concat('%', ?1, '%'))" +
                    " or upper(i.description) like upper(concat('%', ?1, '%'))"
    )
    List<Event> findEventsByQuery(String query);

    List<Event> findAllByCategoryId(Long categoryId);
}
