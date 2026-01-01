package jbch.org.sideproject.response;

import jbch.org.sideproject.domain.User;
import jbch.org.sideproject.domain.UserRole;
import jbch.org.sideproject.domain.UserStatus;
import lombok.Getter;

@Getter
public class UserAdminResponseDto {
    private final Long id;
    private final String email;
    private final String nickName;
    private final String phone;
    private final String userGroup;
    private final UserRole role;
    private final UserStatus status;
    private final Boolean sellerApproved;

    public UserAdminResponseDto(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.nickName = user.getNickName();
        this.phone = user.getPhone();
        this.userGroup = user.getUserGroup();
        this.role = user.getRole();
        this.status = user.getStatus();
        this.sellerApproved = user.getSellerApproved();
    }
}
