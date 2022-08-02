package bin.study.memo.repository.event;

import bin.study.memo.domain.EventRecord;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EventRecordMongodbRepository extends MongoRepository<EventRecord,Long> {
}
