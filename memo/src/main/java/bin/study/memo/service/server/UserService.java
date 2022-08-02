package bin.study.memo.service.server;

import bin.study.memo.domain.*;
import bin.study.memo.dto.UserDto;
import bin.study.memo.error.LoginError;
import bin.study.memo.error.TokenError;
import bin.study.memo.error.UserError;
import bin.study.memo.handler.PasswordEncoding;
import bin.study.memo.repository.info.UserMongodbRepository;
import bin.study.memo.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.springframework.data.mongodb.core.FindAndModifyOptions.options;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Service
@Slf4j
public class UserService {

    @Autowired
    private PasswordEncoding passwordEncoding;
    @Autowired
    private MongoOperations mongoOperations;
    @Autowired
    private UserMongodbRepository userMongodbRepository;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private MongoTemplate mongoTemplate;


    //UserError은 여기서 수정하기
    public UserError validateHandling(Errors errors, UserDto userDto) {
        Map<String, String> validatorResult = new HashMap<>();

        for (FieldError error : errors.getFieldErrors()) {
            String validKeyName = String.format("valid_%s", error.getField());
            validatorResult.put(validKeyName, error.getDefaultMessage());
        }
        UserError userError = new UserError().userFail(validatorResult);
        userError = equalsPwd(userDto,userError);
        userError = DuplicateEmail(userDto,userError);
        return userError;
    }

    //UserError은 여기서 수정하기
    public UserError validateHandling(Errors errors,PasswordCheck userDto) {
        Map<String, String> validatorResult = new HashMap<>();

        for (FieldError error : errors.getFieldErrors()) {
            String validKeyName = String.format("valid_%s", error.getField());
            validatorResult.put(validKeyName, error.getDefaultMessage());
        }
        UserError userError = new UserError().userFail(validatorResult);
        return userError;
    }




    //login error 핸들링은 여기서 수정하기
    public LoginError validateLoginHandling(Errors errors) {
        Map<String, String> validatorResult = new HashMap<>();

        for (FieldError error : errors.getFieldErrors()) {
            String validKeyName = String.format("valid_%s", error.getField());
            validatorResult.put(validKeyName, error.getDefaultMessage());
        }
        LoginError loginError = new LoginError().userFail(validatorResult);
        log.info("loginError:"+loginError);
        return loginError;
    }


    // 회원가입
    public void signUp(UserDto userDto) {
        User user = userDto.toEntity();
      //userRepository.save(user);
        user.setUser_id(generateSequence(User.SEQUENCE_NAME));
        user.setPassword(passwordEncoding.encode(user.getPassword()));
        userMongodbRepository.insert(user);
    }



    //비밀번호 확인
    public UserError equalsPwd(UserDto userDto, UserError userError){

        if (!userDto.getPwd1().equals(userDto.getPwd2())){
            userError.setPwd2_error("비밀번호가 똑같지 않습니다.");
        }
        return  userError;
    }

    //데이터 번호 생성
    public long generateSequence(String seqName) {

        DatabaseSequence counter = mongoOperations.findAndModify(query(where("_id").is(seqName)),
                new Update().inc("seq",1), options().returnNew(true).upsert(true),
                DatabaseSequence.class);
        return !Objects.isNull(counter) ? counter.getSeq() : 1;
    }

    //유저 확인
    public LoginError CheckUser(UserDto userDto, LoginError loginError){
        System.out.println("UserService userDto: "+userDto);
    //유저 불러오기
        User user = userMongodbRepository.findByEmail(userDto.getEmail());
        if(user == null){
            loginError.setSuccess(false);
            loginError.setEmail_error("존재하지 않는 사용자입니다.");
        }else{
            if(!passwordEncoding.matches(userDto.getPwd1(), user.getPassword())){
                loginError.setSuccess(false);
                loginError.setPwd1_error("비밀번호가 맞지 않습니다.");
            }
        }
        return  loginError;
    }

    //유저 확인 이메일 중복 확인
    public UserError DuplicateEmail(UserDto userDto, UserError userError){
        System.out.println("UserService userDto: "+userDto);
        //유저 불러오기
        User user = userMongodbRepository.findByEmail(userDto.getEmail());
        if(user != null){
            userError.setSuccess(false);
            userError.setEmail_error("중복된 이메일입니다.");
            return userError;
        }
        return new UserError().userSuecess();
    }

    public LoginError setToken(LoginError userError, Login login) {
        User user = userMongodbRepository.findByEmail(login.getEmail());
        System.out.println("setToken1:"+user.getRefreshtoken());
                String refreshToken = jwtUtil.generateRefreshToken(user);
                userError.setAccess_token(jwtUtil.generateToken(user));
                userError.setRefresh_token(refreshToken);
                userTokenUpdate(refreshToken, user.getEmail());
        return userError;
    }

    public void userTokenUpdate(String token,String mail){
        User user = userMongodbRepository.findByEmail(mail);

        Query query = new Query();
        Update update = new Update();

        // where절 조건
        query.addCriteria(Criteria.where("_id").is(user.getUser_id()));
        update.set("refreshtoken", token);

        mongoTemplate.updateMulti(query, update, "user");
    }

    public void userTokenUpdate(TokenError tokenError, Email mail){
        User user = userMongodbRepository.findByEmail(mail.getMail());
        Query query = new Query();
        Update update = new Update();

        // where절 조건
        query.addCriteria(Criteria.where("_id").is(user.getUser_id()));
        update.set("refreshtoken", tokenError.getRefresh_token());
        mongoTemplate.updateMulti(query, update, "user");
    }

    public void userPointUpdate(String mail,Long point){
        User user = userMongodbRepository.findByEmail(mail);
        Query query = new Query();
        Update update = new Update();

        // where절 조건
        query.addCriteria(Criteria.where("_id").is(user.getUser_id()));
        update.set("point", user.getPoint()+point);
        mongoTemplate.updateMulti(query, update, "user");
    }

    public TokenError createToken(String refreshToken){
        String email = jwtUtil.getEmail(refreshToken);
        User user = userMongodbRepository.findByEmail(email);
        new TokenError();
        return TokenError.builder()
                .accessToken(jwtUtil.generateToken(user))
                .refresh_success(true)
                .refreshToken("")
                .success(true)
                .build();
    }

    public String getRole(String email) {
        User user = userMongodbRepository.findByEmail(email);
        return user.getRoles();
    }


    public void updatePwd(String pwd, String email) {
        User user = userMongodbRepository.findByEmail(email);
        Query query = new Query();
        Update update = new Update();
        // where절 조건
        query.addCriteria(Criteria.where("_id").is(user.getUser_id()));
        user.setPassword(passwordEncoding.encode(pwd));
        update.set("password", user.getPassword());
        mongoTemplate.updateMulti(query, update, "user");
    }

    public void userUpdateParticipation(String username,String variable,int point) {
        User user = userMongodbRepository.findByEmail(username);
        Query query = new Query();
        Update update = new Update();

        // where절 조건
        query.addCriteria(Criteria.where("_id").is(user.getUser_id()));
        if(variable.equals("participation")){
            update.set("participation", user.getParticipation()+1L);
        }
        else if (variable.equals("point")){
            update.set("point", user.getPoint()+point);
        }
        mongoTemplate.updateMulti(query, update, "user");
    }



    public boolean deleteUser(String email) {
        try {
            Criteria criteria = new Criteria("email");
            criteria.is(email);
            Query query = new Query(criteria);
            mongoTemplate.remove(query, "user");

            Criteria criteria2 = new Criteria("username");
            criteria2.is(email);
            Query query2 = new Query(criteria2);
            mongoTemplate.remove(query2, "review");

            Criteria criteria3 = new Criteria("mail");
            criteria3.is(email);
            Query query3 = new Query(criteria3);
            mongoTemplate.remove(query3, "email");
            return true;

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }
}
