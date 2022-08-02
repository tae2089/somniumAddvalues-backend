package bin.study.memo.service.server;

import bin.study.memo.domain.*;
import bin.study.memo.handler.DBhandler;
import bin.study.memo.repository.event.EventInfoMongodbRepository;
import bin.study.memo.repository.event.EventRecordMongodbRepository;
import bin.study.memo.repository.event.TotalEvent1MongodbRepository;
import bin.study.memo.repository.info.UserMongodbRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class EventService {

        @Autowired
        private EventInfoMongodbRepository eventInfoMongodbRepository;
        @Autowired
        private EventRecordMongodbRepository eventRecordMongodbRepository;
        @Autowired
        private TotalEvent1MongodbRepository totalEvent1MongodbRepository;
        @Autowired
        private UserService userService;
        @Autowired
        private  UserMongodbRepository userMongodbRepository;
        @Autowired
        private MongoTemplate mongoTemplate;
        @Autowired
        private MongoOperations mongoOperations;
        @Autowired
        private DBhandler dbHandler;


        public void insertReviewEvent(Reviews review) {
                //0. 현재 타입을 통해 가능한 이벤트 목록을 불러온다.

              List<EventInfo> eventList = getEventInfo();
                //List<EventInfo> eventList = new ArrayList<EventInfo>();
                // 1 . event record에 기록한다.
                if (eventList.size() != 0){
                        for (EventInfo event : eventList) {
                                TotalEvent1 totalevent1 = totalEvent1MongodbRepository.findByEmail(review.getUsername());
                                insertEventRecord(review,event);
                                if(totalevent1 == null){
                                        //2.  totalevent에 사용자 정보가 없으면 정보를 입력해 준다.
                                        insertTotalEvent1(review,event);

                                }else{
                                        // 3. totablenvt에 사용자 정보가 있다면 업데이트를 해준다.
                                        updateTotalEvent1(review,totalevent1);
                                }
                        }
                }

        }

        public void insertTotalEvent1(Reviews review, EventInfo event) {
                int story_stamp = 0;
                int active_stamp = 0;
                if (review.getType().equals("스토리형")){
                         story_stamp = 1;
                }else if(review.getType().equals("참여형")){
                        active_stamp = 1;
                }else if(review.getType().equals("기부형")){
                        story_stamp = 1;
                }
                List<Boolean> a = new ArrayList<Boolean>();
                a.add(false);
                a.add(false);
                TotalEvent1 totalEvent1 = TotalEvent1.builder()
                                                                .eid(event.getEid())
                                                                .tid(dbHandler.generateSequence(TotalEvent1.SEQUENCE_NAME))
                                                                .email(review.getUsername())
                                                                .request_event_num(0)
                                                                .requestEventPresent(a)
                                                                .receivedActiveStamp(active_stamp)
                                                                .receivedStoryStamp(story_stamp)
                                                                .unreceivedActiveStamp(5-active_stamp)
                                                                .unreceivedStoryStamp(5-story_stamp)
                                                                .build();
                totalEvent1MongodbRepository.save(totalEvent1);
        }

        public void updateTotalEvent1(Reviews review, TotalEvent1 event) {
                if (review.getType().equals("참여형")){
                        if(event.getReceivedActiveStamp() >= 5){
                                event.setReceivedActiveStamp(event.getReceivedActiveStamp());
                                event.setUnreceivedActiveStamp(-1);
                        }else{
                                event.setReceivedActiveStamp(event.getReceivedActiveStamp()+1);
                                event.setUnreceivedActiveStamp(event.getUnreceivedActiveStamp()-1);
                        }
                }
                if(review.getType().equals("스토리형") || review.getType().equals("기부형")){
                        if(event.getReceivedStoryStamp() >= 5){
                                event.setReceivedStoryStamp(event.getReceivedStoryStamp());
                                event.setUnreceivedStoryStamp(-1);
                        }else{
                                event.setReceivedStoryStamp(event.getReceivedStoryStamp()+1);
                                event.setUnreceivedStoryStamp(event.getUnreceivedStoryStamp()-1);
                        }
                }
                try {
                        Query query = new Query();
                        Update update = new Update();
                        // where절 조건
                        query.addCriteria(Criteria.where("tid").is(event.getTid()));
                        update.set("receivedActiveStamp", event.getReceivedActiveStamp());
                        update.set("receivedStoryStamp",event.getReceivedStoryStamp());
                        update.set("unreceivedActiveStamp",event.getUnreceivedActiveStamp());
                        update.set("unreceivedStoryStamp",event.getUnreceivedStoryStamp());
                        mongoTemplate.updateMulti(query, update, "TotalEvent1");
                } catch (Exception e) {
                        e.getMessage();
                }
        }

        public void insertEventRecord(Reviews review, EventInfo event) {
                EventRecord record = new EventRecord();
                record.setAttendDate(review.getCreate_date());
                record.setAttentEventType(review.getType());
                record.setEmail(review.getUsername());
                record.setEid(event.getEid());
                record.setUsedPointType(1);
                record.setErid(dbHandler.generateSequence(EventRecord.SEQUENCE_NAME));
                eventRecordMongodbRepository.save(record);
        }
        public Boolean useEventRecord(String email,Long eid) {
        //사용했다는 기록 남기기
        try{
                SimpleDateFormat date = new SimpleDateFormat("yyyyMMdd");
                String attend_date = date.format(new Date());
                EventRecord record = new EventRecord();
                record.setAttendDate(attend_date);
                record.setEmail(email);
                record.setEid(eid);
                record.setUsedPointType(2);
                record.setErid(dbHandler.generateSequence(EventRecord.SEQUENCE_NAME));
                eventRecordMongodbRepository.save(record);
                return true;
        }catch(Exception e){
                return false;
                }
        }


        public TotalEvent1 getUserEventInfo(Long eid, String email){
                Query query = new Query();
                query.addCriteria(
                        new Criteria().andOperator(
                                Criteria.where("email").is(email),
                                Criteria.where("eid").is(eid)
                        )
                );
                TotalEvent1 totalEvent1 = mongoTemplate.findOne(query, TotalEvent1.class, "TotalEvent1");
                return totalEvent1;
        }

        public List<EventInfo> getEventInfo(){
                Query query = new Query();
                query.addCriteria(
                        new Criteria().andOperator(
                                Criteria.where("eventAttendType").is("review")
                        )
                );
               List<EventInfo> totalEvent1 = mongoTemplate.find(query, EventInfo.class, "eventinfo");
                return totalEvent1;
        }

        public User updatePoint(Long eid, TotalEvent1 totalEvent1, int point, int request_event_num, String email) {
                //포인트 주기
                userService.userUpdateParticipation(email,"point",point);
                User user = userMongodbRepository.findByEmail(email);
                return user;
        }

        public EventInfo getEventInfo(Long eid) {
                EventInfo eventInfo = eventInfoMongodbRepository.findByEid(eid);
                return eventInfo;
        }

        public void updateTotalEventFirst(TotalEvent1 totalEvent1,String tel) {
                List<Boolean> a = totalEvent1.getRequestEventPresent();
                List<Boolean> b = new ArrayList<Boolean>();
                b.add(true);
                b.add(a.get(1));

                try {
                        Query query = new Query();
                        Update update = new Update();
                        // where절 조건
                        query.addCriteria(Criteria.where("tid").is(totalEvent1.getTid()));
                        update.set("requestEventPresent",b);
                        update.set("tel1",tel);
                        mongoTemplate.updateMulti(query, update, "TotalEvent1");
                } catch (Exception e) {
                        e.getMessage();
                }
        }

        public void updateTotalEventSecond(TotalEvent1 totalEvent1,String tel) {
                List<Boolean> a = totalEvent1.getRequestEventPresent();
                List<Boolean> b = new ArrayList<Boolean>();
                b.add(a.get(0));
                b.add(true);
                try {
                        Query query = new Query();
                        Update update = new Update();
                        // where절 조건
                        query.addCriteria(Criteria.where("tid").is(totalEvent1.getTid()));
                        update.set("requestEventPresent",b);
                        update.set("tel2",tel);
                        mongoTemplate.updateMulti(query, update, "TotalEvent1");
                } catch (Exception e) {
                        e.getMessage();
                }
        }
}
