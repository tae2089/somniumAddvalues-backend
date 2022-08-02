package bin.study.memo.controller.dev;

import bin.study.memo.domain.Point;
import bin.study.memo.service.server.PointService;
import bin.study.memo.utils.CookieUtil;
import bin.study.memo.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@Profile("dev")
public class PointRestController {

    @Autowired
    private PointService pointService;
    @Autowired
    private CookieUtil cookieUtil;
    @Autowired
    private JwtUtil jwtUtil;



    @GetMapping("/my-list")
    public List<Point> getPointList(@RequestHeader(required = false) String access_token , HttpServletRequest request){

        String email = "";
        if(access_token != null){
            email = jwtUtil.getEmail(access_token);
        }else{
            String refresh_token = cookieUtil.getCookie(request, "refresh_token").getValue();
            email = jwtUtil.getEmail(refresh_token);
        }
        List<Point> mylist = pointService.callPointList(email);
        return mylist;
    }

}
