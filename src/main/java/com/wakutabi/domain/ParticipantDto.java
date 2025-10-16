package com.wakutabi.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ParticipantDto {
    private Long userId;
    private String nickname;
    private String imagePath;
    private String role; // 'HOST' 또는 'PARTICIPANT'
}