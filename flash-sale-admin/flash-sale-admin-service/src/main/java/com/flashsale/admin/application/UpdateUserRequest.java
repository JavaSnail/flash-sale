package com.flashsale.admin.application;

import lombok.Data;

@Data
public class UpdateUserRequest {
    private String password;
    private String realName;
    private String phone;
    private String email;
    private Integer status;
}
