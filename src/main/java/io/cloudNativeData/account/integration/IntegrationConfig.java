package io.cloudNativeData.account.integration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
public class IntegrationConfig {

    @Value("${app.batch.size:500}")
    private int batchSize;

    @Value("${app.sql.select}")
    private String selectQuery;

    @Value("${app.sql.update}")
    private String updateSQL;

    @Value("${app.inbound.select.fixed.delay.ms:1000}")
    private long inboundSelectFixedDelayMs;

    @Value("${app.gemfire.key.field.name:id}")
    private String keyFieldName;

    @Bean
    public PlatformTransactionManager transactionManager(
            DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    public IntegrationFlow gemFireInboundFlow(
            DataSource dataSource,
            CacheWritingMessageHandler cacheWritingMessageHandler,
            PlatformTransactionManager transactionManager) {

        return IntegrationFlow
                .from(
                        Jdbc.inboundAdapter(
                                        dataSource,
                                        selectQuery)
                                .updateSql(
                                        updateSQL),
                        endpoint -> endpoint.poller(
                                Pollers.fixedDelay(inboundSelectFixedDelayMs)
                                        .transactional(transactionManager)))
                .transform((List<Map<Object, Object>> payload) ->
                        payload.stream()
                                .collect(Collectors.toMap(
                                        item -> item.get(keyFieldName),
                                        HashMap::new
                                )))
                .handle(cacheWritingMessageHandler)
                .get();
    }
}
