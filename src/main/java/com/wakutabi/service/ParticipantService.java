package com.wakutabi.service;

import com.wakutabi.domain.ParticipantDto;
import com.wakutabi.mapper.ParticipantMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ParticipantService {

    private final ParticipantMapper participantMapper;

    public List<ParticipantDto> getParticipants(Long tripArticleId) {
        return participantMapper.findParticipantsByTripId(tripArticleId);
    }
}