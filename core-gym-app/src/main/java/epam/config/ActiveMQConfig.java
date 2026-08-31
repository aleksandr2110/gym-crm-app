package epam.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import epam.domain.dto.request.WorkloadRequest;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.core.JmsTemplate;
//import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

import org.springframework.jms.support.converter.MappingJackson2MessageConverter;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableJms
public class ActiveMQConfig {

    private static final String BROKER_URL = "tcp://localhost:61616";

    @Bean
    public MappingJackson2MessageConverter jacksonJmsMessageConverter() {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT); // Converts to JSON string
        converter.setTypeIdPropertyName("_type");   // Pairs type info with the message
        // Optional but recommended: Explicitly map type IDs to clean up headers and decouple package names
        Map<String, Class<?>> typeIdMappings = new HashMap<>();
        typeIdMappings.put("workloadQueue", epam.domain.dto.request.WorkloadRequest.class);
        converter.setTypeIdMappings(typeIdMappings);
        return converter;
    }
    /*@Bean
    public MessageConverter jacksonJmsMessageConverter(ObjectMapper objectMapper) {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type_");
        converter.setObjectMapper(objectMapper);

        // Map type identifiers to classes
        Map<String, Class<?>> typeIdMappings = new HashMap<>();
        typeIdMappings.put("work", WorkloadRequest.class);
        //typeIdMappings.put("payment", PaymentMessage.class);
        converter.setTypeIdMappings(typeIdMappings);

        return converter;
    }*/

    @Bean
    public ActiveMQConnectionFactory connectionFactory() {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory();
        factory.setBrokerURL(BROKER_URL);
        return factory;
    }

    @Bean
    public JmsTemplate jmsTemplate() {
        return new JmsTemplate(connectionFactory());
    }
}
