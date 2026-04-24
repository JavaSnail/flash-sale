package com.flashsale.user.application;

import com.flashsale.common.exception.BizException;
import com.flashsale.common.result.ErrorCode;
import com.flashsale.user.api.dto.LoginRequest;
import com.flashsale.user.api.dto.RegisterRequest;
import com.flashsale.user.api.dto.UserDTO;
import com.flashsale.user.domain.PasswordEncoder;
import com.flashsale.user.domain.User;
import com.flashsale.user.domain.UserRepository;
import cn.dev33.satoken.stp.StpUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public String login(LoginRequest request) {
        User user = userRepository.findByPhone(request.getPhone())
                .orElseThrow(() -> new BizException(ErrorCode.PARAM_ERROR, "用户不存在"));
        if (!user.checkPassword(request.getPassword(), passwordEncoder)) {
            throw new BizException(ErrorCode.PARAM_ERROR, "密码错误");
        }
        StpUtil.login(user.getId());
        return StpUtil.getTokenValue();
    }

    public void register(RegisterRequest request) {
        if (userRepository.findByPhone(request.getPhone()).isPresent()) {
            throw new BizException(ErrorCode.PARAM_ERROR, "手机号已注册");
        }
        String salt = passwordEncoder.generateSalt();
        String encodedPassword = passwordEncoder.encode(request.getPassword(), salt);
        User user = new User(request.getPhone(), request.getNickname(), encodedPassword, salt);
        userRepository.save(user);
    }

    public UserDTO getById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND));
        return toDTO(user);
    }

    public UserDTO getCurrentUser() {
        long userId = StpUtil.getLoginIdAsLong();
        return getById(userId);
    }

    private UserDTO toDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setNickname(user.getNickname());
        dto.setPhone(user.getPhone());
        return dto;
    }
}
