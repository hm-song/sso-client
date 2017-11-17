package test.sso.client.oauth;

import org.springframework.security.core.GrantedAuthority;

import java.util.List;
import java.util.Map;

/**
 * Created by hm.song on 2017. 11. 17..
 */
public interface AuthoritiesExtractor {

    /**
     * Extract the authorities from the resource server's response.
     * @param map the response
     * @return the extracted authorities
     */
    List<GrantedAuthority> extractAuthorities(Map<String, Object> map);

}
