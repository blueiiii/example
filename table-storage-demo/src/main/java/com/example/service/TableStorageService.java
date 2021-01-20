package com.example.service;

import com.example.model.Person;
import com.microsoft.azure.storage.ResultContinuation;
import com.microsoft.azure.storage.ResultSegment;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.table.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URISyntaxException;

@Slf4j
@Service
public class TableStorageService {

    @Autowired
    private  CloudTableClient tableClient;

    private static final String PARTITION_KEY = "PartitionKey";

    public TableResult  save(String tableName,TableEntity entity){
        // Create a cloud table object for the table.
        TableResult rs = null;
        try{
            CloudTable cloudTable = this.tableClient.getTableReference(tableName);
            cloudTable.createIfNotExists();
            // Create an operation to save the new customer to the people table.
            TableOperation operation = TableOperation.insertOrReplace(entity);
            // Submit the operation to the table service.
            rs = cloudTable.execute(operation);
        }catch (Exception e){
            log.error("TableStorageUtil save data fail.",e);
            return null;
        }
        return rs;
    }

    // 删除接口调试未成功，原因未知
    public TableResult  delete(String tableName,TableEntity entity){
        // Create a cloud table object for the table.
        TableResult rs = null;
        try{
            CloudTable cloudTable = this.tableClient.getTableReference(tableName);
            cloudTable.createIfNotExists();
            // Create an operation to save the new customer to the people table.
            TableOperation operation = TableOperation.delete(entity);
            // Submit the operation to the table service.
            rs = cloudTable.execute(operation);
        }catch (Exception e){
            log.error("TableStorageUtil delete data fail.",e);
            return null;
        }
        return rs;
    }

    public ResultSegment query(String tableName,String partitionKey,Class clazz,ResultContinuation continuationToken){
        CloudTable cloudTable = null;
        try {
            cloudTable = this.tableClient.getTableReference(tableName);
            String partitionFilter = TableQuery.generateFilterCondition(TableStorageService.PARTITION_KEY, TableQuery.QueryComparisons.EQUAL, partitionKey);
            TableQuery partitionQuery = TableQuery.from(clazz).where(partitionFilter);
            return cloudTable.executeSegmented(partitionQuery, continuationToken);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (StorageException e) {
            e.printStackTrace();
        }
        return  null;
    }
}
