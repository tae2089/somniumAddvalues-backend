package bin.study.memo.utils;

import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class CookieUtil {

    public Cookie getCookie(HttpServletRequest req, String cookieName){
        final Cookie[] cookies = req.getCookies();
        if(cookies == null) return null;
        for(int i=0; i <cookies.length;i++){
            if(cookies[i].getName().equals(cookieName)){
                return cookies[i];
            }
        }
        return null;
    }

    public HttpServletResponse deleteCookie(HttpServletRequest request, HttpServletResponse response){

        Cookie[] cookies = request.getCookies();            // 요청정보로부터 쿠키를 가져온다.
        if(cookies!=null){
        for(int i = 0 ; i<cookies.length; i++){
            // 쿠키 배열을 반복문으로 돌린다.
            cookies[i].setMaxAge(0);                        // 특정 쿠키를 더 이상 사용하지 못하게 하기 위해서는
            // 쿠키의 유효시간을 만료시킨다.
            cookies[i].setValue(null);
            response.addCookie(cookies[i]);            // 해당 쿠키를 응답에 추가(수정)한다.
        }
        }
        return response;
    }
    public HttpServletResponse deleteCookie2(HttpServletRequest request, HttpServletResponse response,String name){

        Cookie[] cookies = request.getCookies();            // 요청정보로부터 쿠키를 가져온다.
        if(cookies!=null){
            for(int i = 0 ; i<cookies.length; i++){
                // 쿠키 배열을 반복문으로 돌린다.
                if(cookies[i].getName().equals(name)){
                    cookies[i].setMaxAge(0);                        // 특정 쿠키를 더 이상 사용하지 못하게 하기 위해서는
                    // 쿠키의 유효시간을 만료시킨다.
                    cookies[i].setValue(null);
                }
                response.addCookie(cookies[i]);            // 해당 쿠키를 응답에 추가(수정)한다.
            }
        }
        return response;
    }
}
