package com.wakutabi.domain;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserWithdrawalDto {

    @NotBlank(message = "현재 비밀번호를 적어주세요")
    private String currentPassword;
    private String reason;
}
