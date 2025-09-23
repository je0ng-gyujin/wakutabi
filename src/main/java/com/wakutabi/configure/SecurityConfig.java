package com.wakutabi.configure;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		// CSRF 보호 기능을 비활성화합니다.
		// CSRF는 웹사이트의 보안 취약점 중 하나로, 주로 폼(form)을 사용하는 요청에 대한 보호를 담당합니다.
		// API 서버를 구축할 때는 보통 비활성화합니다.
		http.csrf(csrf -> csrf.disable())

				// HTTP 요청에 대한 접근 규칙을 설정합니다.
				.authorizeHttpRequests(auth -> auth
						// "/schedule/create" 경로에 대한 요청은 인증된 사용자만 접근할 수 있습니다.
						.requestMatchers("/schedule/create").authenticated()
						// "/adm/"으로 시작하는 모든 요청은 "ADMIN" 역할을 가진 사용자만 접근할 수 있습니다.
						.requestMatchers("/adm/**").hasRole("ADMIN")
						// 그 외 모든 요청은 허용합니다. (인증 없이 접근 가능)
						.anyRequest().permitAll())

				// 폼 기반 로그인 설정을 시작합니다.
				.formLogin(login -> login
						// 로그인 페이지의 URL을 "/user/login"으로 지정합니다.
						.loginPage("/user/login")
						// 로그인 처리를 수행할 URL을 "/login"으로 지정합니다.
						.loginProcessingUrl("/login")
						// 로그인 성공 시 실행될 핸들러를 지정합니다.
						.successHandler(successHandler())
						// 로그인 실패 시 이동할 URL을 "/user/login?error=true"로 지정합니다.
						.failureUrl("/user/login?error=true")
						// 로그인 관련 페이지는 모두에게 허용합니다.
						.permitAll())

				// 로그아웃 설정을 시작합니다.
				.logout(logout -> logout
						// 로그아웃을 처리할 URL을 "/logout"으로 지정합니다.
						.logoutUrl("/logout")
						// 로그아웃 성공 시 이동할 URL을 "/" (루트 페이지)로 지정합니다.
						.logoutSuccessUrl("/")
						// 로그아웃 관련 페이지는 모두에게 허용합니다.
						.permitAll())

				// 인증/인가 예외 처리 설정을 시작합니다.
				.exceptionHandling(ex -> ex
						// 인증되지 않은 사용자가 보호된 리소스에 접근할 때의 동작을 정의합니다.
						.authenticationEntryPoint((request, response, authException) -> {
							// HTTP 요청 헤더에 "X-requested-With"가 "XMLHttpRequest"인지 확인합니다.
							// 이는 AJAX 요청인지 아닌지를 구분하는 일반적인 방법입니다.
							if ("XMLHttpRequest".equals(request.getHeader("X-requested-With"))) {
								// AJAX 요청이라면, 인증되지 않았다는 HTTP 401 에러를 반환합니다.
								response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
							} else {
								// 일반적인 웹 요청이라면, 로그인 페이지로 리다이렉트합니다.
								response.sendRedirect("/user/login?required=true");
							}
						}))

				// HTTP 헤더 보안 설정을 시작합니다.
				.headers(headers -> headers
						// X-Frame-Options 헤더를 비활성화합니다.
						// 다른 도메인의 웹사이트에서 iframe을 사용하여 페이지를 삽입하는 것을 허용합니다.
						.frameOptions(frame -> frame.disable()));

		// 모든 설정을 적용하여 SecurityFilterChain 객체를 반환합니다.
		return http.build();
	}

	@Bean
	public AuthenticationSuccessHandler successHandler() {
		// 람다식을 사용해 AuthenticationSuccessHandler 인터페이스를 구현합니다.
		return (request, response, authentication) -> { // 로그인 성공 후 실행되는 코드 블록
			// 사용자가 로그인하기 전에 접근하려고 했던 URL 정보를 세션에서 가져옵니다.
			SavedRequest savedRequest = new HttpSessionRequestCache().getRequest(request, response);
			// 현재 로그인한 사용자의 권한(Authorities) 목록을 가져옵니다.
			// stream()을 사용해 각 권한을 확인합니다.
			boolean isAdmin = authentication.getAuthorities().stream()
					// 권한 중 "ROLE_ADMIN"이 포함되어 있는지 확인합니다.
					.anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

			// 만약 관리자(isAdmin)라면
			if (isAdmin) {
				// "adm/admin_index" 페이지로 리다이렉트(redirect) 시킵니다.
				response.sendRedirect("adm/admin_index");
			}
			// 관리자가 아니라면
			else { // 일반 사용자 가 로그인하기 전에 접근하려고 했던 URL이 있는지 확인합니다
				if (savedRequest != null) { // 저장된 요청이 있다면 그 요청의 URL을 가져옵니다.
					String targetUrl = savedRequest.getRedirectUrl();
					// 저장된 요청이 있다면, 그 URL로 리다이렉트(redirect) 시킵니다.
					response.sendRedirect(targetUrl);
					return;
					
				}
				// 메인 페이지("/")로 리다이렉트(redirect) 시킵니다.
				response.sendRedirect("/");
			}
		};
	}
}
