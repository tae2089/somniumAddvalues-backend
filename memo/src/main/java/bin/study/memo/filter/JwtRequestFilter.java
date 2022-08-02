package bin.study.memo.filter;


import bin.study.memo.domain.User;
import bin.study.memo.error.TokenError;
import bin.study.memo.handler.Regexhandler;
import bin.study.memo.repository.info.UserMongodbRepository;
import bin.study.memo.utils.CookieUtil;
import bin.study.memo.utils.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {


    @Autowired
    private UserMongodbRepository userMongodbRepository;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private CookieUtil cookieUtil;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String accessToken;
        //데이터 불러오기s

//        Enumeration headerNames = request.getHeaderNames();
//        while(headerNames.hasMoreElements()){
//            String name = (String)headerNames.nextElement();
//            String value = request.getHeader(name);
//            System.out.println(name+": "+value);
//        }

        try {
            accessToken = request.getHeader("access_token");
        }catch(Exception e){
            System.out.println(e.getMessage());
            accessToken = null;
        }

        String refreshToken = null;
        String email = null;
        String email1 = null;
        String uri = request.getRequestURI();
        try{
            if(new Regexhandler().check_uri1(uri)){
                accessToken = null;
            }
            System.out.println(accessToken);
            if(accessToken != null){
                email = jwtUtil.getEmail(accessToken);
            }
            if (email != null) {
                User user = userMongodbRepository.findByEmail(email);
                if (jwtUtil.validateToken(accessToken, user)) {
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                    System.out.println("권한:"+user.getAuthorities());
                    usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                }
            }
        }catch(Exception e){
            //refresh token 소환하기
            Cookie cookie = cookieUtil.getCookie(request,"refresh_token");
            refreshToken = cookie.getValue();
            System.out.println("테스트2 : " + refreshToken);
        }
        try {
            if(new Regexhandler().check_uri1(uri)){
                refreshToken = null;
            }
            if(refreshToken != null){
                email1 = new JwtUtil().getEmail(refreshToken);
            }
            if (email1 != null) {
                User user = userMongodbRepository.findByEmail(email1);
                if (jwtUtil.validateToken(refreshToken, user)) {
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                    usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                }
            }
        }catch(ExpiredJwtException e){
            responTokenFail2(response);
            return;
        }
        filterChain.doFilter(request,response);
    }


    private void responTokenFail2(HttpServletResponse rep) throws IOException {
        PrintWriter pw = rep.getWriter();
        TokenError tokenError = new TokenError().tokenFail();
        tokenError.setAccess_success(false);
        tokenError.setRefresh_success(false);
        pw.println(tokenError);
        pw.flush();
        pw.close();
    }
}
