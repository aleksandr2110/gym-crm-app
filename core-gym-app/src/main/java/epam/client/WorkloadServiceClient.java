package epam.client;

import epam.domain.dto.request.WorkloadRequest;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.amqp.AmqpException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Component
@RequiredArgsConstructor
public class WorkloadServiceClient {

    @Autowired
    private JmsTemplate jmsTemplate;

    private final String QUEUE_NAME = "workload.queue";

    public void sendWorkloadUpdate(WorkloadRequest workloadRequest) {
        try {
            log.info("Sending workload message to ActiveMQ. Action: {}, Trainer: {}",
                    workloadRequest.getActionType(), workloadRequest.getUsername());

            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String authHeader = request.getHeader("Authorization");

                String jwtToken = authHeader.substring(7);
                log.info("token to pass {}", jwtToken);

            String transactionId = MDC.get("transactionId");

            jmsTemplate.convertAndSend(QUEUE_NAME, workloadRequest,
                    message -> {
                        if (transactionId != null) {
                            message.setStringProperty("Authorization", "Bearer " + jwtToken);
                            message.setStringProperty("transactionId", transactionId);
                        }
                        return message;
                    });
            }

            log.info("Workload message sent successfully for trainer: {}", workloadRequest.getUsername());
        } catch (AmqpException ex) {
            log.error("Failed to send workload message to ActiveMQ. Action: {}, Trainer: {}. Error: {}",
                    workloadRequest.getActionType(), workloadRequest.getUsername(), ex.getMessage());
        }
    }


}
