package io.cloudNativeData.account.integration;

import org.apache.geode.cache.DataPolicy;
import org.apache.geode.cache.client.ClientCache;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.gemfire.GemfireTemplate;
import org.springframework.data.gemfire.client.ClientRegionFactoryBean;
import org.springframework.data.gemfire.config.annotation.ClientCacheApplication;
import org.springframework.data.gemfire.config.annotation.EnablePdx;
import org.springframework.data.gemfire.repository.config.EnableGemfireRepositories;
import org.springframework.integration.gemfire.outbound.CacheWritingMessageHandler;

import java.util.Map;

@ClientCacheApplication
@Configuration
@EnablePdx
@EnableGemfireRepositories
public class GemFireConf
{
    @Bean("Items")
    ClientRegionFactoryBean<String, Map<Object,Object>> items(ClientCache gemFireCache)
    {
        var bean = new ClientRegionFactoryBean<String, Map<Object,Object>>();
        bean.setCache(gemFireCache);
        bean.setName("Items");
        bean.setDataPolicy(DataPolicy.EMPTY);
        return bean;
    }

    @Bean
    CacheWritingMessageHandler cacheWritingMessageHandler(
            @Qualifier("itemsTemplate") GemfireTemplate gemfireTemplate)
    {
        return new CacheWritingMessageHandler(gemfireTemplate.getRegion());
    }
}
