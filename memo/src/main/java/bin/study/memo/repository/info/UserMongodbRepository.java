package bin.study.memo.repository.info;

import bin.study.memo.domain.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserMongodbRepository  extends MongoRepository<User, Long> {

    User findByEmail(String email);
    User findByRefreshtoken(String refreshToken);
}
