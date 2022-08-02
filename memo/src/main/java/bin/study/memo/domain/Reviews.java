package bin.study.memo.domain;

import bin.study.memo.dto.ReviewDto;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.text.SimpleDateFormat;
import java.util.Date;

@Data
@NoArgsConstructor
@Document("review")
public class Reviews {
    @Transient
    public static final String SEQUENCE_NAME = "review_sequence";
    @Id
    @Field("_id")
    private ObjectId id;

    private String title;

    private Long rid;
    //이름
    private String username;
    //후기 내용
    private String comment;
    //메인 이미지
    private String image_file;
    //해시태그1
    private String tag1;
    //해시태그2
    private String tag2;
    //활동 식별 번호
    private Long aid;
    //활동 타입
    private String type;
    //생성날짜
    @Field("create_date")
    private String create_date;
    //회사
    private String organization;

    private boolean event_success=true;

    @Builder
    public Reviews(Long rid, String username, String comment, String img_src, String tag1, String tag2, Long aid, String type, String created_date,String title,String organization,boolean event_success) {
        this.rid = rid;
        this.username = username;
        this.comment = comment;
        this.image_file = img_src;
        this.tag1 = tag1;
        this.tag2 = tag2;
        this.aid = aid;
        this.type = type;
        this.create_date = created_date;
        this.title = title;
        this.organization = organization;
        this.event_success = event_success;
    }

    public Reviews changeReview(ReviewDto reviewDto) {
        String path ="";
        if(reviewDto.getImage_file()!= null){
            path = "static/"+reviewDto.getImage_file().getOriginalFilename();
        }

            Date from = new Date();
            SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd");
            reviewDto.setCreated_date( transFormat.format(from));


        return Reviews.builder()
                .aid(reviewDto.getAid())
                .comment(reviewDto.getComment())
                .created_date(reviewDto.getCreated_date())
                .type(reviewDto.getType())
                .tag1(reviewDto.getTag1())
                .tag2(reviewDto.getTag2())
                .type(reviewDto.getType())
                .title(reviewDto.getTitle())
                .img_src(path)
                .username(reviewDto.getUsername())
                .organization(reviewDto.getOrganization())
                .event_success(true)
                .build();
    }
}
