package bin.study.memo.controller.local;

import bin.study.memo.error.EmailError;
import bin.study.memo.error.TokenError;
import bin.study.memo.impl.EmailServiceImpl;
import bin.study.memo.utils.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;


@RestController
@Profile("local")
public class EmailRestController {

    @Autowired
    private EmailServiceImpl emailService;

    @Autowired
    private CookieUtil cookieUtil;

    //이메일 클릭
    @PostMapping("/email/certification")
    @ResponseBody
    public TokenError ClickedMail(@RequestParam("authkey") String authkey, HttpServletResponse response, HttpServletRequest request){
        //메일 클릭하면 인증완료
        emailService.modifyEmailType(authkey);
        TokenError tokenError = emailService.getToken(authkey);

        cookieUtil.deleteCookie(request, response);

        ResponseCookie cookie = ResponseCookie.from("refresh_token", tokenError.getRefresh_token())
                .path("/")
                .build();

        response.addHeader("Set-Cookie", cookie.toString());

        tokenError.setRefresh_token("");
        return tokenError;
    }

    //이메일 중복 확인 -> ajax용도
    @GetMapping("/search/email")
    public boolean SearchEmail(@RequestParam("email") String email){
        // 이미 존재하면 false, 없으면 true
        return emailService.CheckDuplicateEmail(email);
    }

    @PostMapping("/find-password")
    @ResponseBody
    public EmailError findPwdl(@RequestBody HashMap<String, String> param) throws MessagingException {
        String email = param.get("email");
        EmailError emailError = emailService.sendPasswordMail(email);
        return emailError;
    }


}
