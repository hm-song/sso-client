package test.sso.client.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.common.AuthenticationScheme;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.filter.RequestContextFilter;
import test.sso.client.oauth.UserInfoTokenServices;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableOAuth2Client
public class SercurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * 클라이언트를 인증 서버로부터 필요한 ID. 서버와 동일한 값이어야 한다
     */
    private String clientId = "demo";

    /**
     * 클라이언트를 인증 서버로부터 필요한 비밀 키. 서버와 동일한 값이어야 한다
     */
    private String clientSecret = "demo";

    /**
     * Authorization grant를 위한 인증서버 URI
     */
    private String authorizationUri = "http://localhost:9999/auth/oauth/authorize";

    /**
     * 토큰을 발급받기 위한 인증서버 URI
     */
    private String accessTokenUri = "http://localhost:9999/auth/oauth/token";

    /**
     * 토큰으로 사용자 정보 획득을 위한 URI
     */
    private String userInfoEndpointUrl = "http://localhost:9999/auth/me";

    /**
     * 로컬에서 OAuth 인증을 처리할 URL
     */
    private String localOauth2Entrypoint = "/auth";

    /**
     * 인증서버로 보낼 사용자 인증정보 전달 방식. 기본은 header에 담는다.
     */
    private AuthenticationScheme authenticationScheme = AuthenticationScheme.header;

    /**
     * 인증서버로 보낼 클라이언트 인증정보 전달 방식. 기본은 header에 담는다.
     */
    private AuthenticationScheme clientAuthenticationScheme = AuthenticationScheme.header;

    @Autowired
    OAuth2ClientContext oauth2ClientContext;

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
            .antMatcher("/**")
                // OAuth2 처리를 위한 필터들을 등록
                .addFilterBefore(requestContextFilter(), BasicAuthenticationFilter.class)
                .addFilterBefore(oAuth2ClientContextFilter(), BasicAuthenticationFilter.class)
                .addFilterBefore(oauth2Filter(), BasicAuthenticationFilter.class)
            .authorizeRequests()
                .anyRequest().authenticated()
                .and()

            // 인증되지 않은 요청을 인증 필터로 redirect. '/auth' 에 OAuth 인증 필터가 적용된다.
            .exceptionHandling()
                .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint(localOauth2Entrypoint));
    }


    /**
     * 인증 컨텍스트를 저장하기 위한 필터
     */
    @Bean
    public RequestContextFilter requestContextFilter() {
        return new RequestContextFilter();
    }

    /**
     * OAuth 인증 시나리오에서 필요한 Redirect 처리 지원
     */
    @Bean
    public OAuth2ClientContextFilter oAuth2ClientContextFilter() {
        return new OAuth2ClientContextFilter();
    }

    /**
     * 인증 서버로부터 인증 및 토큰 발급을 수행
     */
    @Bean
    public OAuth2ClientAuthenticationProcessingFilter oauth2Filter() {
        OAuth2RestTemplate restTemplate = oauth2RestTemplate();

        UserInfoTokenServices tokenServices = userInfoTokenServices();
        tokenServices.setRestTemplate(restTemplate);

        OAuth2ClientAuthenticationProcessingFilter filter = new OAuth2ClientAuthenticationProcessingFilter(localOauth2Entrypoint);
        filter.setRestTemplate(restTemplate);
        filter.setTokenServices(tokenServices);

        // OAuth 인증이 성공할 경우 리다이렉트 할 URI
        filter.setAuthenticationSuccessHandler(new SimpleUrlAuthenticationSuccessHandler("/hello"));
        return filter;
    }

    /**
     * 인증 서버와 통신할 RestTemplate.
     * 내부에서 AuthorizationCodeAccessTokenProvider를 이용해 인증 코드 및 토큰을 획득한다.
     */
    @Bean
    public OAuth2RestTemplate oauth2RestTemplate() {
        return new OAuth2RestTemplate(resourceDetails(), oauth2ClientContext);
    }

    /**
     * 인증 서버로부터 토큰을 이용해 사용자 인증 수행
     * 인증 서버의 userInfoEndpointUrl에서 Principal 정보를 전달 받는다.
     */
    @Bean
    public UserInfoTokenServices userInfoTokenServices() {
        return new UserInfoTokenServices(userInfoEndpointUrl, clientId);
    }

    /**
     * OAuth 클라이언트 설정 컨테이너
     */
    @Bean
    public AuthorizationCodeResourceDetails resourceDetails() {
        AuthorizationCodeResourceDetails rd = new AuthorizationCodeResourceDetails();
        rd.setClientId(clientId);
        rd.setClientSecret(clientSecret);
        rd.setAccessTokenUri(accessTokenUri);
        rd.setUserAuthorizationUri(authorizationUri);
        rd.setScope(Arrays.asList("read"));
        rd.setAuthenticationScheme(authenticationScheme);
        rd.setClientAuthenticationScheme(clientAuthenticationScheme);
        return rd;
    }
}
