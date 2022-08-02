package bin.study.memo.dto;

import bin.study.memo.domain.Reviews;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Data
@NoArgsConstructor
public class ActivitiesDto {

    private Long aid;
    //제목
    private String title;
    //활동주체
    private String organization;
    //회사 링크
    private String url;
    //해시태그 - 참여활동
    private String tag1;
    //해시태그 - 수혜대상
    private String tag2;
    //마감 기한
    private String end_date;
    private List<String> image_file_src;
    private String home_url;
    //이미지 소스
    private List<MultipartFile> image_file;
    //활동 종류
    private String type;
    //포인트
    private Long point;
    //리뷰들
    List<Reviews> reviews ;

    @Builder
    public ActivitiesDto(Long aid, String title, String organization, String url, String tag1, String tag2, String end_date, List<MultipartFile> image_file, List<String>image_file_src,String type, Long point, List<Reviews> reviews,String home_url ) {
        this.aid = aid;
        this.title = title;
        this.organization = organization;
        this.url = url;
        this.tag1 = tag1;
        this.tag2 = tag2;
        this.end_date = end_date;
        this.image_file = image_file;
        this.type = type;
        this.point = point;
        this.reviews = reviews;
        this.image_file_src = image_file_src;
        this.home_url = home_url;
    }
    public  LocalDate switch_date(String date){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            return LocalDate.parse(date, formatter);
    }

}
