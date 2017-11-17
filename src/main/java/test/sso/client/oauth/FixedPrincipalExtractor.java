package test.sso.client.oauth;

import java.util.Map;

/**
 * Created by hm.song on 2017. 11. 17..
 */
public class FixedPrincipalExtractor implements PrincipalExtractor {

    private static final String[] PRINCIPAL_KEYS = new String[] { "user", "username",
            "userid", "user_id", "login", "id", "name" };

    @Override
    public Object extractPrincipal(Map<String, Object> map) {
        for (String key : PRINCIPAL_KEYS) {
            if (map.containsKey(key)) {
                return map.get(key);
            }
        }
        return null;
    }

}
