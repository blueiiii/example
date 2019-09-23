package com.example.service;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.elasticsearch.action.DocWriteResponse.Result;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.ShardSearchFailure;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.action.support.replication.ReplicationResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.model.Book;


@Service
public class ESService {

	@Autowired
	RestHighLevelClient client;
	
	public String createIndex( String index, String type,String confJson){
		String result = "成功";
		CreateIndexRequest request = new CreateIndexRequest(index);//创建索引
		//创建的每个索引都可以有与之关联的特定设置。
        request.settings(Settings.builder()
                .put("index.number_of_shards", 3)
                .put("index.number_of_replicas", 2)
        );
        
      //创建索引时创建文档类型映射
        request.mapping(type,//类型定义
        		confJson,//类型映射，需要的是一个JSON字符串
                XContentType.JSON);
        //可选参数
        request.timeout(TimeValue.timeValueMinutes(2));//超时,等待所有节点被确认(使用TimeValue方式)
        request.masterNodeTimeout(TimeValue.timeValueMinutes(1));//连接master节点的超时时间(使用TimeValue方式)
        //同步执行
        CreateIndexResponse createIndexResponse;
		try {
			createIndexResponse = client.indices().create(request);
			 //返回的CreateIndexResponse允许检索有关执行的操作的信息，如下所示：
	        boolean acknowledged = createIndexResponse.isAcknowledged();//指示是否所有节点都已确认
	        if(!acknowledged){
	        	result = "失败";
	        }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result = "失败";
		}
       
		return result;
	}
	
	public String get(String index,String type,String id){
		GetRequest request =  new GetRequest(index,type,id);
		try {
			GetResponse  respond = client.get(request, RequestOptions.DEFAULT);
			if(respond.isExists()){
				long v = respond.getVersion();
				String value = respond.getSourceAsString();
				System.out.println(value);
				return value;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("获取es数据失败");
		return "";
	}
	
	public String add(String index,String type,String id,Book book){
		String result = "成功";
		IndexRequest request = new IndexRequest(index, type, id);
		request.source(book.toJson(), XContentType.JSON);
		// 设置刷新策略
		request.setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE);
		IndexResponse indexResponse = null;
         // 同步方式
         try {
			indexResponse = client.index(request);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}    
         	
         if (indexResponse.getResult() == Result.CREATED ) {
             System.out.println("添加："+index+"/"+type+"/"+id);
         } else if (indexResponse.getResult() == Result.UPDATED) {
        	 System.out.println("修改："+index+"/"+type+"/"+id);
         }
         
         ReplicationResponse.ShardInfo shardInfo  = indexResponse.getShardInfo();
         // 如果有分片副本失败，可以获得失败原因信息
         if (shardInfo.getFailed() > 0) {
             for (ReplicationResponse.ShardInfo.Failure failure : shardInfo.getFailures()) {
                 String reason = failure.reason();
                 result = "副本操作失败:" +  reason;
                 return result;
             }
         }
         
         if(shardInfo.getSuccessful() != shardInfo.getTotal()){
        	 result = "操作总分片:" + shardInfo.getTotal() + ", 成功分片:" + shardInfo.getSuccessful();
        	 System.out.println(result);
         	 return result;
         }
         
         return result;
	}
	
	
	public String delete(String index,String type,String id){
		String result = "成功";
		DeleteRequest request = new DeleteRequest(index,type,id);
		
		try {
			DeleteResponse response  = client.delete(request);
		    ReplicationResponse.ShardInfo shardInfo = response.getShardInfo();
	        if (shardInfo.getFailed() > 0) {
	            for (ReplicationResponse.ShardInfo.Failure failure : shardInfo.getFailures()) {
	            	result = "删除分片异常:" + failure.reason();
	            	System.out.println(result);
	            	return result;
	            }
	        }
	        if (response.getResult() == Result.NOT_FOUND) {
	        	result = "文档不存在";
	        	return result;
	        }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	public String search(String index,String type,Map<String,String>  matchs){
		String result = "";
		 // 1、创建search请求
        //SearchRequest searchRequest = new SearchRequest();
		SearchRequest searchRequest = new SearchRequest(index); 
        searchRequest.types(type);
        
        // 2、用SearchSourceBuilder来构造查询请求体 
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder(); 
        
        for(Map.Entry<String, String> entry: matchs.entrySet()){
        	sourceBuilder.query(QueryBuilders.matchQuery(entry.getKey(), entry.getValue())); 
        }
      
        sourceBuilder.from(0); 
        sourceBuilder.size(10); 
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS)); 
        
        //将请求体加入到请求中
        searchRequest.source(sourceBuilder);
        
       //3、发送请求        
        try {
			SearchResponse searchResponse = client.search(searchRequest);
			 //4、处理响应
	        //搜索结果状态信息
			RestStatus status = searchResponse.status();
	        TimeValue took = searchResponse.getTook();
	        Boolean terminatedEarly = searchResponse.isTerminatedEarly();
	        boolean timedOut = searchResponse.isTimedOut();
	        
	        int totalShards = searchResponse.getTotalShards();
            int successfulShards = searchResponse.getSuccessfulShards();
            int failedShards = searchResponse.getFailedShards();
            for (ShardSearchFailure failure : searchResponse.getShardFailures()) {
                // failures should be handled here
            	result = failure.reason();
            	System.out.println(result);
            	return result;
            }
            
            //处理搜索命中文档结果
            SearchHits hits = searchResponse.getHits();
            
            long totalHits = hits.getTotalHits();
            float maxScore = hits.getMaxScore();
            
            SearchHit[] searchHits = hits.getHits();
            for (SearchHit hit : searchHits) {
                // do something with the SearchHit
                
               result +=hit.getSourceAsString();
               result += "\n";
            }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
		return result;
	}
}
