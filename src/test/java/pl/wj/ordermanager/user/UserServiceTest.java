package pl.wj.ordermanager.user;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.wj.ordermanager.confirmationtoken.ConfirmationTokenService;
import pl.wj.ordermanager.confirmationtoken.model.ConfirmationToken;
import pl.wj.ordermanager.email.EmailSender;
import pl.wj.ordermanager.exception.ExceptionHelper;
import pl.wj.ordermanager.exception.ResourceExistsException;
import pl.wj.ordermanager.exception.ResourceNotFoundException;
import pl.wj.ordermanager.user.model.User;
import pl.wj.ordermanager.user.model.UserMapper;
import pl.wj.ordermanager.user.model.dto.UserPasswordDto;
import pl.wj.ordermanager.user.model.dto.UserRequestDto;
import pl.wj.ordermanager.user.model.dto.UserResponseDto;
import pl.wj.ordermanager.user.model.dto.UserUpdateRequestDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static pl.wj.ordermanager.exception.ExceptionHelper.createResourceNotFoundExceptionMessage;
import static pl.wj.ordermanager.registration.RegistrationServiceTestHelper.*;
import static pl.wj.ordermanager.user.UserServiceTestHelper.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private ConfirmationTokenService confirmationTokenService;
    @Mock
    private EmailSender emailSender;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Spy
    private static UserMapper userMapper;
    @InjectMocks
    private UserService userService;

    private static List<UserResponseDto> allUsers;

    @BeforeAll
    static void setUp() {
        allUsers = createListOfUserResponseDtos();
        userMapper = Mappers.getMapper(UserMapper.class);
    }

    @Test
    @DisplayName("Should return UserDetails")
    void shouldReturnUserDetails() {
        // given
        User user = createExampleUser(true, 1L);
        given(userRepository.findByUsername(anyString())).willReturn(Optional.of(user));

        // when
        UserDetails userDetails = userService.loadUserByUsername(user.getUsername());

        // then
        assertThat(userDetails)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(createExampleUserDetails(user));
    }

    @Test
    @DisplayName("Should throw UsernameNotFoundException when user does not exist")
    void shouldThrowExceptionWhenUserDoesNotExist() {
        // given
        given(userRepository.findByUsername(anyString())).willReturn(Optional.empty());

        // when
        assertThatThrownBy(() -> userService.loadUserByUsername("johndoe"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User not found in the database");
    }

    @Test
    @DisplayName("Should add new user")
    void shouldAddNewUser() {
        // given
        String encodedPassword = "EncodedPassword";
        User expectedUser = createExampleUser(false, 1L);
        UserRequestDto userRequestDto = createExampleUserRequestDto(expectedUser);
        expectedUser.setPassword(encodedPassword);
        given(userRepository.existsByUsernameOrEmailAddress(anyString(), anyString())).willReturn(false);
        given(userRepository.getLoggedInUserId()).willReturn(Optional.of(expectedUser.getId()));
        given(userMapper.userRequestDtoToUser(any(UserRequestDto.class))).willCallRealMethod();
        given(passwordEncoder.encode(anyString())).willReturn(encodedPassword);
        given(userRepository.save(any(User.class))).willAnswer(
                i -> {
                    User u = i.getArgument(0, User.class);
                    u.setId(expectedUser.getId());
                    u.setCreatedAt(getCurrentTimestamp());
                    u.setUpdatedAt(getCurrentTimestamp());
                    return u;
                });

        // when
        User user =  userService.addUser(userRequestDto);

        // then
        assertThat(user)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(expectedUser);
    }

    @Test
    @DisplayName("Should throw ResourceExistsException when user exists by username or email")
    void shouldThrowExceptionWhenUserExistsByUsernameOrEmail() {
        // given
        given(userRepository.existsByUsernameOrEmailAddress(anyString(), anyString())).willReturn(true);

        // when
        assertThatThrownBy(() -> userService.addUser(
                createExampleUserRequestDto(createExampleUser(false, 1L))))
                .isInstanceOf(ResourceExistsException.class)
                .hasMessage(ExceptionHelper.createResourceExistsExceptionMessage("user", "username or email address"));
    }

    @Test
    @DisplayName("Should throw UsernameNotFoundException when logged in user does not exist")
    void shouldThrowExceptionWhenLoggedInUserDoesNotExist() {
        // given
        given(userRepository.existsByUsernameOrEmailAddress(anyString(), anyString())).willReturn(false);
        given(userRepository.getLoggedInUserId()).willReturn(Optional.empty());

        // when
        assertThatThrownBy(() -> userService.addUser(
                    createExampleUserRequestDto(createExampleUser(false, 1L))))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User not found in the database");
    }

    @Test
    @DisplayName("Should edit user")
    void shouldEditUser() {
        // given
        long userId = 1L;
        String firstName = "July";
        LocalDateTime updatedAt = LocalDateTime.now().plusDays(1);
        User user = createExampleUser(false, userId);
        UserUpdateRequestDto userUpdateRequestDto = createExampleUserUpdateRequestDto(user);
        userUpdateRequestDto.setFirstName(firstName);
        UserResponseDto expectedResponse = mapUserToUserResponseDto(user);
        expectedResponse.setId(userId);
        expectedResponse.setFirstName(firstName);
        expectedResponse.setUpdatedBy(userId);
        expectedResponse.setUpdatedAt(updatedAt);

        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
        given(userRepository.getLoggedInUserId()).willReturn(Optional.of(userId));
        given(userRepository.save(any(User.class))).willAnswer(
                i -> {
                    User u = i.getArgument(0, User.class);
                    u.setUpdatedBy(userId);
                    u.setUpdatedAt(updatedAt);
                    u.setFirstName(firstName);
                    return u;
                });

        // when
        UserResponseDto userResponseDto = userService.editUser(userId, userUpdateRequestDto);

        // then
        assertThat(userResponseDto)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when cannot find edited user")
    void shouldThrowExceptionWhenCannotFindEditedUser() {
        // given
        long userId = 100L;
        given(userRepository.findById(anyLong())).willReturn(Optional.empty());

        // when
        assertThatThrownBy(() -> userService.editUser(userId, createExampleUserUpdateRequestDto()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(createUserNotFoundMessage());
    }


    @Test
    @DisplayName("Should throw UsernameNotFoundException when logged in user does not exist in user edit")
    void shouldThrowExceptionWhenLoggedInUserDoesNotExistInUserEdit() {
        // given
        long userId = 100L;
        given(userRepository.findById(anyLong())).willReturn(Optional.of(createExampleUser(false, userId)));
        given(userRepository.getLoggedInUserId()).willReturn(Optional.empty());

        // when
        assertThatThrownBy(() -> userService.editUser(userId, createExampleUserUpdateRequestDto()))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User not found in the database");
    }

    @Test
    @DisplayName("Should return just one but not last page of users")
    void shouldReturnJustOneButNotLastPageOfAllUsers() {
        // given
        int pageNumber = 0;
        int pageSize = 4;
        int firstElementIndex = pageNumber * pageSize;
        int lastElementIndex = (pageNumber + 1) * pageSize;
        List<UserResponseDto> onePageOfAllUser =
                allUsers.subList(firstElementIndex, lastElementIndex);
        given(userRepository.getUsers(any(Pageable.class)))
                .willReturn(new PageImpl<>(onePageOfAllUser));

        // when
        Page<UserResponseDto> responseUsers =
                userService.getUsers(null, PageRequest.of(pageNumber, pageSize));

        // then
        assertThat(responseUsers)
                .isNotNull()
                .hasSize(pageSize)
                .usingRecursiveFieldByFieldElementComparator()
                .isEqualTo(onePageOfAllUser);
    }

    @Test
    @DisplayName("Should return just last page of users")
    void shouldReturnJustLastPageOfAllUsers() {
        // given
        int pageNumber = 2;
        int pageSize = 4;
        int firstElementIndex = pageNumber * pageSize;
        int lastElementIndex = allUsers.size();
        int expectedSize = allUsers.size() - (pageNumber * pageSize);
        List<UserResponseDto> lastPageOfAllUser =
                allUsers.subList(firstElementIndex, lastElementIndex);
        given(userRepository.getUsers(any(Pageable.class)))
                .willReturn(new PageImpl<>(lastPageOfAllUser));

        // when
        Page<UserResponseDto> responseUsers =
                userService.getUsers(null, PageRequest.of(pageNumber, pageSize));

        // then
        assertThat(responseUsers)
                .isNotNull()
                .hasSize(expectedSize)
                .usingRecursiveFieldByFieldElementComparator()
                .isEqualTo(lastPageOfAllUser);
    }

    @Test
    @DisplayName("Should return empty list of users")
    void shouldReturnEmptyListOfUsers() {
        // given
        int pageNumber = 100;
        int pageSize = 4;
        int expectedSize = 0;
        boolean archived = true;
        given(userRepository.getUsers(any(Pageable.class)))
                .willReturn(new PageImpl<>(new ArrayList<>()));

        // when
        Page<UserResponseDto> responseUsers =
                userService.getUsers(null, PageRequest.of(pageNumber, pageSize));

        // then
        assertThat(responseUsers)
                .isNotNull()
                .hasSize(expectedSize);
    }

    @Test
    @DisplayName("Should return just one but not last page of archived users")
    void shouldReturnJustOneButNotLastPageOfArchivedUsers() {
        // given
        boolean archived = true;
        int pageNumber = 0;
        int pageSize = 2;
        int firstElementIndex = pageNumber * pageSize;
        List<UserResponseDto> onePageOfArchivedUsers =
                allUsers.stream()
                        .filter(u -> u.getArchivedBy() != null)
                        .skip(firstElementIndex)
                        .limit(pageSize)
                        .collect(Collectors.toList());
        given(userRepository.getUsers(anyBoolean(), any(Pageable.class)))
                .willReturn(new PageImpl<>(onePageOfArchivedUsers));

        // when
        Page<UserResponseDto> responseArchivedUsers =
                userService.getUsers(archived, PageRequest.of(pageNumber, pageSize));

        // then
        assertThat(responseArchivedUsers)
                .isNotNull()
                .hasSize(pageSize)
                .usingRecursiveFieldByFieldElementComparator()
                .isEqualTo(onePageOfArchivedUsers);
    }

    @Test
    @DisplayName("Should return just last page of archived users")
    void shouldReturnJustLastPageOfArchivedUsers() {
        // given
        int pageNumber = 2;
        int pageSize = 2;
        int firstElementIndex = pageNumber * pageSize;
        List<UserResponseDto> allArchivedUsers =
                allUsers.stream()
                        .filter(u -> u.getArchivedBy() == null)
                        .collect(Collectors.toList());
        List<UserResponseDto> lastPageOfArchivedUsers =
                allArchivedUsers.stream()
                        .skip(firstElementIndex)
                        .limit(pageSize)
                        .collect(Collectors.toList());
        int expectedSize = allArchivedUsers.size() - (pageNumber * pageSize);
        given(userRepository.getUsers(any(Pageable.class)))
                .willReturn(new PageImpl<>(lastPageOfArchivedUsers));

        // when
        Page<UserResponseDto> responseUsers =
                userService.getUsers(null, PageRequest.of(pageNumber, pageSize));

        // then
        assertThat(responseUsers)
                .isNotNull()
                .hasSize(expectedSize)
                .usingRecursiveFieldByFieldElementComparator()
                .isEqualTo(lastPageOfArchivedUsers);
    }

    @Test
    @DisplayName("Should return just one but not last page of not archived users")
    void shouldReturnJustOneButNotLastPageOfNotArchivedUsers() {
        // given
        boolean archived = false;
        int pageNumber = 0;
        int pageSize = 2;
        int firstElementIndex = pageNumber * pageSize;
        List<UserResponseDto> onePageOfArchivedUsers =
                allUsers.stream()
                        .filter(u -> u.getArchivedBy() == null)
                        .skip(firstElementIndex)
                        .limit(pageSize)
                        .collect(Collectors.toList());
        given(userRepository.getUsers(anyBoolean(), any(Pageable.class)))
                .willReturn(new PageImpl<>(onePageOfArchivedUsers));

        // when
        Page<UserResponseDto> responseArchivedUsers =
                userService.getUsers(archived, PageRequest.of(pageNumber, pageSize));

        // then
        assertThat(responseArchivedUsers)
                .isNotNull()
                .hasSize(pageSize)
                .usingRecursiveFieldByFieldElementComparator()
                .isEqualTo(onePageOfArchivedUsers);
    }

    @Test
    @DisplayName("Should return just last page of not archived users")
    void shouldReturnJustLastPageOfNotArchivedUsers() {
        // given
        int pageNumber = 2;
        int pageSize = 2;
        int firstElementIndex = pageNumber * pageSize;
        List<UserResponseDto> allNotArchivedUsers =
                allUsers.stream()
                        .filter(u -> u.getArchivedBy() == null)
                        .collect(Collectors.toList());
        List<UserResponseDto> lastPageOfNotArchivedUsers =
                allNotArchivedUsers.stream()
                        .skip(firstElementIndex)
                        .limit(pageSize)
                        .collect(Collectors.toList());
        int expectedSize = allNotArchivedUsers.size() - (pageNumber * pageSize);
        given(userRepository.getUsers(any(Pageable.class)))
                .willReturn(new PageImpl<>(lastPageOfNotArchivedUsers));

        // when
        Page<UserResponseDto> responseUsers =
                userService.getUsers(null, PageRequest.of(pageNumber, pageSize));

        // then
        assertThat(responseUsers)
                .isNotNull()
                .hasSize(expectedSize)
                .usingRecursiveFieldByFieldElementComparator()
                .isEqualTo(lastPageOfNotArchivedUsers);
    }

    @Test
    @DisplayName("Should return empty list of users with archived status selected")
    void shouldReturnEmptyListOfUsersWithArchivedStatusSelected() {
        // given
        int pageNumber = 100;
        int pageSize = 4;
        int expectedSize = 0;
        boolean archived = true;
        given(userRepository.getUsers(anyBoolean(), any(Pageable.class)))
                .willReturn(new PageImpl<>(new ArrayList<>()));

        // when
        Page<UserResponseDto> responseUsers =
                userService.getUsers(archived, PageRequest.of(pageNumber, pageSize));

        // then
        assertThat(responseUsers)
                .isNotNull()
                .hasSize(expectedSize);
    }

    @Test
    @DisplayName("Should set user enable status to true")
    void shouldSetUserEnableStatusToTrue() {
        // given
        long id = 1L;
        User user = createExampleUser(false, id);

        // when
        userService.enableUser(user);

        // then
        verify(userRepository).save(user);
        assertThat(user).isNotNull();
        assertThat(user.isEnabled()).isEqualTo(true);
    }

    @Test
    @DisplayName("Should change user password")
    void shouldChangeUserPassword() {
        // given
        long loggedInUserId = 1L;
        UserPasswordDto userPasswordDto = new UserPasswordDto();
        userPasswordDto.setPassword("NewPassword");
        String encodedPassword = "Encoded"+userPasswordDto.getPassword();
        User user = createExampleUser(false, loggedInUserId);
        user.setPassword(encodedPassword);
        given(userRepository.getLoggedInUserId()).willReturn(Optional.of(loggedInUserId));
        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
        given(passwordEncoder.encode(anyString())).willReturn(encodedPassword);
        given(userRepository.save(any(User.class))).willAnswer(
                i -> {
                    User u = i.getArgument(0, User.class);
                    u.setUpdatedAt(getCurrentTimestamp());
                    u.setPassword(encodedPassword);
                    return u;
                });

        // when
        userService.changePassword(userPasswordDto);

        // then
        verify(userRepository).save(user);
        assertThat(userRepository.save(user).getPassword())
                .isNotNull()
                .isEqualTo(encodedPassword);
    }

    @Test
    @DisplayName("Should throw UsernameNotFoundException when cannot find logged in user id")
    void shouldThrowExceptionWhenCannotFindLoggedInUserId() {
        // given
        given(userRepository.getLoggedInUserId()).willReturn(Optional.empty());

        // when
        assertThatThrownBy(() -> userService.changePassword(new UserPasswordDto()))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User not found in the database");
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when cannot find user")
    void shouldThrowExceptionWhenCannotFindUser() {
        // given
        long loggedInUser = 1L;
        given(userRepository.getLoggedInUserId()).willReturn(Optional.of(loggedInUser));
        given(userRepository.findById(anyLong())).willReturn(Optional.empty());

        // when
        assertThatThrownBy(() -> userService.changePassword(new UserPasswordDto()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(createResourceNotFoundExceptionMessage("user"));
    }

    @Test
    @DisplayName("Should send confirmation token")
    void shouldSendConfirmationToken() {
        // given
        String emailAddress = "example@example.com";
        long userId = 1L;
        long tokenId = 1L;
        User user = createExampleUser(false, userId);
        given(userRepository.findByEmailAddress(emailAddress)).willReturn(Optional.of(user));
        given(confirmationTokenService.addConfirmationToken(any(ConfirmationToken.class))).willAnswer(
                i -> {
                    ConfirmationToken ct = i.getArgument(0, ConfirmationToken.class);
                    ct.setId(tokenId);
                    return ct;
                });
        willDoNothing().given(emailSender).sendPasswordResetConfirmationToken(
                anyString(), anyString(), anyString(),
                anyString(), anyLong());

        // when
        userService.sendPasswordResetConfirmationToken(emailAddress);

        // then
        verify(emailSender).sendPasswordResetConfirmationToken(
                eq(SENDER_EMAIL_ADDRESS), eq(emailAddress), eq(getSubject()),
                startsWith(getConfirmationLink()), eq(TOKEN_EXPIRATION_TIME));
    }

}