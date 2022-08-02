package bin.study.memo.domain;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;




@Data
@Document("email")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Email {
    //id
    @Id
    @Field("_id")
    private ObjectId id;
    //주소
    private String mail;
    //제목
    private String title;
    //내용
    private String message;
    //식별 코드
    private String authkey;
    //확인여부
    private int type;

    @Builder
    public Email(ObjectId id, String mail, String title, String message, String authkey, int type) {
        this.id = id;
        this.mail = mail;
        this.title = title;
        this.message = message;
        this.authkey = authkey;
        this.type = type;
    }
}
