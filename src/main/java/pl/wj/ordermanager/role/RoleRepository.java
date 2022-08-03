package pl.wj.ordermanager.role;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.wj.ordermanager.role.model.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
}
