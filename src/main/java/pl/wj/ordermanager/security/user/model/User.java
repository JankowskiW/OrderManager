package pl.wj.ordermanager.security.user.model;

import lombok.Getter;
import lombok.Setter;
import pl.wj.ordermanager.security.role.model.Role;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name="users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String username;
    private String emailAddress;
    private String password;
    private long createdBy;
    private LocalDateTime createdAt;
    private long updatedBy;
    private LocalDateTime updatedAt;
    private long archivedBy;
    private LocalDateTime archivedAt;


    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = {@JoinColumn(name="user_id")},
            inverseJoinColumns = {@JoinColumn(name="role_id")}
    )
    private List<Role> roles = new ArrayList<>();
}
