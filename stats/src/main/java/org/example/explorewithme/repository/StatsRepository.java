package org.example.explorewithme.repository;

import org.example.explorewithme.dto.ViewStatsDto;
import org.example.explorewithme.model.EndpointHit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<EndpointHit, Long> {

    @Query(value = "SELECT new org.example.explorewithme.dto.ViewStatsDto(it.app, it.uri, COUNT(*)) " +
            "FROM EndpointHit AS it " +
            "WHERE it.timestamp BETWEEN ?1 and ?2 " +
            "GROUP BY it.app, it.uri")
    List<ViewStatsDto> findAllByStartIsBeforeAndEndIsAfter(LocalDateTime start, LocalDateTime end);

    @Query(value = "SELECT new org.example.explorewithme.dto.ViewStatsDto(it.app, it.uri, COUNT(it.ip)) " +
            "FROM EndpointHit AS it " +
            "WHERE it.timestamp BETWEEN ?1 and ?2 " +
            "GROUP BY it.app, it.uri")
    List<ViewStatsDto> findAllByStartIsBeforeAndEndIsAfterUnique(LocalDateTime start, LocalDateTime end);

    @Query(value = "SELECT new org.example.explorewithme.dto.ViewStatsDto(it.app, it.uri, COUNT(DISTINCT it.ip)) " +
            "FROM EndpointHit AS it " +
            "WHERE it.uri LIKE ?1 AND it.timestamp BETWEEN ?2 AND ?3 " +
            "ORDER BY COUNT(DISTINCT it.ip) DESC")
    List<ViewStatsDto> findAllByUrisFromStartIsBeforeAndEndIsAfterUnique(
            String uris,
            LocalDateTime start,
            LocalDateTime end
    );

    @Query(value = "SELECT new org.example.explorewithme.dto.ViewStatsDto(it.app, it.uri, COUNT(*)) " +
            "FROM EndpointHit AS it " +
            "WHERE it.uri LIKE ?1 AND it.timestamp BETWEEN ?2 AND ?3")
    List<ViewStatsDto> findAllByUrisFromStartIsBeforeAndEndIsAfter(
            String uris,
            LocalDateTime start,
            LocalDateTime end
    );
}
