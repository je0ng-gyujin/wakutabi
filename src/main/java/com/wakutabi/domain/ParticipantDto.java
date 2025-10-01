package com.wakutabi.domain;

import lombok.Data;

@Data
public class ParticipantDto {
    private Long userId;
    private String nickname;
    private String imagePath;
    private String role; // HOST / MEMBER
}
