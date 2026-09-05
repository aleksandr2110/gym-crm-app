package epam.client;

import epam.config.RabbitMQConfig;
import epam.domain.dto.request.WorkloadRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class WorkloadServiceClient {

    private final RabbitTemplate rabbitTemplate;

    public void sendWorkloadUpdate(WorkloadRequest request) {
        try {
            log.info("Sending workload message to RabbitMQ. Action: {}, Trainer: {}",
                    request.getActionType(), request.getUsername());

            String transactionId = MDC.get("transactionId");

            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.WORKLOAD_EXCHANGE,
                    RabbitMQConfig.WORKLOAD_ROUTING_KEY,
                    request,
                    message -> {
                        if (transactionId != null) {
                            message.getMessageProperties().setHeader("transactionId", transactionId);
                        }
                        return message;
                    }
            );

            log.info("Workload message sent successfully for trainer: {}", request.getUsername());
        } catch (AmqpException ex) {
            log.error("Failed to send workload message to RabbitMQ. Action: {}, Trainer: {}. Error: {}",
                    request.getActionType(), request.getUsername(), ex.getMessage());
        }
    }
}
