package pl.wj.ordermanager.security.role.model;

import lombok.Getter;
import lombok.Setter;
import pl.wj.ordermanager.security.privilege.model.Privilege;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name="roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    private String description;
    private long createdBy;
    private LocalDateTime createdAt;
    private long updatedBy;
    private LocalDateTime updatedAt;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "role_privileges",
            joinColumns = {@JoinColumn(name="role_id")},
            inverseJoinColumns = {@JoinColumn(name="privilege_id")}
    )
    private List<Privilege> privileges = new ArrayList<>();
}
