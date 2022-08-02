package bin.study.memo.domain;

import bin.study.memo.dto.ActivitiesDto;
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
import java.util.List;

@Data
@NoArgsConstructor
@Document("Activities")
public class Activities {
    @Transient
    public static final String SEQUENCE_NAME = "Activities_sequence";
    @Id
    @Field("_id")
    private ObjectId id;

    private Long aid;
    //제목
    private String title;
    //활동주체
    private String organization;
    //회사 링크
    private String url;
    //해시태그 - 참여활동
    private String tag1;
    //해시태그 - 수혜대상
    private String tag2;
    //마감 기한
    private String end_date;
    //이미지 소스
    private List<String> image_file;
    //활동 종류
    private String type;
    //포인트
    private Long point;
    private String home_url;

    //리뷰들
    List<Reviews> reviews ;
    private Long review_cnt;
    private Long total_point= 0L;
    private Long participants = 0L;

    @Builder
    public Activities(Long aid, String title, String organization, String url, String tag1, String tag2, String end_date, List<String> image_src, String type, Long point, List<Reviews> reviews,Long total_point,Long participants,String home_url) {
        this.aid = aid;
        this.title = title;
        this.organization = organization;
        this.url = url;
        this.tag1 = tag1;
        this.tag2 = tag2;
        this.end_date = end_date;
        this.image_file = image_src;
        this.type = type;
        this.point = point;
        this.reviews = reviews;
        this.total_point = total_point;
        this.participants = participants;
        this.home_url =  home_url;
    }


    public Activities getActivities(ActivitiesDto activity) {

        if(activity.getEnd_date() == null){
            Date from = new Date();
            SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            activity.setEnd_date( transFormat.format(from));
        }

        return Activities.builder()
                .aid(activity.getAid())
                .end_date(activity.getEnd_date())
                .organization(activity.getOrganization())
                .point(activity.getPoint())
                .tag1(activity.getTag1())
                .tag2(activity.getTag2())
                .type(activity.getType())
                .url(activity.getUrl())
                .title(activity.getTitle())
                .build();
    }
}
