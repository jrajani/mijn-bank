package io.blueharvest.labs.axon.transactions;

import org.axonframework.config.Configuration;
import org.axonframework.config.ConfigurationScopeAwareProvider;
import org.axonframework.config.EventProcessingConfigurer;
import org.axonframework.deadline.DeadlineManager;
import org.axonframework.deadline.SimpleDeadlineManager;
import org.axonframework.eventhandling.TrackingEventProcessorConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;


@SpringBootApplication
public class TransactionApplication {

    @Autowired
    protected Configuration axonConfiguration;

    @Bean
    public DeadlineManager deadlineManager() {
        return SimpleDeadlineManager.builder().scopeAwareProvider(new ConfigurationScopeAwareProvider(axonConfiguration)).build();
    }

    @Autowired
    public void configure(EventProcessingConfigurer eventProcessingConfigurer) {
        eventProcessingConfigurer.registerTrackingEventProcessor(
                "MoneyTransactionSagaProcessor",
                Configuration::eventStore,
                configuration -> TrackingEventProcessorConfiguration
                        .forSingleThreadedProcessing()
                        .andInitialTrackingToken(
                                streamableMessageSource -> streamableMessageSource.createHeadToken()));
    }

    public static void main(String[] args) {
        SpringApplication.run(TransactionApplication.class, args);
    }

}
