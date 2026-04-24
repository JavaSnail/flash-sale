package com.flashsale.user.domain;

import java.time.LocalDateTime;

public class User {
    private Long id;
    private String phone;
    private String nickname;
    private String password;
    private String salt;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public User() {}

    public User(String phone, String nickname, String password, String salt) {
        this.phone = phone;
        this.nickname = nickname;
        this.password = password;
        this.salt = salt;
        this.createTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
    }

    public boolean checkPassword(String inputPassword, PasswordEncoder encoder) {
        return encoder.matches(inputPassword, this.password, this.salt);
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getSalt() { return salt; }
    public void setSalt(String salt) { this.salt = salt; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}
