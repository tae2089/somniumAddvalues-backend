package bin.study.memo.service.server;

import bin.study.memo.domain.Activities;
import bin.study.memo.domain.DatabaseSequence;
import bin.study.memo.domain.SurveyList;
import bin.study.memo.dto.ActivitiesDto;
import bin.study.memo.handler.S3handler;
import bin.study.memo.repository.active.ActivityMongodbRepository;
import bin.study.memo.repository.info.SurveyMongodbRepository;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.springframework.data.mongodb.core.FindAndModifyOptions.options;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Service
public class ActivityService {

    @Autowired
    private ActivityMongodbRepository activityMongodbRepository;
    @Autowired
    private SurveyMongodbRepository surveyMongoRepository;
    @Autowired
    private ReviewService reviewService;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private MongoOperations mongoOperations;
    @Autowired
    private S3handler s3handler;
    @Autowired
    private UserService userService;
    @Autowired
    private PointService pointService;


    public Activities find(Long aid) {
        Activities activity = activityMongodbRepository.findByaid(aid);
        activity.setReviews(reviewService.find(aid));
        System.out.println(activity.getId());
        activity.setImage_file(getImgSrc(activity.getId()));
        return activity;
    }

    //search
    public List<Activities> findByTag1(String tag) {
        return activityMongodbRepository.findByTag1OrTag2(tag,tag);
    }

    // random-search
    public List<Activities> findByTag2(String maxCnt) {
        List<Activities> activities = activityMongodbRepository.findAll();
        //랜덤함수 써서 다섯개 뽑고 append
        List<Activities> activitiesList = new ArrayList<>();
        ArrayList<Integer> randomList = new ArrayList<>();

        int value = Integer.parseInt(maxCnt);
        if(value > activities.size()){
            value = activities.size();
        }
        if(activities.size() == 0) return activitiesList;

        while(true){
            if(value == randomList.size()){
                break;
            }
            int idx = (int)(Math.random()*activities.size());
            if(randomList.size() == 0){
                randomList.add(idx);
                Activities activity    = activities.get(idx);
                if(reviewService.find(activity.getAid()) == null ){
                    activity.setReview_cnt(0L );
                }else{
                    activity.setReview_cnt((long)reviewService.find(activity.getAid()).size());
                }
                activity.setImage_file(getImgSrc(activity.getId()));
                activitiesList.add(activity);
            }else{
                if(randomList.contains(idx)) continue;
                randomList.add(idx);
                Activities activity    = activities.get(idx);
                if(reviewService.find(activity.getAid()) == null ){
                    activity.setReview_cnt(0L );
                }else{
                    activity.setReview_cnt((long)reviewService.find(activity.getAid()).size());
                }

                activity.setImage_file(getImgSrc(activity.getId()));
                activitiesList.add(activity);
            }
        }
        System.out.println("findByTag2Size:"+activitiesList.size());
        return activitiesList;
    }

    private List<String> getImgSrc(ObjectId id){
        Query query = new Query().addCriteria(Criteria.where( "_id" ).is( id));
        Document doc = mongoTemplate.findOne(query, Document.class, "Activities");  //데이터를 가져와서
        List<String> list = null;
        if (doc != null) {
            list = doc.getList("image_file", String.class);
        }
        return list;
    }

    public void insertActivity(ActivitiesDto activity)  {
        List<String> img_urls = new ArrayList<>();
        try{
            for(MultipartFile img: activity.getImage_file()){
               String uri = s3handler.upload(img,"static");
                //String uri = s3handler.upload(img,"test");
                System.out.println(uri);
                img_urls.add(uri);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        Activities activities = new Activities().getActivities(activity);
        activities.setImage_file(img_urls);
        activities.setAid(generateSequence(Activities.SEQUENCE_NAME));
        activityMongodbRepository.save(activities);
    }
    //데이터 번호 생성
    public long generateSequence(String seqName) {
        DatabaseSequence counter = mongoOperations.findAndModify(query(where("_id").is(seqName)),
                new Update().inc("seq",1), options().returnNew(true).upsert(true),
                DatabaseSequence.class);
        return !Objects.isNull(counter) ? counter.getSeq() : 1;
    }

    public void insertSurvey(SurveyList surveyList) {
        System.out.println("surveyList"+surveyList);
        surveyList.setSurvey_id(generateSequence(SurveyList.SEQUENCE_NAME));
        surveyMongoRepository.save(surveyList);
    }

    public boolean donate( String aid,String email) {

        Activities activity = activityMongodbRepository.findByaid(Long.parseLong(aid));
        //포인트 업데이트 500 차감
        userService.userPointUpdate(email, -500L);
        //point 기록하기
        activity.setPoint(-500L);
        pointService.insertPoint2(email,activity);
        //활동에 total_point 500추가, 활동인원 추가

        return updateDonateInfo(activity);
    }

    private boolean updateDonateInfo(Activities activity) {
        try{
            Query query = new Query();
            Update update = new Update();

            // where절 조건
            query.addCriteria(Criteria.where("_id").is(activity.getId()));
            update.set("total_point", activity.getTotal_point()+500L);
            update.set("participants",activity.getParticipants()+1L);
            mongoTemplate.updateMulti(query, update, "Activities");
        }catch(Exception e){
            return false;
        }
        return true;
    }

    public boolean delete(long aid) {
        try {
            Criteria criteria = new Criteria("aid");
            criteria.is(aid);
            Query query = new Query(criteria);
            mongoTemplate.remove(query, "Activities");
            Query query2 = new Query(criteria);
            mongoTemplate.remove(query2, "review");

        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public List<Activities> findByTitle(String title) {
        return activityMongodbRepository.findByTitleLike(title);
    }

    public List<Activities> findAll() {
        return activityMongodbRepository.findAll();
    }

    public boolean updateActivity(Long aid, ActivitiesDto activity) {
        //image_src list 생성
        System.out.println(activity.getImage_file());
        System.out.println(activity.getImage_file_src());
        List<String>image_src = setImgSrc(activity.getImage_file(), activity.getImage_file_src());

        //활동 선언
        Activities new_activity = Activities.builder()
                .aid(aid)
                .point(activity.getPoint())
                .tag1(activity.getTag1())
                .tag2(activity.getTag2())
                .title(activity.getTitle())
                .end_date(activity.getEnd_date())
                .type(activity.getType())
                .url(activity.getUrl())
                .organization(activity.getOrganization())
                .image_src(image_src)
                .home_url(activity.getHome_url())
                .build();
        //활동 업데이트
        System.out.println("new_activity:"+new_activity);
    return update_activity(new_activity);
    }

    private boolean update_activity(Activities new_activity) {

        try {
            Query query = new Query();
            Update update = new Update();
            // where절 조건
            query.addCriteria(Criteria.where("aid").is(new_activity.getAid()));
            update.set("point", new_activity.getPoint());
            update.set("title",new_activity.getTitle());
            update.set("url",new_activity.getUrl());
            update.set("end_date",new_activity.getEnd_date());
            update.set("tag1",new_activity.getTag1());
            update.set("tag2",new_activity.getTag2());
            update.set("type",new_activity.getType());
            update.set("organization",new_activity.getOrganization());
            update.set("image_file",new_activity.getImage_file());
            update.set("home_url",new_activity.getHome_url());
            mongoTemplate.updateMulti(query, update, "Activities");
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private List<String> setImgSrc(List<MultipartFile> image_files, List<String> image_file_src) {

        //imgae_src 넣는곳
        List<String>image_src = new ArrayList<String>();
        int i = 0;
        //
        for(String src: image_file_src){
            if(!src.equals("")){
                image_src.add(src);
            }else{
                try {
                    String uri = s3handler.upload(image_files.get(i), "static");
                    //String uri = s3handler.upload(image_files.get(i), "test");
                    image_src.add(uri);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            i++;
        }
        return image_src;
    }
}
