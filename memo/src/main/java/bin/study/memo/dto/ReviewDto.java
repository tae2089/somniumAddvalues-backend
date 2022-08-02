package bin.study.memo.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;

@Data
public class ReviewDto {


    //특수문자 제한 넣기
    //이름
    private String username;
    //후기 내용
    @NotBlank(message = "내용을 채워주세요")
    private String comment;
    //메인 이미지
    private MultipartFile image_file;
    //해시태그1
    private String tag1;
    //해시태그2
    private String tag2;
    //활동 식별 번호
    private Long aid;
    //활동 타입
    private String type;
    private String title;
    private String  created_date;
    private String organization;

    @Builder
    public ReviewDto( String username, String comment, MultipartFile img_src, String tag1, String tag2, Long aid, String type,String title,String created_date,String organization) {
        this.username = username;
        this.comment = comment;
        this.image_file = img_src;
        this.tag1 = tag1;
        this.tag2 = tag2;
        this.aid = aid;
        this.type = type;
        this.title = title;
        this.created_date = created_date;
        this.organization = organization;
    }

}
