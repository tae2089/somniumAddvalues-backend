package bin.study.memo.domain;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@NoArgsConstructor
@Document("TotalEvent1")
public class TotalEvent1 {
    @Transient
    public static final String SEQUENCE_NAME = "total_event1_sequence";
    //event id
    private Long eid;
    //이메일(사용자명)
    private String email;
    private Long tid;

    //이벤트 신청 횟수(보상)
    private int request_event_num;
    //비 사용 스탬프 관리
    private int receivedActiveStamp;
    private int receivedStoryStamp;
    private int unreceivedActiveStamp;
    private int unreceivedStoryStamp;
    //바뀔 것으로 보임 -> 이벤트 신청자 정보
    private String user_info;
    private List<Boolean> requestEventPresent;
    private String tel1;
    private String tel2;


    @Builder
    public TotalEvent1(Long eid, String email, Long tid, int request_event_num, int receivedActiveStamp, int receivedStoryStamp, int unreceivedActiveStamp, int unreceivedStoryStamp, String user_info, List<Boolean> requestEventPresent, String tel1, String tel2) {
        this.eid = eid;
        this.email = email;
        this.tid = tid;
        this.request_event_num = request_event_num;
        this.receivedActiveStamp = receivedActiveStamp;
        this.receivedStoryStamp = receivedStoryStamp;
        this.unreceivedActiveStamp = unreceivedActiveStamp;
        this.unreceivedStoryStamp = unreceivedStoryStamp;
        this.user_info = user_info;
        this.requestEventPresent = requestEventPresent;
        this.tel1 = tel1;
        this.tel2 = tel2;
    }
}
