package bin.study.memo.controller.server;


import bin.study.memo.domain.Email;
import bin.study.memo.domain.Login;
import bin.study.memo.domain.PasswordCheck;
import bin.study.memo.dto.UserDto;
import bin.study.memo.error.LoginError;
import bin.study.memo.error.TokenError;
import bin.study.memo.error.UserError;
import bin.study.memo.impl.EmailServiceImpl;
import bin.study.memo.service.server.SurveyService;
import bin.study.memo.service.server.UserService;
import bin.study.memo.utils.CookieUtil;
import bin.study.memo.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseCookie;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@Profile("server")
public class UserRestController {

    @Autowired
    private UserService userService;
    @Autowired
    private EmailServiceImpl emailService;
    @Autowired
    private CookieUtil cookieUtil;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private SurveyService surveyService;

    @GetMapping("/test")
    public String test2() {
        return "good test";
    }

    @GetMapping("/token")
    public String testToken() {
        return "token test";
    }


    //security로 암호화 하기(예정)
    @PostMapping("/join")
    public UserError memberjoin(@Valid @RequestBody UserDto userDto, Errors errors, Model model) {
        UserError userError = new UserError();
        if (errors.hasErrors()) {
            // 회원가입 실패시, 입력 데이터를 유지
            model.addAttribute("userDto", userDto);
            // 유효성 통과 못한 필드와 메시지를 핸들링
            //userError만들기
            userError = userService.validateHandling(errors,userDto);
        }
        userError = userService.DuplicateEmail(userDto,userError);
        if(!userError.isSuccess()){
            return userError;
        }else{
            //userError없을 경우
            userService.signUp(userDto);
            Email emaildto = Email.builder().mail(userDto.getEmail()).build();
            emailService.mailSend2(emaildto);
            return new UserError().userSuecess();
        }
    }


    //로그인 만들기
    @PostMapping("/login")
    @ResponseBody
    public LoginError LoginUser(@Valid @RequestBody Login login, Errors errors, Model model,HttpServletResponse response,HttpServletRequest request)  {
        //errors 확인하기
        LoginError loginError = new LoginError().loginSucess();
        if (errors.hasErrors()) {
            // 회원가입 실패시, 입력 데이터를 유지
            model.addAttribute("userDto", login);
            // 유효성 통과 못한 필드와 메시지를 핸들링
            loginError = userService.validateLoginHandling(errors);
            //userError만들기
            return loginError;
        }
        loginError = userService.CheckUser(login.createlogin(),loginError);
        if (emailService.CheckAuthkeyEmail(login.getEmail())==0 ){
            loginError = emailService.checkEmail(loginError);
        }
        if (loginError.isSuccess()){
            loginError = userService.setToken(loginError,login);

            //cookieUtil.deleteCookie(request, response);

            ResponseCookie cookie = ResponseCookie.from("access_token", "")
                    .httpOnly(true)
                    .sameSite("None")
                    .secure(true)
                    .path("/")
                    .build();
            ResponseCookie cookie2 = ResponseCookie.from("refresh_token", loginError.getRefresh_token())
                    .httpOnly(true)
                    .sameSite("None")
                    .secure(true)
                    .path("/")
                    .build();

            response.addHeader("Set-Cookie", cookie.toString());
            response.addHeader("Set-Cookie", cookie2.toString());

            loginError.setRoles(userService.getRole(login.getEmail()));
            loginError.setChecksurvey(surveyService.checkEmail(login.getEmail()));
        }
        loginError.setRefresh_token("");

        return loginError;
    }

@GetMapping("/silent-refresh")
@ResponseBody
    public TokenError silent_refresh( HttpServletRequest request,HttpServletResponse response){

        Cookie refresh = cookieUtil.getCookie(request, "refresh_token");
        TokenError tokenError = new TokenError();
        if(refresh != null){
            tokenError= userService.createToken(refresh.getValue());
            ResponseCookie cookie = ResponseCookie.from("access_token", "")
                    .httpOnly(true)
                    .sameSite("None")
                    .secure(true)
                    .path("/")
                    .build();
            ResponseCookie cookie2 = ResponseCookie.from("refresh_token", refresh.getValue())
                    .httpOnly(true)
                    .sameSite("None")
                    .secure(true)
                    .path("/")
                    .build();

            //cookieUtil.deleteCookie(request, response);

            response.addHeader("Set-Cookie", cookie.toString());
            response.addHeader("Set-Cookie", cookie2.toString());

            tokenError.setRefresh_token("");
            //tokenError.setRoles(jwtUtil.getRole(tokenError.getAccess_token()));
        }
        return tokenError;
    }


    @PatchMapping("/userinfo")
    public UserError hi(@RequestBody PasswordCheck pwds, @RequestHeader(required = false) String access_token , Errors error,HttpServletRequest request){
        UserError userError = new UserError();
        if(error.hasErrors()){
            userError = userService.validateHandling(error,pwds);
            userError.setEmail_error("");
            userError.setFirst_error("");
            userError.setSecond_error("");
        }else{
            userError.setPwd1_error("");
            userError.setPwd2_error("");
        }


        if(userError.getPwd1_error().equals("")&&userError.getPwd2_error().equals("")) {
            userError.setSuccess(true);
            String email;
            if(access_token != null){
               email = jwtUtil.getEmail(access_token);
            }else{
                email = jwtUtil.getEmail(cookieUtil.getCookie(request,"refresh_token").getValue());
            }

            userService.updatePwd(pwds.getPwd1(), email);
        }
        return userError;
    }




    @GetMapping("/logout_")
    @ResponseBody
    public TokenError logout(HttpServletRequest request, HttpServletResponse response){
        cookieUtil.deleteCookie2(request, response,"refresh_token");
        return new TokenError();
    }



    @DeleteMapping("/user")
    public UserError WithdrawUser(HttpServletRequest request){
        Cookie refresh = cookieUtil.getCookie(request, "refresh_token");
        String refresh_token = refresh.getValue();
        String email = jwtUtil.getEmail(refresh_token);
        boolean success = userService.deleteUser(email);
        UserError userError = new UserError();
        userError.setSuccess(success);
        System.out.println("userError success: "+userError.isSuccess());
        return userError;
    }
}
