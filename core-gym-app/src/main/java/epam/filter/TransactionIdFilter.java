package epam.filter;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
@Order(1)
public class TransactionIdFilter implements Filter {

    @Value("${spring.application.name}")
    private String serviceName;

    //private static final String TRANSACTION_ID = "transactionId";
    //private static final String TRANSACTION_ID_HEADER = "X-Transaction-Id";
    public static final String CORRELATION_ID_HEADER = "X-Correlation-Id";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        String correlationId = null;
        String transactionId = httpRequest.getHeader(CORRELATION_ID_HEADER);
        if (transactionId == null || transactionId.isEmpty()) {
            correlationId = serviceName + "_" + UUID.randomUUID().toString();
            //transactionId = UUID.randomUUID().toString();
        }

        MDC.put(CORRELATION_ID_HEADER, correlationId);

        log.info("Starting transaction: {} for request: {} {}",
                correlationId, httpRequest.getMethod(), httpRequest.getRequestURI());

        try {
            chain.doFilter(request, response);
        } finally {
            log.info("Completed transaction: {}", correlationId);
            MDC.remove(CORRELATION_ID_HEADER);
        }
    }
}
