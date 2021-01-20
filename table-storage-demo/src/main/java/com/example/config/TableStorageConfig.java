package com.example.config;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.table.CloudTableClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URISyntaxException;
import java.security.InvalidKeyException;

@Slf4j
@Configuration
public class TableStorageConfig {

    @Value("${tablestorage.storageConnectionString}")
    public String storageConnectionString;
    /**
     * table storage客户端实例
     *
     * @return
     * @throws URISyntaxException
     * @throws InvalidKeyException
     */
    @Bean
    public CloudTableClient cloudTableClient() throws URISyntaxException, InvalidKeyException {
        log.info("CloudTableClient Init...");
        CloudStorageAccount storageAccount =
                CloudStorageAccount.parse(storageConnectionString);
        return storageAccount.createCloudTableClient();
    }
}
