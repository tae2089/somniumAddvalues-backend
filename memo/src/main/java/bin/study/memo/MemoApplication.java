package bin.study.memo;

import bin.study.memo.impl.EmailServiceImpl;
import bin.study.memo.service.dev.EmailServiceDev;
import bin.study.memo.service.local.EmailServiceLocal;
import bin.study.memo.service.server.EmailServiceServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.filter.HiddenHttpMethodFilter;


@SpringBootApplication
public class MemoApplication
{
	@Value("${spring.profiles.active}")
	private String profile;

	public static void main(String[] args) {
		SpringApplication.run(MemoApplication.class, args);
	}
	@Bean
	public HiddenHttpMethodFilter hiddenHttpMethodFilter() {
		return new HiddenHttpMethodFilter();
	}

	@Bean
	public EmailServiceImpl setEmailService(){
		if(profile.equals("dev")){
			return new EmailServiceDev();
		}
		else if(profile.equals("local")){
			return new EmailServiceLocal();
		}else{
			return new EmailServiceServer();
		}
	}
}
