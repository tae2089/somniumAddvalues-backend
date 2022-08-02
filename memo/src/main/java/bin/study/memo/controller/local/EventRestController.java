package bin.study.memo.controller.local;

import bin.study.memo.domain.EventInfo;
import bin.study.memo.domain.TotalEvent1;
import bin.study.memo.service.server.EventService;
import bin.study.memo.utils.CookieUtil;
import bin.study.memo.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@Profile("local")
public class EventRestController {
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private CookieUtil cookieUtil;
    @Autowired
    private EventService eventService;

    @GetMapping("/events/{eid}")
    public TotalEvent1 getUserEventInfo(@PathVariable Long eid, HttpServletRequest req){
        String refresh_token = cookieUtil.getCookie(req, "refresh_token").getValue();
        String email = jwtUtil.getEmail(refresh_token);
        TotalEvent1 totalEvent1 = eventService.getUserEventInfo(eid,email);
        return totalEvent1;
    }

    @PostMapping("/events/{eid}")
    public Boolean participantsEvent(@PathVariable Long eid, HttpServletRequest req, @RequestBody  Map<String,Object> data){
        String refresh_token = cookieUtil.getCookie(req, "refresh_token").getValue();
        String email = jwtUtil.getEmail(refresh_token);

        TotalEvent1 totalEvent1 = eventService.getUserEventInfo(eid,email);
        EventInfo eventInfo = eventService.getEventInfo(eid);

        int request_event_num = (int) data.get("request_event_num");
        String tel = (String)data.get("user_info");

        eventService.useEventRecord(email,eid);
        if (request_event_num == 1){
            eventService.updateTotalEventFirst(totalEvent1,tel);
        } else{
            eventService.updateTotalEventSecond(totalEvent1,tel);
        }
        return true;
    }
}
