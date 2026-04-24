package com.flashsale.user.api.dto;

import lombok.Data;
import java.io.Serializable;

@Data
public class UserDTO implements Serializable {
    private Long id;
    private String nickname;
    private String phone;
}
