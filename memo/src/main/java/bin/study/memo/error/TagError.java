package bin.study.memo.error;

import lombok.Builder;
import lombok.Data;

@Data
public class TagError {

    private boolean Success;

    @Builder
    public TagError(boolean success) {
        Success = success;
    }
}
