package com.flashsale.user.infrastructure;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.flashsale.user.domain.EncryptedPassword;
import com.flashsale.user.domain.Nickname;
import com.flashsale.user.domain.PhoneNumber;
import com.flashsale.user.domain.User;
import com.flashsale.user.domain.UserRepository;
import com.flashsale.user.infrastructure.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 用户仓储 MyBatis 实现。
 *
 * <p>负责领域对象 {@link User} 与数据对象 {@link UserDO} 之间的双向转换。
 * 使用 {@link User#reconstitute} 重建领域对象（无 setter），
 * 使用 {@link User#withId} 注入数据库自增 ID。</p>
 */
@Repository
@RequiredArgsConstructor
public class MyBatisUserRepository implements UserRepository {

    private final UserMapper userMapper;

    @Override
    public Optional<User> findById(Long id) {
        UserDO userDO = userMapper.selectById(id);
        return Optional.ofNullable(userDO).map(this::toDomain);
    }

    @Override
    public Optional<User> findByPhone(PhoneNumber phone) {
        UserDO userDO = userMapper.selectOne(
                new LambdaQueryWrapper<UserDO>().eq(UserDO::getPhone, phone.value()));
        return Optional.ofNullable(userDO).map(this::toDomain);
    }

    @Override
    public User save(User user) {
        UserDO userDO = toDO(user);
        if (user.getId() == null) {
            userMapper.insert(userDO);
            return user.withId(userDO.getId());
        } else {
            userMapper.updateById(userDO);
            return user;
        }
    }

    // ==================== DO ↔ Domain 转换 ====================

    /**
     * 数据对象 → 领域对象。通过 reconstitute 重建，绕过业务校验。
     */
    private User toDomain(UserDO userDO) {
        return User.reconstitute(
                userDO.getId(),
                new PhoneNumber(userDO.getPhone()),
                new Nickname(userDO.getNickname()),
                EncryptedPassword.of(userDO.getPassword(), userDO.getSalt()),
                userDO.getCreateTime(),
                userDO.getUpdateTime()
        );
    }

    /**
     * 领域对象 → 数据对象。提取值对象内部值映射到扁平字段。
     */
    private UserDO toDO(User user) {
        UserDO userDO = new UserDO();
        userDO.setId(user.getId());
        userDO.setPhone(user.getPhone().value());
        userDO.setNickname(user.getNickname().value());
        userDO.setPassword(user.getPassword().hash());
        userDO.setSalt(user.getPassword().salt());
        return userDO;
    }
}
