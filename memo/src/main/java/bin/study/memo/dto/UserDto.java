package bin.study.memo.dto;


import bin.study.memo.domain.User;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;


@Data
@NoArgsConstructor
public class UserDto {
    private Long id;

    @NotBlank(message = "성은 필수 입력입니다.")
    private String first_name;

    @NotBlank(message = "이름은 필수 입력입니다.")
    private String second_name;

    @NotBlank(message = "이메일은 필수 입력입니다.")
    @Email(message = "이메일 형식에 맞지 않습니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수 입력입니다.")
    @Pattern(regexp="(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,20}",
            message = "비밀번호는 영문 대,소문자와 숫자, 특수기호가 적어도 1개 이상씩 포함된 8자 ~ 20자의 비밀번호여야 합니다.")
    private String pwd1;

    @NotBlank(message = "비밀번호는 필수 입력입니다.")
    @Pattern(regexp="(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,20}",
            message = "비밀번호는 영문 대,소문자와 숫자, 특수기호가 적어도 1개 이상씩 포함된 8자 ~ 20자의 비밀번호여야 합니다.")
    private String pwd2;


    @Builder
    public UserDto(Long id, String firstname,String Secondname, String email, String password,String password2) {
        this.id = id;
        this.first_name = firstname;
        this.second_name = Secondname;
        this.pwd2= password2;
        this.email = email;
        this.pwd1 = password;
    }

    public User toEntity(){
        return User.builder().id(id).email(email).password(pwd1).secondname(second_name).firstname(first_name).build();
    }


}
