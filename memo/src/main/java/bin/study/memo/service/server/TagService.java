package bin.study.memo.service.server;

import bin.study.memo.domain.DatabaseSequence;
import bin.study.memo.domain.Tag;
import bin.study.memo.dto.TagDto;
import bin.study.memo.handler.S3handler;
import bin.study.memo.repository.active.TagMongodbRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.springframework.data.mongodb.core.FindAndModifyOptions.options;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Service
public class TagService {

    @Autowired
    private TagMongodbRepository tagMongodbRepository;
    @Autowired
    private S3handler s3handler;
    @Autowired
    private MongoOperations mongoOperations;
    @Autowired
    private MongoTemplate mongoTemplate;

    public List<Tag> findAllTag() {
        return tagMongodbRepository.findAll(Sort.by("_id"));
    }

    public boolean insert(TagDto tag) {
        List<String> tag_url = new ArrayList<String>();
        if(tag.getImage_file()!= null) {
            for(MultipartFile file : tag.getImage_file()) {
                try {
                    if(file != null){
                        String uri  = s3handler.upload(file, "tag");
                        tag_url.add(uri);
                    }
                } catch (IOException e) {
                   return false;
                }
            }
        }

        Tag tag1 = Tag.builder()
                .tag_name(tag.getTag_name())
                .image_file(tag_url)
                .tid(generateSequence(Tag.SEQUENCE_NAME))
                .build();

        try {
            tagMongodbRepository.save(tag1);
            return true;
        }catch (Exception e){
            return false;
        }
    }


    public long generateSequence(String seqName) {
        DatabaseSequence counter = mongoOperations.findAndModify(query(where("_id").is(seqName)),
                new Update().inc("seq",1), options().returnNew(true).upsert(true),
                DatabaseSequence.class);
        return !Objects.isNull(counter) ? counter.getSeq() : 1;
    }


    public boolean deleteTag(String tid) {
        try {
            Criteria criteria = new Criteria("tid");
            criteria.is(Long.parseLong(tid));
            Query query = new Query(criteria);
            mongoTemplate.remove(query, "Tag");
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
