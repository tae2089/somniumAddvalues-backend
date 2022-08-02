package bin.study.memo.error;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ActivityError {
    //공백, 특수문자 사용
    private String comment_error;
    //성공 실패
    private boolean success;

    @Builder
    public ActivityError(String comment_error, boolean success) {
        this.comment_error = comment_error;
        this.success = success;
    }



    public ActivityError success() {


        return ActivityError.builder()
                .comment_error("")
                .success(true)
                .build();
    }


}
