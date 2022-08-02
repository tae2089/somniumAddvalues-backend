package bin.study.memo.repository.event;

import bin.study.memo.domain.TotalEvent1;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TotalEvent1MongodbRepository  extends MongoRepository<TotalEvent1,Long> {
    TotalEvent1 findByEmail(String email);
    TotalEvent1 findByTid(Long tid);

    TotalEvent1 findByEidAndEmail(Long tid,String email);
}
