package bin.study.memo.error;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
public class UserError {
    private boolean success;
    private String first_error;
    private String second_error;
    private String email_error;
    private String pwd1_error;
    private String pwd2_error;

    @Builder
    public UserError(boolean success, String firstNameErrMsg,
                     String secondNameErrMsg, String emailErrMsg, String pwd1ErrMsg, String pwd2ErrMsg) {
        this.success = success;
        this.first_error = firstNameErrMsg;
        this.second_error = secondNameErrMsg;
        this.email_error = emailErrMsg;
        this.pwd1_error = pwd1ErrMsg;
        this.pwd2_error = pwd2ErrMsg;
    }

//유효성 검증 성공 시
    public UserError userSuecess(){
        return UserError.builder()
                .success(true)
                .emailErrMsg("")
                .firstNameErrMsg("")
                .secondNameErrMsg("")
                .pwd1ErrMsg("")
                .pwd2ErrMsg("")
                .build();
    }

    //null값 체크
private String checknull(String data){

        if(data != null ){
            return data;
        }
        return  "";
}

// 유효성 검증 실패 시,
    public UserError userFail(Map<String, String> validatorResult) {
        UserError userError = new UserError();
        userError.setSuccess(false);
        userError.setFirst_error(checknull(validatorResult.get("valid_first_name")));
        userError.setSecond_error(checknull(validatorResult.get("valid_second_name")));
        userError.setEmail_error(checknull(validatorResult.get("valid_email")));
        userError.setPwd1_error(checknull(validatorResult.get("valid_pwd1")));
        userError.setPwd2_error(checknull(validatorResult.get("valid_pwd2")));
        return userError;
    }
}
