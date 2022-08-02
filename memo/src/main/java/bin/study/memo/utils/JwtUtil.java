package bin.study.memo.utils;

import bin.study.memo.domain.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    public final static long TOKEN_VALIDATION_SECOND =  1000 * 60L * 60L ;//1시간
    public final static long REFRESH_TOKEN_VALIDATION_SECOND = 1000 * 60L * 60L * 24L; //24시간

    final static public String ACCESS_TOKEN_NAME = "accessToken";
    final static public String REFRESH_TOKEN_NAME = "refreshToken";


    private final String SECRET_KEY = "se10vs!@cPwseqfGh$sccFoQwsaddgVmls!";


    private Key getSigningKey(String secretKey) {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    //extractAllclaims() : 토큰이 유효한 토큰인지 검사한 후, 토큰에 담긴 Payload 값을 가져온다.
    public Claims extractAllClaims(String token) throws ExpiredJwtException {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey(SECRET_KEY))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    //getUsername() : 추출한 Payload로부터 userName을 가져온다.
    public String getUsername(String token) {
        return extractAllClaims(token).get("username", String.class);
    }
    public String getEmail(String token) {
        return extractAllClaims(token).get("email", String.class);
    }
    public Long getPoint(String token) {
        return extractAllClaims(token).get("point", Long.class);
    }
    public String getRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }
    public String getAuthkey(String token) {
        return extractAllClaims(token).get("authkey", String.class);
    }
    //isTokenExpired() : 토큰이 만료됐는지 안됐는지 확인.
    public Boolean isTokenExpired(String token) {
        final Date expiration = extractAllClaims(token).getExpiration();
        return expiration.before(new Date());
    }

    //geneateAccessToken() : Access/Refresh Token을 형성
    public String generateToken(User member) {
        return doGenerateToken(member, TOKEN_VALIDATION_SECOND);
    }

    //geneateRefreshToken() : Refresh Token을 형성
    public String generateRefreshToken(User member) {
        return doGenerateToken(member, REFRESH_TOKEN_VALIDATION_SECOND);
    }

    //doGenerateToken() : 토큰을 생성, 페이로드에 담길 값은 username
    public String doGenerateToken(User user, long expireTime) {
        Claims claims = Jwts.claims();
        claims.put("username", user.getFirstname()+user.getSecondname());
        claims.put("email", user.getEmail());
        claims.put("role", user.getRoles());
        claims.put("point",user.getPoint());
        claims.put("participation",user.getParticipation());
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expireTime))
                .signWith(getSigningKey(SECRET_KEY), SignatureAlgorithm.HS256)
                .compact();
    }

    public String emailToken(String email,String authkey){return doGenerateToken2(email,authkey,TOKEN_VALIDATION_SECOND);}

    public String doGenerateToken2(String email,String authkey, long expireTime) {
        Claims claims = Jwts.claims();
        claims.put("email", email);
        claims.put("authkey",authkey);
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expireTime))
                .signWith(getSigningKey(SECRET_KEY), SignatureAlgorithm.HS256)
                .compact();
    }


    public Boolean validateToken(String token, User userDetails) {
        final String username = getEmail(token);
        return (username.equals(userDetails.getEmail()) && !isTokenExpired(token));
    }

}
