package bin.study.memo.domain;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
@NoArgsConstructor
@Document("eventinfo")
public class EventInfo {
    @Transient
    public static final String SEQUENCE_NAME = "event_info_sequence";
    @Id
    @Field("_id")
    private ObjectId id;

    //event id
    private Long eid;
    //event 제목
    private String eventName;
    //활동주체
    private String eventEndDate;
    //회사 링크
    private String eventStartDate;
    //이벤트 유형
    private String eventType;
    //진행중
    private int eventEnd = 1;

    private String eventAttendType;

    private List<Integer> eventPoint;

    //     db.eventinfo.insertOne({ eid : NumberLong(1), eventName : "테스트", eventEndDate : "20210315", eventStartDate: "20210222" .eventType : "stamp",
    //     eventEnd : "0", eventAttendType : "review"} )

    @Builder
    public EventInfo(ObjectId id, Long eid, String evet_name, String event_end_date, String event_start_date, String event_type, int event_end, String event_attend_type, List<Integer> event_point) {
        this.id = id;
        this.eid = eid;
        this.eventName= evet_name;
        this.eventEndDate = event_end_date;
        this.eventStartDate = event_start_date;
        this.eventType = event_type;
        this.eventEnd = event_end;
        this.eventAttendType = event_attend_type;
        this.eventPoint = event_point;
    }
}
