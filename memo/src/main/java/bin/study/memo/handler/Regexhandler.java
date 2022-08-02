package bin.study.memo.handler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Regexhandler {


//    public Boolean check_uri(String uri){
//        //email uri check
//        boolean check_slientRefresh =  Pattern.matches("^/silent-refresh.*",uri);
//        boolean check_logout_uri = Pattern.matches("^/logout.*",uri);
//        boolean check_email_uri = Pattern.matches("^/email/.*",uri);
//        boolean check_login_uri = Pattern.matches("^/login",uri);
//        boolean check_join_uri = Pattern.matches("^/join",uri);
//        return check_login_uri | check_join_uri | check_email_uri |check_slientRefresh|check_logout_uri;
//    }

    public Boolean check_uri1(String uri){
        //email uri check
        boolean check_email_uri = Pattern.matches("^/email/.*",uri);
        boolean check_login_uri = Pattern.matches("^/login",uri);
        boolean check_join_uri = Pattern.matches("^/join",uri);
        boolean check_review_uri = Pattern.matches("^/reviews",uri);
        boolean check_search_uri = Pattern.matches("^/search",uri);
        boolean check_random_search_uri = Pattern.matches("^/random-search",uri);
        return check_login_uri | check_join_uri | check_email_uri | check_search_uri | check_review_uri | check_random_search_uri;
    }
    //제한된 특수문자 체크하기
    public Boolean check_text(String text){
        Pattern pattern = Pattern.compile("[\"']");
        Matcher matcher = pattern.matcher(text);
    return matcher.find();
    }


    public Boolean check_whitespace(String text){
        Pattern pattern = Pattern.compile("[ ]");
        Matcher matcher = pattern.matcher(text);
        return matcher.find();
    }
}
