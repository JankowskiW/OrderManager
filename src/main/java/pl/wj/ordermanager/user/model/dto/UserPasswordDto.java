package pl.wj.ordermanager.user.model.dto;

import lombok.AllArgsConstructor;

import javax.validation.constraints.Pattern;

@AllArgsConstructor
public class UserPasswordDto {
    @Pattern(regexp="^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,20}$",
            message = "Password must contain:\n" +
                        "- at least one digit [0-9]\n" +
                        "- at least one lowercase letter [a-z]\n" +
                        "- at least one uppercase letter [A-Z]\n" +
                        "- at least one special character [!, @, #, &, (, ), –, [, {, }\n" +
                        "- a length between 8 and 20 characters")
    String password;
}
