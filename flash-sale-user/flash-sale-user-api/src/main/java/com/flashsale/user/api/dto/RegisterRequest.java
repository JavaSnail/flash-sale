package com.flashsale.user.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Schema(description = "注册请求")
@Data
public class RegisterRequest {
    @Schema(description = "手机号", example = "13800138000")
    @NotBlank
    private String phone;

    @Schema(description = "密码", example = "123456")
    @NotBlank
    private String password;

    @Schema(description = "昵称", example = "用户A")
    private String nickname;
}
