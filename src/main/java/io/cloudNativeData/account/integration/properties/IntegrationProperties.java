package io.cloudNativeData.account.integration.properties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ConfigurationProperties(prefix = "integration")
public class IntegrationProperties {
    private int batchSize = 500;
    private String sqlSelect;
    private String sqlUpdate;
    private long inboundSelectFixedDelayMs = 1000;
    private String keyFieldName = "id";
}
