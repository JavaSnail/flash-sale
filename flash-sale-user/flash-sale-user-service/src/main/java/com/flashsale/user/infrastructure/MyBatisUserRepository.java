package com.flashsale.user.infrastructure;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.flashsale.user.domain.User;
import com.flashsale.user.domain.UserRepository;
import com.flashsale.user.infrastructure.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

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
    public Optional<User> findByPhone(String phone) {
        UserDO userDO = userMapper.selectOne(
                new LambdaQueryWrapper<UserDO>().eq(UserDO::getPhone, phone));
        return Optional.ofNullable(userDO).map(this::toDomain);
    }

    @Override
    public void save(User user) {
        UserDO userDO = toDO(user);
        if (user.getId() == null) {
            userMapper.insert(userDO);
            user.setId(userDO.getId());
        } else {
            userMapper.updateById(userDO);
        }
    }

    private User toDomain(UserDO userDO) {
        User user = new User();
        user.setId(userDO.getId());
        user.setPhone(userDO.getPhone());
        user.setNickname(userDO.getNickname());
        user.setPassword(userDO.getPassword());
        user.setSalt(userDO.getSalt());
        user.setCreateTime(userDO.getCreateTime());
        user.setUpdateTime(userDO.getUpdateTime());
        return user;
    }

    private UserDO toDO(User user) {
        UserDO userDO = new UserDO();
        userDO.setId(user.getId());
        userDO.setPhone(user.getPhone());
        userDO.setNickname(user.getNickname());
        userDO.setPassword(user.getPassword());
        userDO.setSalt(user.getSalt());
        return userDO;
    }
}
