package bin.study.memo.domain;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;

@Data
//@Entity
//@Table(name = "tbl_user")
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
//@EntityListeners(AuditingEntityListener.class)

@Document("user")
public class User implements UserDetails {

//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name="user_id")
@Transient
public static final String SEQUENCE_NAME = "user_sequence";

    @Id
    @Field("_id")
    private Long user_id;
    @Field("firstname")
    private String firstname;
    @Field("secondname")
    private String secondname;
    @Field("email")
    private String email;
    @Field("password")
    private String password;
    @Field("roles")
    private String roles = "ROLE_USER";
    @Field("refreshtoken")
    private String refreshtoken;
    @Field("createdDate")
    private LocalDate createdDate;
    @Field("modifiedDate")
    private LocalDate modifiedDate;
    @Field("point")
    private Long point=0L;
    @Field("participation")
    private Long participation= 0L;



    @Builder
    public  User(Long id, String firstname, String secondname, String email, String password,String refreshToken,Long point,Long participation) {
        this.user_id = id;
        this.email = email;
        this.password = password;
        this.firstname = firstname;
        this.secondname = secondname;
        this.createdDate = LocalDate.now();
        this.refreshtoken = refreshToken;
        this.point = point;
        this.participation = participation;
    }


    public User() {

    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        ArrayList<GrantedAuthority> auth = new ArrayList<GrantedAuthority>();
        if (this.roles.length() > 0) {
            for (String role :
                    this.roles.split(",")) {
                auth.add(new SimpleGrantedAuthority(role));
            }
            return auth;
        }
        else{
            return new ArrayList<>();
        }
    }

    @Override
    public String getUsername() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
