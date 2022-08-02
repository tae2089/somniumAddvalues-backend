package bin.study.memo.domain;

import lombok.Builder;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
@Document("Tag")
public class Tag {

    @Transient
    public static final String SEQUENCE_NAME = "Tag_sequence";

    @Id
    @Field("_id")
    private ObjectId id;

    private Long tid;
    private String tag_name;
    private List<String> image_file;

    @Builder
    public Tag(String tag_name, List<String> image_file,Long tid) {
        this.tid = tid;
        this.tag_name = tag_name;
        this.image_file = image_file;
    }
}
