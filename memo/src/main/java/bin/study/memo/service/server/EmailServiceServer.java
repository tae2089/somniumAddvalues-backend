package bin.study.memo.service.server;

import bin.study.memo.domain.Email;
import bin.study.memo.domain.User;
import bin.study.memo.error.EmailError;
import bin.study.memo.error.LoginError;
import bin.study.memo.error.TokenError;
import bin.study.memo.handler.Emailhandler;
import bin.study.memo.impl.EmailServiceImpl;
import bin.study.memo.repository.info.EmailMongodbRepository;
import bin.study.memo.repository.info.UserMongodbRepository;
import bin.study.memo.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import java.util.List;
import java.util.Random;

public class EmailServiceServer implements EmailServiceImpl {
    @Autowired
    private UserMongodbRepository userMongodbRepository;
    @Autowired
    private EmailMongodbRepository emailMongodbRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private JavaMailSender mailSender;
    // Thymeleaf Template 사용
    @Autowired
    private TemplateEngine templateEngine;


    @Override
    public boolean CheckDuplicateEmail(String email) {
        User user =  userMongodbRepository.findByEmail(email);
        // 이미 존재하면 false, 없으면 true
        return user == null;
    }

    @Override
    public void mailSend2(Email mailDto) {
// authkey코등 생성하기
        try {
            Emailhandler mailHandler = new Emailhandler(mailSender);
            String authkey = searchRandom();
            mailDto.setAuthkey(authkey);
            //메시지 내용
            String token = new JwtUtil().emailToken(mailDto.getMail(),authkey);
            String url = "https://addvalue.kr/email/certification?authkey="+token;
            mailDto.setMessage(url);
            // 제목
            String Title = "인증을 해주세요!";
            //메일 보내기
            MimeMessagePreparator message = mimeMessage -> {
                String content = build(url);
                MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true,"UTF-8");
                messageHelper.setTo(mailDto.getMail());
                messageHelper.setSubject(Title);
                messageHelper.setText(content, true);
                messageHelper.addInline("6.png", new ClassPathResource("static/6.png"));
                messageHelper.addInline("3.png", new ClassPathResource("static/3.png"));

            };
            mailSender.send(message);
            emailMongodbRepository.insert(mailDto);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    private String build(String text) {
        Context context = new Context();
        context.setVariable("text", text);
        return templateEngine.process("mail-template", context);
    }

    @Override
    public boolean modifyEmailType(String authkey) {
        String authkey2 = jwtUtil.getAuthkey(authkey);
        Email mail = emailMongodbRepository.findAllByAuthkey(authkey2);
        Query query = new Query();
        Update update = new Update();
        // where절 조건
        query.addCriteria(Criteria.where("mail").is(mail.getMail()));
        update.set("type", 1);
        try {
            mongoTemplate.updateMulti(query, update, "email");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public int CheckAuthkeyEmail(String email) {
        List<Email> mail = emailMongodbRepository.findByMail(email);
        Email mail1 = mail.get(0);
        if(mail1 != null) return mail1.getType();
        return 0;
    }

    @Override
    public String generateRandom(int length) {
        Random random = new Random();
        char[] digits = new char[length];
        digits[0] = (char) (random.nextInt(9) + '1');
        for (int i = 1; i < length; i++) {
            digits[i] = (char) (random.nextInt(10) + '0');
        }
        return new String(digits);
    }

    @Override
    public String searchRandom() {
        String auth = generateRandom(9);
        Email mail = emailMongodbRepository.findByAuthkey(auth);
        System.out.println(mail);
        if (mail == null){
            return auth;
        }else{
            return searchRandom();
        }
    }

    @Override
    public LoginError checkEmail(LoginError loginError) {
        loginError.setSuccess(false);
        loginError.setEmail_error("인증되지 않은 이메일입니다.");
        return loginError;
    }

    @Override
    public TokenError getToken(String authkey) {
        TokenError tokenError = new TokenError();
        String authkey2 = jwtUtil.getAuthkey(authkey);
        Email mail = emailMongodbRepository.findAllByAuthkey(authkey2);
        System.out.println(mail.getMail());
        System.out.println(userMongodbRepository.findByEmail(mail.getMail()));
        tokenError.setAccess_success(true);
        tokenError.setAccess_token(jwtUtil.generateToken(userMongodbRepository.findByEmail(mail.getMail())));
        tokenError.setRefresh_token(jwtUtil.generateRefreshToken(userMongodbRepository.findByEmail(mail.getMail())));
        userService.userTokenUpdate(tokenError,mail);
        return tokenError;
    }

    @Override
    public EmailError sendPasswordMail(String email) throws MessagingException {
        User user = userMongodbRepository.findByEmail(email);
        if(user != null){
            Emailhandler mailHandler = new Emailhandler(mailSender);
            String token = new JwtUtil().emailToken(email,"1523231");
            //메시지 내용
            String message = "https://addvalue.kr/change-password?token="+token;
            // 받는 사람
            mailHandler.setTo(email);
            // 보내는 사람
            //mailHandler.setFrom(EmailService.FROM_ADDRESS);
            // 제목
            String Title = "<더함>비밀번호 변경 관련 메일입니다.";
            mailHandler.setSubject(Title);
            // HTML Layout
            String htmlContent = "<p><a href='" +message+ "'/a>  여기를 클릭해주세요. </p>";
            mailHandler.setText(htmlContent, true);
            //메일 보내기
            System.out.println("여기로 왔다");
            mailHandler.send();
            return EmailError.success();
        }else {
            return new EmailError().fail("작성하신 이메일에 문제가 있습니다.");
        }
    }
}
