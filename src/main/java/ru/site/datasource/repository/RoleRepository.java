package ru.site.datasource.repository;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.site.datasource.enums.RoleName;
import ru.site.datasource.model.Role;

@Repository
public interface RoleRepository extends CrudRepository<Role, Long> {
  Optional<Role> findByRoleName(RoleName roleName);
}
