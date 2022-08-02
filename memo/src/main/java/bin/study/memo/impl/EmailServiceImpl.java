package bin.study.memo.impl;

import bin.study.memo.domain.Email;
import bin.study.memo.error.EmailError;
import bin.study.memo.error.LoginError;
import bin.study.memo.error.TokenError;

import javax.mail.MessagingException;

public interface EmailServiceImpl {
    //이메일 중복 확인 요청 service
   public boolean CheckDuplicateEmail(String email) ;
    public void mailSend2(Email mailDto);
    //이메일 type변경하기
    public boolean modifyEmailType(String authkey);
    //이메일 type확인하기
    public int CheckAuthkeyEmail(String email);
    public LoginError checkEmail(LoginError loginError) ;
    public TokenError getToken(String authkey);
    public EmailError sendPasswordMail(String email) throws MessagingException;
    public  String generateRandom(int length);
    public String searchRandom();

}
