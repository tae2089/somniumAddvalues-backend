package bin.study.memo.service.server;

import bin.study.memo.domain.SurveyList;
import bin.study.memo.repository.info.SurveyMongodbRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SurveyService {
    @Autowired
    private SurveyMongodbRepository surveyMongodbRepository;

    public boolean insertSurvey(){

        return true;
    }


    public Boolean checkEmail(String email) {
        try{
            SurveyList survey = surveyMongodbRepository.findByEmail(email);
            if(survey != null){
                return true;
            }else{
                return false;
            }
        }catch(Exception e){
            return false;
        }
    }
}
