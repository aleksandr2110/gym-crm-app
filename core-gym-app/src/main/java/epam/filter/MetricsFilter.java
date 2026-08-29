package epam.filter;

import epam.monitoring.metrics.RequestMetrics;
import io.micrometer.core.instrument.Timer;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@Order(2)
@RequiredArgsConstructor
public class MetricsFilter extends OncePerRequestFilter {

    private final RequestMetrics requestMetrics;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();
        if (path.startsWith("/actuator")) {
            filterChain.doFilter(request, response);
            return;
        }

        Timer.Sample sample = requestMetrics.startTimer();
        requestMetrics.incrementRequestCount();

        try {
            filterChain.doFilter(request, response);

            if (response.getStatus() >= 400) {
                requestMetrics.incrementErrorCount();
            }
        } catch (Exception e) {
            requestMetrics.incrementErrorCount();
            throw e;
        } finally {
            requestMetrics.stopTimer(sample);
        }
    }
}
