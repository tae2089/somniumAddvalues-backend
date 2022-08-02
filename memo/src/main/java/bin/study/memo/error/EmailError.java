package bin.study.memo.error;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EmailError {
    private boolean success;
    private String email_error;

    @Builder
    public EmailError(boolean success, String email_error) {
        this.success = success;
        this.email_error = email_error;
    }

    public static EmailError success() {
        return EmailError.builder()
                .email_error("")
                .success(true)
                .build();
    }

    public EmailError fail(String message) {
        return EmailError.builder()
                .success(false)
                .email_error(message)
                .build();
    }
}
