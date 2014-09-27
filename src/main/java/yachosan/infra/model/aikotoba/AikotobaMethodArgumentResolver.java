package yachosan.infra.model.aikotoba;

import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.annotation.AbstractNamedValueMethodArgumentResolver;
import yachosan.domain.model.Aikotoba;

import javax.servlet.ServletException;

public class AikotobaMethodArgumentResolver extends AbstractNamedValueMethodArgumentResolver {
    @Override
    protected NamedValueInfo createNamedValueInfo(MethodParameter parameter) {
        return new NamedValueInfo("dummy", false, null);
    }

    @Override
    protected Object resolveName(String name, MethodParameter parameter, NativeWebRequest request) throws Exception {
        String authorization = request.getHeader("Authorization");
        if (authorization != null && authorization.startsWith("Bearer ")) {
            String aikotoba = authorization.substring("Bearer ".length(), authorization.length());
            return new Aikotoba(aikotoba);
        }
        return null;
    }

    @Override
    protected void handleMissingValue(String name, MethodParameter parameter) throws ServletException {

    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return Aikotoba.class.isAssignableFrom(parameter.getParameterType());
    }
}
