package bin.study.memo.repository.info;

import bin.study.memo.domain.Point;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PointMongodbRepository extends MongoRepository<Point,Long> {
    List<Point> findByEmail(String email);
}
