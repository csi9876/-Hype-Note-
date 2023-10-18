package com.example.securitystudy.auth.controller;

import com.example.securitystudy.auth.dto.*;
import com.example.securitystudy.auth.jwt.JwtTokenProvider;
import com.example.securitystudy.auth.model.PrincipalDetails;
import com.example.securitystudy.auth.service.AuthService;
import com.example.securitystudy.user.service.UserService;
import com.example.securitystudy.user.entity.User;
import com.example.securitystudy.util.BaseException;
import com.example.securitystudy.util.BaseResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;
    private final AuthService authService;

    @Autowired
    public AuthController(PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider, UserService userService, AuthService authService) {
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
        this.authService = authService;
    }

    @PostMapping("/signup")
    public BaseResponse<String> join(@RequestBody PostSignupReq postUserReq) throws BaseException {

        String encodedPassword = passwordEncoder.encode(postUserReq.getPassword());
        User user = new User(postUserReq.getUsername(), postUserReq.getNickname(),
                postUserReq.getEmail(), encodedPassword, "ROLE_USER", "none", "none", 1);
        try {
            userService.createUser(user);
            return new BaseResponse("회원가입에 성공하였습니다.");
        } catch (BaseException e) {
            return new BaseResponse(e.getStatus());
        }
    }

    @PostMapping("/signin")
    public BaseResponse<PostSigninRes> loginAuto(@RequestBody PostSigninReq postLoginReq) {
        System.out.println("postLoginReq = " + postLoginReq.getEmail() + postLoginReq.getPassword());
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(postLoginReq.getEmail(), postLoginReq.getPassword());
        System.out.println("authenticationToken = " + authenticationToken);
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        System.out.println("authentication = " + authentication);
        log.info("유저 인증 성공. 일반 로그인을 진행합니다.");

        PrincipalDetails userEntity = (PrincipalDetails) authentication.getPrincipal();
        System.out.println(userEntity);

        Long user_id = userEntity.getUser().getId();
        String accessToken = jwtTokenProvider.createAccessToken(user_id);

        return new BaseResponse<>(new PostSigninRes(accessToken, ""));
    }

    @PostMapping("/signin/auto")
    public BaseResponse<PostSigninAutoRes> login(@RequestBody PostSigninAutoReq postLoginReq) {

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(postLoginReq.getEmail(), postLoginReq.getPassword());
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        log.info("유저 인증 . 자동 로그인을 진행합니다.");

        PrincipalDetails userEntity = (PrincipalDetails) authentication.getPrincipal();
        System.out.println(userEntity);

        Long user_id = userEntity.getUser().getId();
        String accessToken = jwtTokenProvider.createAccessToken(user_id);
        String refreshToken = jwtTokenProvider.createRefreshToken(user_id);

        authService.registerRefreshToken(user_id, refreshToken);
        return new BaseResponse<>(new PostSigninAutoRes(accessToken, refreshToken));
    }

    @PostMapping("/logout")
    public BaseResponse<String> logout(HttpServletRequest request) {
        // 현재 사용자의 인증 정보를 가져옵니다.
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 만약 사용자가 인증되어 있다면 로그아웃 처리를 진행합니다.
        if (authentication != null && authentication.isAuthenticated()) {
            // 인증 정보를 무효화하고 세션을 종료합니다.
            new SecurityContextLogoutHandler().logout(request, null, authentication);

            return new BaseResponse<>("로그아웃 되었습니다.");
        }

        return new BaseResponse<>("이미 로그아웃 상태입니다.");
    }

    @PutMapping("/update")
    public BaseResponse<String> updateUser(@RequestBody PutUpdateReq updateUserReq, HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            System.out.println("token = " + token);
            String accessToken = token.substring(7);
            String userId = jwtTokenProvider.getUseridFromAcs(accessToken);
            User user = userService.findById(Long.parseLong(userId));
            // 입력받은 값으로 유저 정보를 업데이트합니다.
            user.setName(updateUserReq.getUsername());
            user.setNickname(updateUserReq.getNickname());

            // 변경된 정보로 유저 정보를 업데이트 합니다.
            userService.updateUser(user);

            return new BaseResponse<>("유저 정보가 성공적으로 업데이트 되었습니다.");
        } catch (BaseException e) {
            return new BaseResponse(e.getStatus());
        }
    }

    // 비밀번호 재설정
    // 회원 탈퇴

    @Tag(name = "소셜 로그인", description = "소셜 로그인.")
    @GetMapping("/oauth2/success")
    public BaseResponse<PostSigninAutoRes> loginSuccess(@RequestParam("accessToken") String accessToken, @RequestParam("refreshToken") String refreshToken) {
        PostSigninAutoRes postLoginRes = new PostSigninAutoRes(accessToken, refreshToken);
        return new BaseResponse<>(postLoginRes);
    }

}
