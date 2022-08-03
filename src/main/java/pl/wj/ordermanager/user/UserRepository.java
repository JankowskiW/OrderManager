package pl.wj.ordermanager.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;
import pl.wj.ordermanager.user.model.User;
import pl.wj.ordermanager.user.model.dto.UserResponseDto;

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

    @Query("SELECT new pl.wj.ordermanager.user.model.dto.UserResponseDto(" +
            "u.id, u.firstName, u.lastName, u.username, u.emailAddress, u.createdBy, u.createdAt, " +
            "u.updatedBy, u.updatedAt, u.archivedBy, u.archivedAt) FROM User u")
    Page<UserResponseDto> getUsers(Pageable pageable);


    @Query("SELECT new pl.wj.ordermanager.user.model.dto.UserResponseDto(" +
            "u.id, u.firstName, u.lastName, u.username, u.emailAddress, u.createdBy, u.createdAt, " +
            "u.updatedBy, u.updatedAt, u.archivedBy, u.archivedAt) FROM User u " +
            "WHERE (:archived = true AND u.archivedBy IS NOT NULL) OR (:archived = false AND u.archivedBy IS NULL)")
    Page<UserResponseDto> getUsers(Boolean archived, Pageable pageable);
}
