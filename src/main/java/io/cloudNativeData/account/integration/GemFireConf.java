package io.cloudNativeData.account.integration;

import io.cloudNativeData.account.integration.properties.IntegrationProperties;
import org.apache.geode.cache.DataPolicy;
import org.apache.geode.cache.client.ClientCache;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.gemfire.GemfireTemplate;
import org.springframework.data.gemfire.client.ClientRegionFactoryBean;
import org.springframework.data.gemfire.config.annotation.ClientCacheApplication;
import org.springframework.data.gemfire.config.annotation.EnablePdx;
import org.springframework.integration.gemfire.outbound.CacheWritingMessageHandler;

import java.util.Map;

@ClientCacheApplication
@Configuration
@EnablePdx
public class GemFireConf
{
    @Bean("Region")
    ClientRegionFactoryBean<String, Map<Object,Object>> region(ClientCache gemFireCache, IntegrationProperties integrationProperties)
    {
        var bean = new ClientRegionFactoryBean<String, Map<Object,Object>>();
        bean.setCache(gemFireCache);
        bean.setRegionName(integrationProperties.getRegionName());
        bean.setDataPolicy(DataPolicy.EMPTY);
        return bean;
    }

    @Bean
    CacheWritingMessageHandler cacheWritingMessageHandler(
            @Qualifier("regionTemplate") GemfireTemplate gemfireTemplate)
    {
        return new CacheWritingMessageHandler(gemfireTemplate.getRegion());
    }
}
