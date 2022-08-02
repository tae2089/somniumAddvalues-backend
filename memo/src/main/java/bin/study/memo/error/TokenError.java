package bin.study.memo.error;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TokenError {
    private boolean access_success;
    private boolean refresh_success = true;
    private String access_token;
    private String refresh_token;


    @Builder
    public TokenError(boolean success, String accessToken, String refreshToken,boolean refresh_success) {
        this.access_success = success;
        this.refresh_success = refresh_success;
        this.access_token = accessToken;
        this.refresh_token = refreshToken;
    }

    public TokenError tokenFail(){
        return TokenError.builder()
                .accessToken("")
                .refreshToken("")
                .success(false)
                .refresh_success(true)
                .build();
    }
}
