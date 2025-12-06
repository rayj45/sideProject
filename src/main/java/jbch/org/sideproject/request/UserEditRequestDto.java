package jbch.org.sideproject.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserEditRequestDto {
    private String username; // username 필드 추가
    private String nickName;
    private String email;
    private String phone;
    private String userGroup;
    private String currentPassword;
    private String newPassword;
}
