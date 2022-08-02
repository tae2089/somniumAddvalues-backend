package bin.study.memo.repository.info;

import bin.study.memo.domain.Email;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface EmailMongodbRepository extends MongoRepository<Email,Long> {
    Email findByAuthkey(String authkey);

    List<Email> findByMail(String email);

    Email findAllByAuthkey(String authkey);
}
