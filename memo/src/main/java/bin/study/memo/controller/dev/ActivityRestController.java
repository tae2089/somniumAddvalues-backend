package bin.study.memo.controller.dev;

import bin.study.memo.domain.Activities;
import bin.study.memo.domain.Survey;
import bin.study.memo.domain.SurveyList;
import bin.study.memo.dto.ActivitiesDto;
import bin.study.memo.error.ActivityError;
import bin.study.memo.service.server.ActivityService;
import bin.study.memo.utils.CookieUtil;
import bin.study.memo.utils.JwtUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@RestController
@Profile("dev")
public class ActivityRestController {

    @Autowired
    private ActivityService activityService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private CookieUtil cookieUtil;


    @GetMapping("/activities/{aid}")
    @ResponseBody
    public Activities getActivityInfo(@PathVariable("aid") String aid){
        return activityService.find(Long.parseLong(aid));
    }

    @PostMapping("/activities/{aid}/donate")
    @ResponseBody
    public ActivityError donateActivity(@PathVariable("aid") String aid, @RequestHeader(required = false) String access_token,HttpServletRequest request){
        String email = "";
        if(access_token != null){
            email = jwtUtil.getEmail(access_token);
        }else{
            String refresh_token = cookieUtil.getCookie(request, "refresh_token").getValue();
            email = jwtUtil.getEmail(refresh_token);
        }

        ActivityError activityError = new ActivityError();
        activityError.setSuccess(activityService.donate(aid,email));

        return  activityError;
    }

    @GetMapping("/search")
    @ResponseBody
    public List<Activities> getActivities(@RequestParam(required = false) String tag,@RequestParam(required = false) String title, @RequestParam(required = false) String maxCnt){

        if(!tag.trim().equals("")){
            if(tag.equals("전체")) {
                return activityService.findAll();
            }else{
                return activityService.findByTag1(tag);
            }
        }
        if(!title.trim().equals("")){
                return activityService.findByTitle(title);
        }
        return new ArrayList<Activities>();
    }

    @GetMapping("/random-search")
    @ResponseBody
    public List<Activities> getRandomShuffleActivities( @RequestParam String maxCnt){
        return activityService.findByTag2(maxCnt);
    }

    @PostMapping(value="/activities")
    @ResponseBody
    public Boolean addActivities(ActivitiesDto activity ){
        System.out.println(activity);
        activityService.insertActivity(activity);
        return new ActivityError().success().isSuccess();
    }

    @PostMapping(value="/survey")
    public void addSurvey(@RequestBody Map<String, Object> parameters, HttpServletRequest request) throws JsonProcessingException {
        String json = parameters.get("items").toString();
        System.out.println(json);
        Gson gson  = new Gson();
        JsonReader reader = new JsonReader(new StringReader(json));
        reader.setLenient(true);

        List<Survey> list2 = gson.fromJson(reader, new TypeToken<List<Survey>>(){}.getType());
        System.out.println(list2);

        String refresh_token = cookieUtil.getCookie(request, "refresh_token").getValue();
        String email = jwtUtil.getEmail(refresh_token);


        SurveyList a = new SurveyList();
        a.setSurveyList(list2);
        a.setEmail(email);
        activityService.insertSurvey(a);
    }

    @DeleteMapping("/activities/{aid}")
    public  ActivityError deleteActivity( @PathVariable("aid")String aid){
        ActivityError activityError = new ActivityError();
        activityError.setSuccess(activityService.delete(Long.parseLong(aid)));
        return activityError;
    }

    @PutMapping("/activities/{aid}")
    public  ActivityError putActivity( @PathVariable("aid")String aid,  ActivitiesDto activity){
        ActivityError activityError = new ActivityError();
        System.out.println(activity);
        activityError.setSuccess(activityService.updateActivity(Long.parseLong(aid),activity));
        return activityError;
    }
}
