package com.wakutabi.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.wakutabi.domain.TripListDto;
import com.wakutabi.service.TripService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class MyTripsController {

	private final TripService tripService;
	
	@GetMapping("/mytrips") // 또는 @GetMapping("/schedule/myTrips")
	public String MyTrips(Principal principal, Model model) {
	  
	  // ⭐⭐ NullPointerException 방지: principal 객체가 null인지 먼저 확인 ⭐⭐
	  if (principal == null || principal.getName() == null) {
	      // 비로그인 상태이므로 로그인 페이지로 리다이렉트합니다.
	      // Spring Security 설정에 따라 /login으로 리다이렉트되도록 합니다.
	      return "redirect:/login"; 
	  }

	  
	  // 1. 현재 로그인된 사용자 ID를 가져옵니다.
	  String stringUsername = principal.getName(); // Spring Security는 String 반환
	  
	  // 2. Long 타입의 사용자 PK를 조회하는 로직을 사용
	  Long currentUserId = tripService.findUserIdByUsername(stringUsername); 
	  
	  if (currentUserId == null) {
	      // ... (로그 출력 로직) ...
	      return "redirect:/login"; // DB에서 사용자 ID를 찾을 수 없으면 로그인 페이지로
	  }
	  
	  // 3. 사용자가 등록한 여행 목록을 서비스 계층에서 조회합니다.
	  List<TripListDto> registeredTrips = tripService.getRegisteredTrips(currentUserId);
	
	  model.addAttribute("registeredTrips", registeredTrips);
	  
	  // 4. 사용자가 신청한 여행 목록도 필요하다면 여기서 추가합니다.
	  List<TripListDto> appliedTrips = tripService.getAppliedTrips(currentUserId);
	  model.addAttribute("appliedTrips", appliedTrips);
	  
	  // myTrips.html 템플릿 반환
	  return "travels/myTrips";
	}
}
