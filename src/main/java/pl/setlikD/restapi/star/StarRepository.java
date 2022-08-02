package pl.setlikD.restapi.star;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface StarRepository extends JpaRepository<Star, Long> {
    @Query("select s from Star s where s.name = ?1 and s.distance = ?2")
    Optional<Star> findByNameAndDistance(String name, long distance);

}
