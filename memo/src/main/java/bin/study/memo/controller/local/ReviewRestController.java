package bin.study.memo.controller.local;

import bin.study.memo.domain.Activities;
import bin.study.memo.domain.Reviews;
import bin.study.memo.dto.ReviewDto;
import bin.study.memo.error.ReviewError;
import bin.study.memo.handler.Regexhandler;
import bin.study.memo.repository.active.ActivityMongodbRepository;
import bin.study.memo.service.server.ReviewService;
import bin.study.memo.utils.CookieUtil;
import bin.study.memo.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


@RestController
@Profile("local")
public class ReviewRestController {

    @Autowired
    private ReviewService reviewService;
    @Autowired
    private ActivityMongodbRepository activityMongodbRepository;

    @GetMapping("/reviews")
    public List<Reviews> getReviews(@RequestParam String maxCnt){
        return reviewService.findReviews(maxCnt);
    }
//,consumes={MediaType.MULTIPART_FORM_DATA_VALUE}

    @PostMapping(value="/reviews")
    @ResponseBody
    public ReviewError addReview( ReviewDto reviewDto, Errors errors, HttpServletRequest request){
        ReviewError reviewError = new ReviewError();
        String email = new JwtUtil().getEmail((new CookieUtil().getCookie(request,"refresh_token").getValue()));
        Activities activity = activityMongodbRepository.findByaid(reviewDto.getAid());

        //에러 확인
        if (reviewDto.getComment().equals("")) reviewError = reviewService.validateHandling();
        if (new Regexhandler().check_text(reviewDto.getComment())) reviewError = reviewService.validRegex();
        if(activity.getType().equals(new String("참여형"))&&reviewDto.getImage_file().isEmpty())reviewError = reviewService.checkFile(reviewError);
        reviewDto.setUsername(email);
        reviewDto.setOrganization(activity.getOrganization());
        if(!reviewService.checkDuplicateAcitvity(email,reviewDto.getAid())) reviewError = reviewError.ReviewErrorFail("이미 참여하신 활동입니다.");
        if(!reviewError.isSuccess()) return reviewError;
        reviewError =  reviewService.insertReview(reviewDto,activity,reviewError);

        return reviewError.ReviewErrorSuccess();
    }

}
