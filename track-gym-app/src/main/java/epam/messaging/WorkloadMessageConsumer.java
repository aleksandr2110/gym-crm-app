package epam.messaging;

import epam.controller.exception.UnauthorizedException;
import epam.domain.dto.WorkloadRequest;
import epam.security.JwtUtil;
import epam.service.WorkloadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.messaging.handler.annotation.Header;

import java.util.ArrayList;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class WorkloadMessageConsumer {

    private final WorkloadService workloadService;
    private final JwtUtil jwtUtil;

    @JmsListener(destination = "workload.queue")
    public void receiveWorkloadMessage(WorkloadRequest request, @Header("Authorization") String token,
                                       @Header(name = "transactionId", required = false) String transactionId) {

        log.info("Receiving data via workload.queue");
        if (token == null || !token.startsWith("Bearer ")) {
            log.warn("No Bearer token found for destination queue: workload.queue");
            throw new UnauthorizedException("Trainer is not authenticated");
        }

        String jwt = token.substring(7);
        String username = jwtUtil.extractUsername(jwt);
        log.info("Extracted username: {}", username);
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            log.info("Validating JWT token for user: {}", username);
            if (jwtUtil.validateToken(jwt)) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(username, null, new ArrayList<>());
                //authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
                log.info("JWT authentication successful for user: {}", username);
                throw new UnauthorizedException("Trainer is not authenticated");
            } else {
                log.error("JWT token validation failed for user: {}", username);
            }
        }

        if (transactionId == null || transactionId.isBlank()) {
            transactionId = UUID.randomUUID().toString();
        }
        MDC.put("transactionId", transactionId);

        try {
            log.info("Received workload message from ActiveMQ. Action: {}, Trainer: {}",
                    request.getActionType(), request.getUsername());

            validateRequest(request);
            workloadService.processWorkload(request);

            log.info("Workload message processed successfully for trainer: {}", request.getUsername());
        } catch (IllegalArgumentException ex) {
            log.error("Invalid workload message rejected. Reason: {}. Trainer: {}",
                    ex.getMessage(), request.getUsername());
            throw ex;
        } catch (Exception ex) {
            log.error("Failed to process workload message for trainer: {}. Error: {}",
                    request.getUsername(), ex.getMessage());
            throw ex;
        } finally {
            MDC.remove("transactionId");
        }
    }

    private void validateRequest(WorkloadRequest request) {
        if (request.getUsername() == null || request.getUsername().isBlank()) {
            throw new IllegalArgumentException("Username is required");
        }
        if (request.getFirstName() == null || request.getFirstName().isBlank()) {
            throw new IllegalArgumentException("First name is required");
        }
        if (request.getLastName() == null || request.getLastName().isBlank()) {
            throw new IllegalArgumentException("Last name is required");
        }
        if (request.getTrainingDate() == null) {
            throw new IllegalArgumentException("Training date is required");
        }
        if (request.getTrainingDuration() == null || request.getTrainingDuration() <= 0) {
            throw new IllegalArgumentException("Training duration must be positive");
        }
        if (request.getActionType() == null) {
            throw new IllegalArgumentException("Action type is required");
        }
    }
}
