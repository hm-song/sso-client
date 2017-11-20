package test.sso.client.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.SecurityConfig;
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

// https://spring.io/guides/tutorials/spring-boot-oauth2/#_social_login_manual
@Configuration
@EnableWebSecurity
@EnableOAuth2Client
public class SercurityConfig extends WebSecurityConfigurerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    private String clientId = "demo";
    private String clientSecret = "demo";
    private String accessTokenUri = "http://localhost:9999/auth/oauth/token";
    private String authorizationUri = "http://localhost:9999/auth/oauth/authorize";

    private String userInfoUri = "http://localhost:9999/auth/me";

    @Autowired
    OAuth2ClientContext oauth2ClientContext;

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
            .antMatcher("/**")
                .addFilterBefore(requestContextFilter(), BasicAuthenticationFilter.class)
                .addFilterBefore(oAuth2ClientContextFilter(), BasicAuthenticationFilter.class)
                .addFilterBefore(oauth2Filter(), BasicAuthenticationFilter.class)
            .authorizeRequests()
                .antMatchers("/", "/login**").permitAll()
                .anyRequest().authenticated()
                .and()
            .exceptionHandling()
                .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/"));
    }

    @Bean
    public RequestContextFilter requestContextFilter() {
        return new RequestContextFilter();
    }

    @Bean
    public OAuth2ClientContextFilter oAuth2ClientContextFilter() {
        return new OAuth2ClientContextFilter();
    }

    @Bean
    public OAuth2ClientAuthenticationProcessingFilter oauth2Filter() {
        OAuth2RestTemplate restTemplate = new OAuth2RestTemplate(resourceDetails(),oauth2ClientContext);

        UserInfoTokenServices tokenServices = new UserInfoTokenServices(userInfoUri, clientId);
        tokenServices.setRestTemplate(restTemplate);

        OAuth2ClientAuthenticationProcessingFilter filter = new OAuth2ClientAuthenticationProcessingFilter("/");
        filter.setRestTemplate(restTemplate);
        filter.setTokenServices(tokenServices);
        filter.setAuthenticationSuccessHandler(new SimpleUrlAuthenticationSuccessHandler("/hello"));
        return filter;
    }

    @Bean
    public AuthorizationCodeResourceDetails resourceDetails() {
        AuthorizationCodeResourceDetails rd = new AuthorizationCodeResourceDetails();
        rd.setClientId(clientId);
        rd.setClientSecret(clientSecret);
        rd.setAccessTokenUri(accessTokenUri);
        rd.setUserAuthorizationUri(authorizationUri);
        rd.setTokenName("oauth_token");
        rd.setAuthenticationScheme(AuthenticationScheme.query);
        rd.setClientAuthenticationScheme(AuthenticationScheme.form);
        return rd;
    }
}
