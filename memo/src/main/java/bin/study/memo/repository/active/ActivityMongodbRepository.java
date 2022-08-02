package bin.study.memo.repository.active;


import bin.study.memo.domain.Activities;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ActivityMongodbRepository extends MongoRepository<Activities,Long> {
    Activities findByaid(Long aid);

    List<Activities> findByTag1(String tag);
    List<Activities> findByTag1LikeOrTag2LikeOrTitleLike(String tag1,String tag2,String Title);


    List<Activities> findByTag1OrTag2(String s, String s1);
    List<Activities> findByTitleLike(String s);
}
