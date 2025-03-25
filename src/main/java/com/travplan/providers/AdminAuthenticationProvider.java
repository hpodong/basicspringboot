package com.travplan.providers;

import com.travplan.dto.BSQuery;
import com.travplan.enums.AdminStatus;
import com.travplan.models.admin.Admin;
import com.travplan.models.admin.AdminMenu;
import com.travplan.models.logs.AdminConnectLog;
import com.travplan.services.manage.AdminConnectLogService;
import com.travplan.services.manage.AdminMenuService;
import com.travplan.services.manage.AdminService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class AdminAuthenticationProvider implements AuthenticationProvider {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AdminService service;
    private final AdminMenuService adminMenuService;
    private final AdminConnectLogService connectLogService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        final UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) authentication;

        final HttpServletRequest request = getRequest();

        final String id = token.getName();
        final String password = token.getCredentials().toString();

        final String save_id = request.getParameter("save_id");
        final Cookie cookie = new Cookie("save_id", id);
        if(save_id == null) cookie.setMaxAge(0);
        getResponse().addCookie(cookie);

        final Admin admin = findById(id);

        if(admin == null) throw new BadCredentialsException("아이디를 확인해주세요.");
        else if(!bCryptPasswordEncoder.matches(password, admin.getPassword())) throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
        else if(admin.getStatus().equals(AdminStatus.WAITING)) throw new BadCredentialsException("승인대기 중인 관리자입니다.");
        else if(admin.getStatus().equals(AdminStatus.PAUSE)) throw new BadCredentialsException("활동정지 상태인 관리자입니다.");

        final List<GrantedAuthority> authorities = new ArrayList<>();
        final List<AdminMenu> menus = adminMenuService.getAllPageMenus(admin.getIdx());

        menus.forEach(menu -> authorities.add(new SimpleGrantedAuthority("ROLE_" + menu.linkToRole())));

        final AdminConnectLog connectLog = new AdminConnectLog();
        connectLog.setAdmin_idx(admin.getIdx());
        connectLog.setRemote_ip(request.getRemoteAddr());
        connectLogService.insert(connectLog);

        return new UsernamePasswordAuthenticationToken(admin, password, authorities);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

    private HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
    }

    private HttpServletResponse getResponse() {
        return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getResponse();
    }

    private Admin findById(String id) {
        final BSQuery bsq = new BSQuery(Admin.class);
        bsq.setWhere("a_dldt IS NULL", "a_id = ?");
        bsq.setLimit(1);
        bsq.addArgs(id);
        return service.findOne(bsq, Admin::new);
    }
}
