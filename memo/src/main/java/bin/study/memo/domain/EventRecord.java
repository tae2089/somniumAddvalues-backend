package bin.study.memo.domain;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@NoArgsConstructor
@Document("eventrecord")
public class EventRecord {
    @Transient
    public static final String SEQUENCE_NAME = "event_recode_sequence";
    @Id
    @Field("_id")
    private ObjectId id;

    //event record id
    private Long erid;
    //event id
    private Long eid;
    //이메일(사용자명)
    private String email;
    //활동주체
    private String attendDate;
    //활동 스토리(후기, 응원) 판단하는 타입
    private String attentEventType;
    //사용, 적립 확
    private int usedPointType;

    @Builder

    public EventRecord(ObjectId id, Long erid, Long eid, String email, String attend_date, String attent_event_type, int used_point_type) {
        this.id = id;
        this.erid = erid;
        this.eid = eid;
        this.email = email;
        this.attendDate = attend_date;
        this.attentEventType = attent_event_type;
        this.usedPointType = used_point_type;
    }
}
