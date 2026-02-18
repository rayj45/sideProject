package jbch.org.sideproject.security;

import jbch.org.sideproject.domain.User;
import jbch.org.sideproject.domain.UserStatus;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Getter
public class UserPrincipal implements UserDetails {

    private final Long id;
    private final String email;
    private final String nickName;
    private final String password;
    private final UserStatus status; // 상태 필드 추가
    private final Collection<? extends GrantedAuthority> authorities;

    public UserPrincipal(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.nickName = user.getNickName();
        this.password = user.getPassword();
        this.status = user.getStatus(); // 상태 초기화
        this.authorities = Collections.singletonList(new SimpleGrantedAuthority(user.getRole().name()));
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // PENDING 상태이면 계정이 잠긴 것으로 처리 (false 반환)
        return this.status != UserStatus.PENDING;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        // 휴면, 탈퇴, 차단 상태 등은 여기서 처리 가능 (현재는 true로 유지)
        return this.status == UserStatus.ACTIVE || this.status == UserStatus.PENDING; 
        // PENDING 상태에서도 로그인은 시도되지만 LockedException이 발생하도록 유도하기 위해 Enabled는 true로 둠.
        // 만약 Enabled가 false면 DisabledException이 발생함.
    }
}
