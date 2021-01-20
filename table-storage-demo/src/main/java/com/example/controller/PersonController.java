package com.example.controller;

import com.example.model.Person;
import com.example.service.TableStorageService;
import com.microsoft.azure.storage.ResultContinuation;
import com.microsoft.azure.storage.ResultSegment;
import com.microsoft.azure.storage.table.TableResult;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@Validated
@RequestMapping(value = "/person")
@Slf4j
public class PersonController {

    private String tableName = "ABlueStorage";

    @Autowired
    private TableStorageService tableStorageService;

    @ApiOperation(value = "save person", notes = "save person")
    @PostMapping("/save")
    public TableResult save(@RequestBody Person person){
        return tableStorageService.save(tableName,person);
    }

    @ApiOperation(value = "delete person", notes = "save person")
    @DeleteMapping("/delete")
    public TableResult save(@RequestParam String partitionKey,@RequestParam String rowKey){
        Person person = new Person();
        person.setPartitionKey(partitionKey);
        person.setRowKey(rowKey);
        person.setEtag("W/\\\"datetime'2021-01-20T06%3A32%3A50.6691162Z'\\\"");
        return tableStorageService.delete(tableName,person);
    }

    @ApiOperation(value = "query person", notes = "query person")
    @GetMapping("/list/{partitionKey}")
    public List<Person> list(@PathVariable String partitionKey) {
        ResultContinuation resultContinuation = null;
        ResultSegment resultSegment = null;
        List<Person> persons = new ArrayList<>();
        do  {
            resultSegment = tableStorageService.query(tableName, partitionKey, Person.class, resultContinuation);
            persons.addAll(resultSegment.getResults());
            resultContinuation = resultSegment.getContinuationToken();
        }while(resultSegment != null && resultSegment.getHasMoreResults());

        return persons;
    }
}
