package pl.wj.ordermanager.security.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;
import pl.wj.ordermanager.security.user.model.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    default Optional<Long> getLoggedUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth instanceof AnonymousAuthenticationToken)) {
            return getIdByUsername(auth.getName());
        }
        return Optional.empty();
    }

    @Query("SELECT u.id FROM User u WHERE u.username = :username")
    Optional<Long> getIdByUsername(String username);

    Optional<User> getByUsername(String username);
}
