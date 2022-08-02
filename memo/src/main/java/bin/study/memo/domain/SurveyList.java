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
@Document("Survey")
public class SurveyList {
    @Transient
    public static final String SEQUENCE_NAME = "Survey_sequence";
    @Id
    @Field("_id")
    private ObjectId id;
    private Long survey_id;
    private String email;
    private List<Survey> surveyList;

    @Builder
    public SurveyList(ObjectId id, Long survey_id, List<Survey> surveyList,String email) {
        this.id = id;
        this.survey_id = survey_id;
        this.surveyList = surveyList;
        this.email = email;
    }
}
