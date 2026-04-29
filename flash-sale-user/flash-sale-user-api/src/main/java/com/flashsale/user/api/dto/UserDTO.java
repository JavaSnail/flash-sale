package com.flashsale.user.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;

@Schema(description = "用户信息")
@Data
public class UserDTO implements Serializable {
    @Schema(description = "用户ID", example = "1")
    private Long id;

    @Schema(description = "昵称", example = "用户A")
    private String nickname;

    @Schema(description = "手机号", example = "13800138000")
    private String phone;
}
