package ru.site.datasource.repository;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.site.datasource.model.User;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
  Optional<User> findByLogin(String login);

  Optional<User> findById(Long uuid);
}
