package io.cloudNativeData.account.integration;

import io.cloudNativeData.account.integration.properties.IntegrationProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.gemfire.outbound.CacheWritingMessageHandler;
import org.springframework.integration.jdbc.dsl.Jdbc;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@Slf4j
@EnableConfigurationProperties(IntegrationProperties.class)
public class IntegrationConfig {

    @Bean
    public PlatformTransactionManager transactionManager(
            DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    public IntegrationFlow gemFireInboundFlow(
            DataSource dataSource,
            CacheWritingMessageHandler cacheWritingMessageHandler,
            PlatformTransactionManager transactionManager,
            IntegrationProperties properties) {

        return IntegrationFlow
                .from(
                        Jdbc.inboundAdapter(
                                        dataSource,
                                        properties.getSqlSelect())
                                .updateSql(
                                        properties.getSqlUpdate()),
                        endpoint -> endpoint.poller(
                                Pollers.fixedDelay(properties.getInboundSelectFixedDelayMs())
                                        .transactional(transactionManager)))
                .transform((List<Map<Object, Object>> payload) ->
                        payload.stream()
                                .collect(Collectors.toMap(
                                        item -> item.get(properties.getKeyFieldName()),
                                        HashMap::new
                                )))
                .handle(cacheWritingMessageHandler)
                .get();
    }
}
