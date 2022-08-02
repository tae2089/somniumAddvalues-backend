package bin.study.memo.domain;


import bin.study.memo.dto.UserDto;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
public class Login {

    @NotBlank(message = "이메일은 필수 입력 값입니다.")
    @Email(message = "이메일 형식에 맞지 않습니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    private String password;


    public UserDto createlogin(){
        return UserDto.builder().email(email).password(password).build();
    }
}
