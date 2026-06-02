package shop.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import shop.domain.Role;
import shop.domain.RoleName;
import shop.repository.RoleRepository;

@Component
@Order(1)
public class RoleDataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    public RoleDataInitializer(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) {
        migrateEmployeeToStaff();

        for (RoleName roleName : RoleName.values()) {
            createRoleIfMissing(roleName);
        }
    }

    private void migrateEmployeeToStaff() {
        Role employee = roleRepository.findByName("EMPLOYEE");
        if (employee == null) {
            return;
        }
        Role staff = roleRepository.findByName(RoleName.STAFF.name());
        if (staff == null) {
            employee.setName(RoleName.STAFF.name());
            employee.setDescription(RoleName.STAFF.getDescription());
            roleRepository.save(employee);
        }
    }

    private void createRoleIfMissing(RoleName roleName) {
        if (roleRepository.findByName(roleName.name()) != null) {
            return;
        }
        Role role = new Role();
        role.setName(roleName.name());
        role.setDescription(roleName.getDescription());
        roleRepository.save(role);
    }
}
