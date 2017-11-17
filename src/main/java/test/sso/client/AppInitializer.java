package test.sso.client;

import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;
import test.sso.client.config.AppConfig;
import test.sso.client.config.SercurityConfig;

public class AppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    @Override
    protected String[] getServletMappings() {
        return new String[]{"/"};
    }

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class[]{AppConfig.class, SercurityConfig.class};
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class[]{};
    }
}