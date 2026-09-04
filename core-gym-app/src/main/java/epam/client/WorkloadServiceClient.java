package epam.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import epam.domain.dto.request.WorkloadRequest;
import jakarta.jms.TextMessage;
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
            ObjectMapper mapper = new ObjectMapper();
            log.info("before writing " + workloadRequest.toString());

            //String jsonObj = mapper.writeValueAsString(workload);
            String jsonObj = mapper.writer().withDefaultPrettyPrinter().writeValueAsString(workloadRequest);
            log.info("after writing jsonObj: " + jsonObj);
            //String json = "{\"username\":\"Ricci.Deep7\", \"firstName\":\"Ricci\", \"lastName\":\"Deep7\", \"isActive\":true, \"trainingDate\":\"2026-06-12T18:30\", \"trainingDuration\":60, \"actionType\":\"add\"}";

            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String authHeader = request.getHeader("Authorization");

                String jwtToken = authHeader.substring(7);
                log.info("token to pass {}", jwtToken);

            String transactionId = MDC.get("transactionId");
            log.info("in the block");

                jmsTemplate.send(QUEUE_NAME, messageCreator -> {
                    TextMessage message = messageCreator.createTextMessage();
                    message.setText(jsonObj);
                    //message.setText(json);
                    return message;
                });

            }
            /*jmsTemplate.convertAndSend(QUEUE_NAME, workloadRequest,
                    message -> {
                        if (transactionId != null) {
                            message.setStringProperty("Authorization", "Bearer " + jwtToken);
                            message.setStringProperty("transactionId", transactionId);
                        }
                        return message;
                    });
            } */

            log.info("Workload message sent successfully for trainer: {}", workloadRequest.getUsername());
        } catch (AmqpException ex ) {
            log.error("Failed to send workload message to ActiveMQ. Action: {}, Trainer: {}. Error: {}",
                    workloadRequest.getActionType(), workloadRequest.getUsername(), ex.getMessage());
        } catch (JsonProcessingException js) {
            log.error("Failed to serialize object to JSON");
        }
        //}
    }

}
