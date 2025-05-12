package com.example.srm.controller;

import com.example.srm.common.Result;
import com.example.srm.security.JwtUtils;
import com.example.srm.security.LoginRequest;
import com.example.srm.security.LoginResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    public AuthController(AuthenticationManager authenticationManager, JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/login")
    public Result<LoginResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        // 验证验证码
        if (!validateCaptcha(loginRequest.getCaptchaId(), loginRequest.getCaptchaCode())) {
            return Result.error("验证码错误");
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        return Result.success(new LoginResponse(jwt));
    }

    @GetMapping("/captcha")
    public Result<?> generateCaptcha() {
        // 生成验证码
        CaptchaResult captchaResult = CaptchaUtils.generateCaptcha();
        
        // 将验证码ID和对应的答案存入Redis或Session，此处简化处理
        // redisTemplate.opsForValue().set(captchaResult.getId(), captchaResult.getCode(), 5, TimeUnit.MINUTES);
        
        return Result.success(captchaResult);
    }

    private boolean validateCaptcha(String captchaId, String captchaCode) {
        // 从Redis或Session中获取验证码答案并验证
        // String storedCode = redisTemplate.opsForValue().get(captchaId);
        // return storedCode != null && storedCode.equals(captchaCode);
        
        // 简化处理，实际开发中应使用上面的代码
        return "1234".equals(captchaCode);
    }
}    