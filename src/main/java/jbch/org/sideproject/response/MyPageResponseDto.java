package jbch.org.sideproject.response;

import jbch.org.sideproject.domain.User;
import lombok.Getter;

@Getter
public class MyPageResponseDto {
    private final String username;
    private final String nickName;
    private final String email;
    private final String phone;
    private final String userGroup;

    public MyPageResponseDto(User user) {
        this.username = user.getUsername();
        this.nickName = user.getNickName();
        this.email = user.getEmail();
        this.phone = user.getPhone();
        this.userGroup = user.getUserGroup();
    }
}
