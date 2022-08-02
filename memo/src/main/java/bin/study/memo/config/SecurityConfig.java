package bin.study.memo.config;


import bin.study.memo.filter.JwtRequestFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;

//import org.springframework.web.filter.CorsFilter;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
@Profile("server")
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Override
    public void configure(WebSecurity web) { // 5
        web.ignoring().antMatchers("/css/**", "/js/**", "/img/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        http.cors().configurationSource(corsConfigurationSource())
                .and()
                .csrf().disable()
                .httpBasic().disable()
                .formLogin().disable()
                .logout().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                .antMatchers("/").permitAll()
               .antMatchers("/token").hasAuthority("ROLE_USER")
                .antMatchers("/userinfo").permitAll()
                .antMatchers("/login").permitAll()
                .antMatchers("/logout_").permitAll()
                .antMatchers(HttpMethod.POST,"/activities").hasAuthority("ROLE_MANAGER")
                .antMatchers(HttpMethod.DELETE,"/activities/**").hasAuthority("ROLE_MANAGER")
                .antMatchers(HttpMethod.GET,"/activities/**").permitAll()
                .antMatchers(HttpMethod.GET,"/tags").permitAll()
                .antMatchers(HttpMethod.POST,"/tags").hasAuthority("ROLE_MANAGER")
                .antMatchers(HttpMethod.DELETE,"/tags/**").hasAuthority("ROLE_MANAGER")
                .antMatchers("/find-password").permitAll()
                .antMatchers("/random-search").permitAll()
                .antMatchers("/find-password").permitAll()
                .antMatchers("/silent-refresh").permitAll()
                .antMatchers("/join").permitAll()
                .antMatchers("/email/**").permitAll()
                .antMatchers("/search").permitAll()
                .antMatchers("/reviews").permitAll()
                .antMatchers("/my-list").permitAll()
                .antMatchers("/survey").permitAll()
                .anyRequest().authenticated();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        //configuration.addAllowedOrigin("http://addvalue.kr.s3-website.ap-northeast-2.amazonaws.com");
        configuration.addAllowedOrigin("*");
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        configuration.setAllowCredentials(true);
        configuration.addExposedHeader("access_token");
        configuration.addExposedHeader("refresh_token");
        configuration.setExposedHeaders(Arrays.asList("access_token","refresh_token"));

          configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);;
        return source;
    }
}
