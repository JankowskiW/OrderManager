package pl.wj.ordermanager.user.model.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode
@Builder
@AllArgsConstructor
public class UserResponseDto {
    private long id;
    private String firstName;
    private String lastName;
    private String username;
    private String emailAddress;
    private long createdBy;
    private LocalDateTime createdAt;
    private long updatedBy;
    private LocalDateTime updatedAt;
    private Long archivedBy;
    private LocalDateTime archivedAt;
}
