package yachosan.infra.model.password;

import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.annotation.AbstractNamedValueMethodArgumentResolver;
import yachosan.domain.model.Password;

import javax.servlet.ServletException;
import java.util.Arrays;

public class PasswordMethodArgumentResolver extends AbstractNamedValueMethodArgumentResolver {
    @Override
    protected NamedValueInfo createNamedValueInfo(MethodParameter parameter) {
        return new NamedValueInfo("dummy", false, null);
    }

    @Override
    protected Object resolveName(String name, MethodParameter parameter, NativeWebRequest request) throws Exception {
        String authorization = request.getHeader("Authorization");
        if (authorization != null && authorization.startsWith("Bearer ")) {
            char[] password = Arrays.copyOfRange(authorization.toCharArray(), "Bearer ".length(), authorization.length());
            return Password.of(password);
        }
        return null;
    }

    @Override
    protected void handleMissingValue(String name, MethodParameter parameter) throws ServletException {

    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return Password.class.isAssignableFrom(parameter.getParameterType());
    }
}
