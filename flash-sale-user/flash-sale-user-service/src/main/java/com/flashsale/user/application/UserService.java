package com.flashsale.user.application;

import org.springframework.stereotype.Service;

import com.flashsale.common.exception.BizException;
import com.flashsale.common.result.ErrorCode;
import com.flashsale.user.api.dto.LoginRequest;
import com.flashsale.user.api.dto.RegisterRequest;
import com.flashsale.user.api.dto.UserDTO;
import com.flashsale.user.domain.InvalidCredentialsException;
import com.flashsale.user.domain.Nickname;
import com.flashsale.user.domain.PasswordEncoder;
import com.flashsale.user.domain.PhoneNumber;
import com.flashsale.user.domain.User;
import com.flashsale.user.domain.UserRepository;

import cn.dev33.satoken.stp.StpUtil;
import lombok.RequiredArgsConstructor;

/**
 * 用户应用服务。
 * <p>
 * 协调领域对象完成用户用例，自身不包含业务规则。 密码加密逻辑已收敛至 {@link User#register} 和 {@link User#authenticate}， 本服务仅负责：
 * </p>
 * <ul>
 * <li>跨聚合不变量（手机号唯一性查重 —— 需查 Repository）</li>
 * <li>调用领域工厂创建聚合根</li>
 * <li>调用 Repository 持久化</li>
 * <li>第三方会话管理（Sa-Token）</li>
 * <li>DTO 转换</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    /**
     * 用户登录。
     * <p>
     * 流程：手机号查找用户 → 领域对象校验密码 → Sa-Token 登录 → 返回 Token。
     * </p>
     *
     * @param request 登录请求（手机号 + 密码）
     * @return Sa-Token 令牌字符串
     * @throws BizException 用户不存在或密码错误
     */
    public String login(LoginRequest request) {
        PhoneNumber phone = new PhoneNumber(request.getPhone());

        User user = userRepository.findByPhone(phone)
            .orElseThrow(() -> new BizException(ErrorCode.PARAM_ERROR, "用户不存在"));

        // 密码校验委托给领域对象，失败抛 InvalidCredentialsException
        try {
            user.authenticate(request.getPassword(), passwordEncoder);
        }
        catch (InvalidCredentialsException e) {
            throw new BizException(ErrorCode.PARAM_ERROR, "密码错误");
        }

        StpUtil.login(user.getId());
        return StpUtil.getTokenValue();
    }

    /**
     * 用户注册。
     * <p>
     * 流程：手机号唯一性检查（跨聚合不变量）→ 领域工厂创建用户 → 持久化。 密码加盐加密由 {@link User#register} 内部完成，本服务不关心加密细节。
     * </p>
     *
     * @param request 注册请求（手机号 + 密码 + 昵称）
     * @throws BizException 手机号已注册
     */
    public void register(RegisterRequest request) {
        PhoneNumber phone = new PhoneNumber(request.getPhone());
        Nickname nickname = new Nickname(request.getNickname());

        // 跨聚合不变量：手机号全局唯一（需查 Repository，聚合根内部无法独立判断）
        if (userRepository.findByPhone(phone).isPresent()) {
            throw new BizException(ErrorCode.PARAM_ERROR, "手机号已注册");
        }

        User user = User.register(phone, request.getPassword(), nickname, passwordEncoder);
        userRepository.save(user);
    }

    /**
     * 根据 ID 查询用户信息。
     *
     * @param id 用户 ID
     * @return 用户 DTO
     * @throws BizException 用户不存在
     */
    public UserDTO getById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND));
        return toDTO(user);
    }

    /**
     * 获取当前登录用户信息（通过 Sa-Token 解析当前会话）。
     *
     * @return 当前用户 DTO
     */
    public UserDTO getCurrentUser() {
        long userId = StpUtil.getLoginIdAsLong();
        return getById(userId);
    }

    // ==================== DTO 转换 ====================

    private UserDTO toDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setNickname(user.getNickname().value());
        dto.setPhone(user.getPhone().value());
        return dto;
    }
}
