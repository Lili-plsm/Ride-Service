package ru.site.datasource.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.site.datasource.model.RideEntity;

@Repository
public interface RideRepository extends JpaRepository<RideEntity, Long> {

  List<RideEntity> findByDriverId(Long riderId);

  List<RideEntity> findByClientId(Long clientId);
}
