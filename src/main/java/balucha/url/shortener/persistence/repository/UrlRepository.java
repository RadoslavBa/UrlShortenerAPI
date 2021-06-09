package balucha.url.shortener.persistence.repository;

import balucha.url.shortener.persistence.entity.UrlEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UrlRepository extends JpaRepository<UrlEntity, UUID> {

    Optional<UrlEntity> findByNameIgnoreCase(String name);
}
