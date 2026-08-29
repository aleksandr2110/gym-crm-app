package epam.filter;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
@Order(1)
public class TransactionIdFilter implements Filter {

    private static final String TRANSACTION_ID = "transactionId";
    private static final String TRANSACTION_ID_HEADER = "X-Transaction-Id";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        String transactionId = httpRequest.getHeader(TRANSACTION_ID_HEADER);
        if (transactionId == null || transactionId.isEmpty()) {
            transactionId = UUID.randomUUID().toString();
        }

        MDC.put(TRANSACTION_ID, transactionId);
        log.info("Starting transaction: {} for request: {} {}",
                transactionId, httpRequest.getMethod(), httpRequest.getRequestURI());

        try {
            chain.doFilter(request, response);
        } finally {
            log.info("Completed transaction: {}", transactionId);
            MDC.remove(TRANSACTION_ID);
        }
    }
}
