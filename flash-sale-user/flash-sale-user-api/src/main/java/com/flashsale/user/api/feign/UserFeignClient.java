package com.flashsale.user.api.feign;

import com.flashsale.common.result.Result;
import com.flashsale.user.api.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "flash-sale-user", path = "/user")
public interface UserFeignClient {

    @GetMapping("/{id}")
    Result<UserDTO> getById(@PathVariable("id") Long id);

    @GetMapping("/token/{token}")
    Result<UserDTO> getByToken(@PathVariable("token") String token);
}
