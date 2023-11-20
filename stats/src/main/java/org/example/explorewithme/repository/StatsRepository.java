package org.example.explorewithme.repository;

import org.example.explorewithme.model.EndpointHit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<EndpointHit, Long> {

    List<EndpointHit> findAllByUri(String uri);

    /*@Query(value = "SELECT * " +
            "FROM PUBLIC.endpoint_hit " +
            "WHERE (cast(times_tamp AS date)) BETWEEN ?1 and ?2", nativeQuery = true)
    List<EndpointHit> findAllByStartIsBeforeAndEndIsAfter(LocalDateTime start, LocalDateTime end);
*/
    @Query(value = "SELECT it " +
            "FROM EndpointHit AS it " +
            "WHERE it.timestamp BETWEEN ?1 and ?2")
    List<EndpointHit> findAllByStartIsBeforeAndEndIsAfter(LocalDateTime start, LocalDateTime end);

    /*@Query(value = "SELECT * " +
            "FROM PUBLIC.endpoint_hit " +
            "WHERE (cast(times_tamp AS date)) BETWEEN ?1 and ?2 " +
            "GROUP BY ip", nativeQuery = true)
    List<EndpointHit> findAllByStartIsBeforeAndEndIsAfterUnique(LocalDateTime start, LocalDateTime end);

    @Query(value = "SELECT * " +
            "FROM PUBLIC.endpoint_hit " +
            "WHERE uris LIKE ?1 AND (cast(times_tamp AS date)) BETWEEN ?2 AND ?3 " +
            "GROUP BY ip", nativeQuery = true)
    List<EndpointHit> findAllByUrisFromStartIsBeforeAndEndIsAfterUnique(
            String uris,
            LocalDateTime start,
            LocalDateTime end
    );

    @Query(value = "SELECT * " +
            "FROM PUBLIC.endpoint_hit " +
            "WHERE uris LIKE ?3 AND (cast(times_tamp AS date)) BETWEEN ?1 AND ?2", nativeQuery = true)
    List<EndpointHit> findAllByUrisFromStartIsBeforeAndEndIsAfter(
            LocalDateTime start,
            LocalDateTime end,
            String uris
    );
     */

    @Query(value = "SELECT it " +
            "FROM EndpointHit AS it " +
            "WHERE it.timestamp BETWEEN ?1 and ?2 " +
            "GROUP BY it.ip")
    List<EndpointHit> findAllByStartIsBeforeAndEndIsAfterUnique(LocalDateTime start, LocalDateTime end);

    @Query(value = "SELECT it " +
            "FROM EndpointHit AS it " +
            "WHERE it.uri LIKE ?1 AND it.timestamp BETWEEN ?2 AND ?3 " +
            "GROUP BY it.ip")
    List<EndpointHit> findAllByUrisFromStartIsBeforeAndEndIsAfterUnique(
            String uris,
            LocalDateTime start,
            LocalDateTime end
    );

    @Query(value = "SELECT it " +
            "FROM EndpointHit AS it " +
            "WHERE it.uri LIKE ?1 AND it.timestamp BETWEEN ?2 AND ?3")
    List<EndpointHit> findAllByUrisFromStartIsBeforeAndEndIsAfter(
            String uris,
            LocalDateTime start,
            LocalDateTime end
    );
}
