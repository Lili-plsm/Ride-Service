package ru.site.security.init;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.site.datasource.enums.RoleName;
import ru.site.datasource.model.Role;
import ru.site.datasource.repository.RoleRepository;

@Component
public class RoleInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    public RoleInitializer(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        createRoleIfNotFound(RoleName.USER);
        createRoleIfNotFound(RoleName.ADMIN);
    }

    private void createRoleIfNotFound(RoleName roleName) {
        if (roleRepository.findByRoleName(roleName).isEmpty()) {
            Role role = new Role();
            role.setRoleName(roleName);
            roleRepository.save(role);
        }
    }
}
