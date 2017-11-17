package test.sso.client.oauth;

import java.util.Map;

/**
 * Created by hm.song on 2017. 11. 17..
 */
public interface PrincipalExtractor {

    /**
     * Extract the principal that should be used for the token.
     * @param map the source map
     * @return the extracted principal or {@code null}
     */
    Object extractPrincipal(Map<String, Object> map);

}
