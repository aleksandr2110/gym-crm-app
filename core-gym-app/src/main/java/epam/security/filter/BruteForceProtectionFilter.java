package epam.security.filter;
import epam.domain.dto.request.LoginRequest;
import epam.security.service.LoginAttemptService;
import org.springframework.web.filter.OncePerRequestFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;


import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class BruteForceProtectionFilter extends OncePerRequestFilter {

    private final LoginAttemptService loginAttemptService;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        if ("/api/auth/login".equals(request.getRequestURI()) && "POST".equals(request.getMethod())) {
            byte[] body = request.getInputStream().readAllBytes();
            CachedBodyHttpServletRequest cachedRequest = new CachedBodyHttpServletRequest(request, body);

            try {
                LoginRequest loginRequest = objectMapper.readValue(body, LoginRequest.class);
                String username = loginRequest.getUsername();

                if (username != null && loginAttemptService.isBlocked(username)) {
                    log.warn("Blocking login attempt for blocked user: {}", username);
                    sendBlockedResponse(response, username);
                    return;
                }
            } catch (Exception e) {
            }

            filterChain.doFilter(cachedRequest, response);
        } else {
            filterChain.doFilter(request, response);
        }
    }

    private void sendBlockedResponse(HttpServletResponse response, String username) throws IOException {
        long remainingTime = loginAttemptService.getRemainingBlockTimeInSeconds(username);
        long minutes = remainingTime / 60;
        long seconds = remainingTime % 60;

        response.setStatus(HttpStatus.LOCKED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        Map<String, String> error = new HashMap<>();
        error.put("error", String.format(
                "Account is locked according to too many failed login attempts. Try again in %d minute(s) & %d second(s).",
                minutes, seconds
        ));

        objectMapper.writeValue(response.getWriter(), error);
    }

    // https://www.baeldung.com/spring-reading-httpservletrequest-multiple-times
    private static class CachedBodyHttpServletRequest extends HttpServletRequestWrapper {
        private final byte[] cachedBody;

        public CachedBodyHttpServletRequest(HttpServletRequest request, byte[] cachedBody) {
            super(request);
            this.cachedBody = cachedBody;
        }

        @Override
        public ServletInputStream getInputStream() {
            return new CachedBodyServletInputStream(this.cachedBody);
        }

        @Override
        public BufferedReader getReader() {
            return new BufferedReader(new InputStreamReader(getInputStream()));
        }
    }

    private static class CachedBodyServletInputStream extends ServletInputStream {
        private final ByteArrayInputStream buffer;

        public CachedBodyServletInputStream(byte[] cachedBody) {
            this.buffer = new ByteArrayInputStream(cachedBody);
        }

        @Override
        public int read() {
            return buffer.read();
        }

        @Override
        public boolean isFinished() {
            return buffer.available() == 0;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener listener) {
        }
    }
}
