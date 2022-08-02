package bin.study.memo.error;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
public class LoginError {

    //공백, 비밀번호 틀림
    private String pwd1_error;
    //이메일 미입력
    private String email_error;
    //성공 실패
    private boolean success;
    //access_token
    private String access_token;
    //refresh_token
    private String refresh_token;

    private boolean checksurvey;

    private String roles;


    @Builder
    public LoginError(String passwordError, String emailError,boolean success
                                        ,String access_token,String refresh_token,boolean checksurvey) {
        this.pwd1_error = passwordError;
        this.email_error = emailError;
        this.success = success;
        this.access_token = access_token;
        this.refresh_token = refresh_token;
        this.checksurvey = checksurvey;
    }

    //null값 체크
    private String checking(String data){
        if(data != null){
            return data;
        }
        return "";
    }

    // 유효성 검증 실패 시,
    public LoginError userFail(Map<String, String> validatorResult) {
        LoginError loginError = new LoginError();
        loginError.setSuccess(false);
        loginError.setEmail_error(checking(validatorResult.get("valid_email")));
        loginError.setPwd1_error(checking(validatorResult.get("valid_password")));
        loginError.setRoles("'");
        return loginError;
    }

    public LoginError loginSucess(){
        return LoginError.builder()
                    .success(true)
                    .emailError("")
                    .passwordError("")
                    .access_token("")
                    .refresh_token("")
                    .build();
    }





}
