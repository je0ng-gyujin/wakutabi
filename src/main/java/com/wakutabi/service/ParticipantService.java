package com.wakutabi.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.wakutabi.domain.ParticipantDto;
import com.wakutabi.mapper.ParticipantMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ParticipantService {
	
	private final ParticipantMapper participantmapper;
	
	public List<ParticipantDto> participant(Long tripArticleId){
		List<ParticipantDto> participant = participantmapper.findParticipantsWithHost(tripArticleId);
		return participant;
	}
}
