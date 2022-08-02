package bin.study.memo.service.server;

import bin.study.memo.domain.Activities;
import bin.study.memo.domain.DatabaseSequence;
import bin.study.memo.domain.Point;
import bin.study.memo.repository.active.ActivityMongodbRepository;
import bin.study.memo.repository.info.PointMongodbRepository;
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
public class PointService {
    @Autowired
    private ReviewMongodbRepository reviewMongodbRepository;
    @Autowired
    private ActivityMongodbRepository activityMongodbRepository;
    @Autowired
    private PointMongodbRepository pointMongodbRepository;
    @Autowired
    private MongoOperations mongoOperations;
    @Autowired
    private UserService userService;

    public boolean insertPoint(String email,Long aid){
        Activities activity = activityMongodbRepository.findByaid(aid);
            Point point = Point.builder()
                    .aid(aid)
                    .email(email)
                    .get_point(activity.getPoint())
                    .point_content(activity.getTitle())
                    .point_id(generateSequence(Point.SEQUENCE_NAME))
                    .build();
            try{
                pointMongodbRepository.save(point);
            }catch(Exception e){
                e.printStackTrace();
                return false;
            }
            return true;
    }

    public boolean insertPoint2(String email,Activities activity){

        Point point = Point.builder()
                .aid(activity.getAid())
                .email(email)
                .get_point(activity.getPoint())
                .point_content(activity.getTitle())
                .point_id(generateSequence(Point.SEQUENCE_NAME))
                .build();
        try{
            pointMongodbRepository.save(point);
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }


    //데이터 번호 생성
    public long generateSequence(String seqName) {
        DatabaseSequence counter = mongoOperations.findAndModify(query(where("_id").is(seqName)),
                new Update().inc("seq",1), options().returnNew(true).upsert(true),
                DatabaseSequence.class);
        return !Objects.isNull(counter) ? counter.getSeq() : 1;
    }

    public List<Point> callPointList(String email){
        List<Point> list = pointMongodbRepository.findByEmail(email);
        if(list.size() == 0) return new ArrayList<Point>();
        return list;
    }


}
