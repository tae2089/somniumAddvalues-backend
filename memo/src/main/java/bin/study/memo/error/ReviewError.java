package bin.study.memo.error;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReviewError {
    //공백, 특수문자 사
    private String comment_error;
    private String image_error;
    //성공 실패
    private boolean success = true;

    @Builder
    public ReviewError(String comment_error, boolean success,String image_error) {
        this.comment_error = comment_error;
        this.success = success;
        this.image_error = image_error;
    }

    public ReviewError ReviewErrorFail(String defaultMessage){
        return ReviewError.builder()
                .comment_error(defaultMessage)
                .success(false)
                .build();
    }

    public ReviewError ReviewErrorSuccess(){
        return ReviewError.builder()
                .success(true)
                .comment_error("")
                .build();
    }

}
