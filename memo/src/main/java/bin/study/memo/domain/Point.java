package bin.study.memo.domain;

import lombok.Builder;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.text.SimpleDateFormat;
import java.util.Date;

@Data
@Document("point")
public class Point {
    public static final String SEQUENCE_NAME = "point_sequence";
    @Id
     @Field("_id")
    private ObjectId id;
    private Long point_id;
    //포인트  내용 - 타일틀
    private String point_content;
    private String point_date;
    private Long get_point;
    //사용자 정보
    private String email;
    private Long aid;

    @Builder
    public Point(ObjectId id, Long point_id, String point_content,  Long get_point, String email, Long aid) {
        this.id = id;
        this.point_id = point_id;
        this.point_content = point_content;
        this.point_date = date1();
        this.get_point = get_point;
        this.email = email;
        this.aid = aid;
    }

    private String date1(){

            Date from = new Date();
            SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
           return transFormat.format(from);

    }



}
