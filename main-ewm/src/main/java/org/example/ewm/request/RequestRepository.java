package org.example.ewm.request;

import org.example.ewm.request.model.Request;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> findAllByRequesterIdOrderByCreatedDesc(Long userId);

    Request findByEventIdAndRequesterId(Long eventId, Long userId);

    List<Request> findAllByEventIdAndRequesterId(Long eventId, Long userId);

    List<Request> findAllByEventId(Long eventId);
}
