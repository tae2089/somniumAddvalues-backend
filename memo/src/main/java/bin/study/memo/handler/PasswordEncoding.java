package bin.study.memo.handler;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordEncoding  implements PasswordEncoder {

    private final PasswordEncoder passwordEncoder;

    //비밀번호 암호화 defalut
    public PasswordEncoding() {
        this.passwordEncoder = new BCryptPasswordEncoder();
    }
    //암호화 4가지 종류중 하나로 하기
    public PasswordEncoding(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
    //비밀번호 암호화
    @Override
    public String encode(CharSequence rawPassword) {
        return this.passwordEncoder.encode(rawPassword);
    }

    //암호화된 비밀번호 맞는지 확인 맞으면 true, 틀리면 false
    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        //Service 혹은 repositoy를 활용해 이메일 불러오기
        return this.passwordEncoder.matches(rawPassword, encodedPassword);

    }
}
