package bin.study.memo.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

@Slf4j // 로깅을 위한 어노테이션
public class Filehandler {

    private final static String TEMP_FILE_PATH = "src/main/resources/";

    @Value("${spring.profiles.active}")
    private String profile;

    // 프로퍼티에서 cloude.aws.s3.bucket에 대한 정보를 불러옵니다.
    public String bucket = "sominium";             // 저는 .properties가 아닌 .yml을 이용하였습니다!

    public String upload(MultipartFile multipartFile, String dirName) throws IOException {
        File convertedFile = convert(multipartFile);
        return upload(convertedFile, dirName);
    }

    private String upload(File uploadFile, String dirName) {
        String uploadImageUrl = uploadFile.getAbsolutePath();
        return uploadImageUrl;
    }

    private void removeNewFile(File targetFile) {
        if (targetFile.delete()) {
            return;
        }
        log.info("임시 파일이 삭제 되지 못했습니다. 파일 이름: {}", targetFile.getName());
    }

    private File convert(MultipartFile file) throws IOException {
        SimpleDateFormat date = new SimpleDateFormat("yyyyMMdd");
        File convertFile = null;
        if(profile.equals("dev")){
             convertFile = new File(Objects.requireNonNull(new Date() +"/dev/"+file.getOriginalFilename()));
        }else if (profile.equals("server")){
             convertFile = new File(Objects.requireNonNull(new Date() +"/"+file.getOriginalFilename()));
        }else{
             convertFile = new File(Objects.requireNonNull(new Date() +"/local/"+file.getOriginalFilename()));
        }

        if (convertFile.createNewFile()) {
            try (FileOutputStream fos = new FileOutputStream(convertFile)) {
                fos.write(file.getBytes());
            }
            return convertFile;
        }
        throw new IllegalArgumentException(String.format("파일 변환이 실패했습니다. 파일 이름: %s", file.getName()));
    }
}
