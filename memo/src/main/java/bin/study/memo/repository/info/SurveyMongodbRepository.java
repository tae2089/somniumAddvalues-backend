package bin.study.memo.repository.info;

import bin.study.memo.domain.SurveyList;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SurveyMongodbRepository extends MongoRepository<SurveyList,Long> {
    SurveyList findByEmail(String email);
}
