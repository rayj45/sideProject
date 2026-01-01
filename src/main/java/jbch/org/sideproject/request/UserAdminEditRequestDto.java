package jbch.org.sideproject.request;

import jbch.org.sideproject.domain.UserRole;
import jbch.org.sideproject.domain.UserStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserAdminEditRequestDto {
    private String nickName;
    private String phone;
    private String userGroup;
    private UserRole role;
    private UserStatus status;
    private Boolean sellerApproved;
}
