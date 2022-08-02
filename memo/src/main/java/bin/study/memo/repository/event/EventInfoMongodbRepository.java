package bin.study.memo.repository.event;


import bin.study.memo.domain.EventInfo;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface EventInfoMongodbRepository extends MongoRepository<EventInfo,Long> {
    List<EventInfo> findByEventAttendType(String attend_typeList);

    EventInfo findByEid(Long eid);
}
