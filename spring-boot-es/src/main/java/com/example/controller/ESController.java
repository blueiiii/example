package com.example.controller;

import java.util.HashMap;
import java.util.Map;

import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.model.Book;
import com.example.service.ESService;

@RestController
public class ESController {
	
	@Autowired
	private ESService esService;
	
	@PostMapping("{index}/{type}/index")
	public String esIndex(@PathVariable String index,@PathVariable String type,@RequestBody String confJson){
        return esService.createIndex(index, type,confJson );
	}
	
	@GetMapping("{index}/{type}/{id}")
	public String esGet(@PathVariable String index,@PathVariable String type,@PathVariable String id){
		return esService.get(index, type, id);
	}
	
	@PostMapping("{index}/{type}/{id}")
	public String esPut(@PathVariable String index,@PathVariable String type,@PathVariable String id,@RequestBody Book  book){
		return esService.add(index, type, id,book);
	}
	
	@DeleteMapping("{index}/{type}/{id}")
	public String esDelete(@PathVariable String index,@PathVariable String type,@PathVariable String id){
		return esService.delete(index, type, id);
	}
	
	
	@PostMapping("/{index}/{type}/search")
	public String esSearch(@PathVariable String index,@PathVariable String type,@RequestBody Map<String,String> matchs){
		return esService.search(index,type,matchs);
	}
	
}
