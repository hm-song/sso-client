package test.sso.client.filter;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by hm.song on 2017. 12. 5..
 */
public class CustomSimpleAuthFailHandler extends SimpleUrlAuthenticationFailureHandler{

    private static final String REDIRECT_PATH_FROM_INVALID_SCOPE = "/denied";

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        String cause = request.getParameter("error");
        if (cause != null) {
            getRedirectStrategy().sendRedirect(request, response, REDIRECT_PATH_FROM_INVALID_SCOPE);
        } else {
            super.onAuthenticationFailure(request, response, exception);
        }
    }
}
