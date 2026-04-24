package com.flashsale.user.domain;

import java.util.Optional;

/**
 * 用户仓储接口（领域层定义）。
 * <p>
 * 遵循依赖倒置原则（DIP），接口定义在 domain 层， 具体实现（如 MyBatis）在 infrastructure 层。
 * </p>
 */
public interface UserRepository {

    /**
     * 根据 ID 查找用户。
     *
     * @param id 用户 ID
     * @return 用户聚合根，不存在返回 empty
     */
    Optional<User> findById(Long id);

    /**
     * 根据手机号查找用户。
     * <p>
     * 用于注册时的唯一性检查和登录时的用户查找。
     * </p>
     *
     * @param phone 手机号值对象
     * @return 用户聚合根，不存在返回 empty
     */
    Optional<User> findByPhone(PhoneNumber phone);

    /**
     * 持久化用户。
     * <p>
     * 新建时（id = null）执行 INSERT 并返回带数据库生成 ID 的新实例； 更新时执行 UPDATE 并返回原实例。
     * </p>
     *
     * @param user 用户聚合根
     * @return 持久化后的用户（新建时 ID 已填充）
     */
    User save(User user);
}
