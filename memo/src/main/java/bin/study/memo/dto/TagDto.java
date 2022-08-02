package bin.study.memo.dto;


import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class TagDto {
    private String tag_name;
    private List<MultipartFile> image_file;

    @Builder
    public TagDto(String tag_name, List<MultipartFile> image_file) {
        this.tag_name = tag_name;
        this.image_file = image_file;
    }
}
