package bin.study.memo.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;


public class Emailhandler {

    @Autowired
    private final JavaMailSender sender;

    private final MimeMessage message;
    private final MimeMessageHelper messageHelper;

    // 생성자
    public Emailhandler(JavaMailSender jSender) throws
            MessagingException {
        this.sender = jSender;
        message = jSender.createMimeMessage();
        messageHelper = new MimeMessageHelper(message, true, "UTF-8");
    }

//    보내는 사람 이메일
//    public void setFrom(String fromAddress) throws MessagingException {
//        messageHelper.setFrom(fromAddress);
//    }

    // 받는 사람 이메일
    public void setTo(String email) throws MessagingException {
        messageHelper.setTo(email);
    }

    // 제목
    public void setSubject(String subject) throws MessagingException {
        messageHelper.setSubject(subject);
    }

    // 메일 내용
    public void setText(String text, boolean useHtml) throws MessagingException {
        messageHelper.setText(text, useHtml);
        //FileSystemResource res = new FileSystemResource(new File("/Users/imtaebin/Documents/Codes/ThePlus/prototype/memo/src/main/resources/static/1.jpeg"));
        //messageHelper.addInline("sample-img", res);
    }

//    // 첨부 파일
//    public void setAttach(String displayFileName, String pathToAttachment) throws MessagingException, IOException {
//        File file = new ClassPathResource(pathToAttachment).getFile();
//        FileSystemResource fsr = new FileSystemResource(file);
//
//        messageHelper.addAttachment(displayFileName, fsr);
//    }

//    // 이미지 삽입
//    public void setInline(String contentId, String pathToInline) throws MessagingException, IOException {
//        File file = new ClassPathResource(pathToInline).getFile();
//        FileSystemResource fsr = new FileSystemResource(file);
//
//        messageHelper.addInline(contentId, fsr);
//    }

    // 발송
    public void send() {
        try {
            sender.send(message);
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
}
