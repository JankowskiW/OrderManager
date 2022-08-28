package pl.wj.ordermanager.domain.role;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.wj.ordermanager.domain.role.model.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
}
