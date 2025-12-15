package jbch.org.sideproject.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSignupRequestDto {
    private String nickName;
    private String password;
    private String email;
    private String phone;
    private String userGroup;
}
