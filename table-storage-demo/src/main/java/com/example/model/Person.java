package com.example.model;

import com.microsoft.azure.storage.table.TableServiceEntity;
import lombok.Data;

@Data
public class Person extends TableServiceEntity {
    private  String id;
    private  String name;
    private  Integer age;
}
