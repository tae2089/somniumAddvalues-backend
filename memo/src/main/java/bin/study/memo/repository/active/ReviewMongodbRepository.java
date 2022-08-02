package bin.study.memo.repository.active;

import bin.study.memo.domain.Reviews;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ReviewMongodbRepository extends MongoRepository<Reviews,Long> {
    List<Reviews> findByaid(Long aid);

    List<Reviews> findByAidAndUsername(Long aid, String email);

    List<Reviews> findByTag1(String tag1);

    List<Reviews> findByType(String type);
}
