package bin.study.memo.repository.active;


import bin.study.memo.domain.Tag;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TagMongodbRepository extends MongoRepository<Tag,Long> {
}
