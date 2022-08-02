package bin.study.memo.service.server;

import bin.study.memo.domain.Activities;
import bin.study.memo.domain.DatabaseSequence;
import bin.study.memo.dto.ReviewDto;
import bin.study.memo.domain.Reviews;
import bin.study.memo.error.ReviewError;
import bin.study.memo.handler.S3handler;
import bin.study.memo.repository.active.ActivityMongodbRepository;
import bin.study.memo.repository.active.ReviewMongodbRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.springframework.data.mongodb.core.FindAndModifyOptions.options;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Service
public class ReviewService {

    @Autowired
    private ReviewMongodbRepository reviewMongodbRepository;
    @Autowired
    private ActivityMongodbRepository activityMongodbRepository;
    @Autowired
    private EventService eventService;
    @Autowired
    private PointService pointService;
    @Autowired
    private MongoOperations mongoOperations;
    @Autowired
    private UserService userService;
    @Autowired
    private S3handler s3handler;


    public List<Reviews> find(Long aid) {
        return reviewMongodbRepository.findByaid(aid);
    }

    public List<Reviews> findReviews(String parseLong) {
        List<Reviews> reviewsList = reviewMongodbRepository.findByType("참여형");
        List<Reviews> reviewsList2 = new ArrayList<>();
        ArrayList<Integer> randomList = new ArrayList<>();
        if (reviewsList.size() == 0) return reviewsList2;
        int value = Integer.parseInt(parseLong);
        if (value > reviewsList.size()) {
            value = reviewsList.size();
        }
       while(true) {
            int idx = (int) (Math.random() * reviewsList.size());

            if(value == randomList.size()){
                break;
            }

            if (randomList.size() == 0) {
                randomList.add(idx);
                Reviews reviews = reviewsList.get(idx);
                reviewsList2.add(reviews);
            } else {
                if (randomList.contains(idx)) {
                    continue;
                }
                randomList.add(idx);
                Reviews reviews = reviewsList.get(idx);
                reviewsList2.add(reviews);
                }
            }

        return reviewsList2;
    }

    private Reviews findAcitivity(Reviews reviews) {
        Activities activity = activityMongodbRepository.findByaid(reviews.getAid());
        reviews.setTitle(activity.getTitle());
        reviews.setTag1(activity.getTag1());
        reviews.setTag2(activity.getTag2());
        reviews.setType(activity.getType());
        return reviews;
    }


    //UserError은 여기서 수정하기
    public ReviewError validateHandling() {
        return new ReviewError().ReviewErrorFail("공백 입니다.");
    }

    public ReviewError validRegex() {
        return new ReviewError().ReviewErrorFail(" 제한된 특수문자인 ' 혹은 \"를 입력하셨습니다.");
    }

    public ReviewError insertReview(ReviewDto reviewDto, Activities activity, ReviewError reviewError) {
        Reviews review = new Reviews().changeReview(reviewDto);

        review.setRid(generateSequence(Reviews.SEQUENCE_NAME));
        userService.userPointUpdate(review.getUsername(), activity.getPoint());
        userService.userUpdateParticipation(review.getUsername(),"participation",0);
        pointService.insertPoint(review.getUsername(), review.getAid());
        review = findAcitivity(review);

            try {
                if(reviewDto.getImage_file() != null) {
               String uri  = s3handler.upload(reviewDto.getImage_file(), "reviews");
               review.setImage_file(uri);
                }
                reviewMongodbRepository.save(review);
                eventService.insertReviewEvent(review);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return reviewError;
        }


    //데이터 번호 생성
    public long generateSequence(String seqName) {

        DatabaseSequence counter = mongoOperations.findAndModify(query(where("_id").is(seqName)),
                new Update().inc("seq",1), options().returnNew(true).upsert(true),
                DatabaseSequence.class);
        return !Objects.isNull(counter) ? counter.getSeq() : 1;
    }

    public ReviewError checkFile(ReviewError reviewError) {
        reviewError.setImage_error("최소 1장의 이미지가 필요합니다.");
        return reviewError;
    }

    public boolean checkDuplicateAcitvity(String email, Long aid) {
        List<Reviews> review = reviewMongodbRepository.findByAidAndUsername(aid,email);
        if (review.size() !=0) return false;
        return true;
    }
}

