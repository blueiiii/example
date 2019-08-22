package com.example.mapreduce;

import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.file.tfile.TFile.Reader.Scanner;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.util.Tool;

import com.example.MapReduceApp;

public class HDFSToHbaseDriver extends Configured implements Tool{
	
	private Connection connection;
	
	@SuppressWarnings("deprecation")
	public int run(String[] arg0) throws Exception {
		
		if(arg0.length < 1){
			arg0 = new  String[]{"hdfs://192.168.12.122:9000/input/student"};
		}
		// 初始化表
		initTable();
		Configuration conf = HBaseConfiguration.create();
        //设置zookeeper的地址，可以有多个，以逗号分隔
        conf.set("hbase.zookeeper.quorum","192.168.12.122");
	    //设置zookeeper的端口
        conf.set("hbase.zookeeper.property.clientPort","2181");
        Job job = new Job(conf,"hdfs-to-hbase");
        job.setJarByClass(MapReduceApp.class);
        
        Path in = new Path(arg0[0]);
        
        job.setInputFormatClass(TextInputFormat.class);
        FileInputFormat.addInputPath(job, in);
        
        job.setMapperClass(HDFSToHbaseMapper.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(NullWritable.class);
        
        TableMapReduceUtil.initTableReducerJob("student", HDFSToHbaseReduce.class, job);
        
        boolean isDone = job.waitForCompletion(true);
        printTable();
        return isDone ? 0 : 1;
	}
	
	@SuppressWarnings({ "deprecation"})
	private void initTable() throws IOException{
		
		System.out.println("开始创建student表");
		Configuration conf = HBaseConfiguration.create();
        //设置zookeeper的地址，可以有多个，以逗号分隔
        conf.set("hbase.zookeeper.quorum","192.168.12.122");
	    //设置zookeeper的端口
        conf.set("hbase.zookeeper.property.clientPort","2181");
		//创建hbase的连接，这是一个分布式连接
		connection = ConnectionFactory.createConnection(conf);
	    //这个admin是管理table时使用的，比如说创建表
		HBaseAdmin admin = (HBaseAdmin) connection.getAdmin();
		//声明一个表名
        TableName tableName = TableName.valueOf("student");
		// 如果存在表，则删除表
	    if(admin.tableExists(tableName)){
	    	System.out.println("表已存在，准备删除重建");
	    	admin.disableTable(tableName);
	    	admin.deleteTable(tableName);
	    }
	   
        //构造一个表的描述
		HTableDescriptor desc = new HTableDescriptor(tableName);
        //创建列族
        HColumnDescriptor family = new HColumnDescriptor("info");
        //添加列族
        desc.addFamily(family);
        //创建表
        admin.createTable(desc);
        System.out.println("student表创建完成");
	}
	
	private void printTable() throws IOException{

		Configuration conf = HBaseConfiguration.create();
	    //设置zookeeper的地址，可以有多个，以逗号分隔
	    conf.set("hbase.zookeeper.quorum","192.168.12.122");
	    //设置zookeeper的端口
	    conf.set("hbase.zookeeper.property.clientPort","2181");
	    
	    Table table = connection.getTable(TableName.valueOf("student"));  
	
		  // Instantiating the Scan class
		  Scan scan = new Scan();
		
		  // Scanning the required columns
		  scan.addColumn(Bytes.toBytes("info"), Bytes.toBytes("name"));
		  scan.addColumn(Bytes.toBytes("info"), Bytes.toBytes("sex"));
		  scan.addColumn(Bytes.toBytes("info"), Bytes.toBytes("age"));
		  // Getting the scan result
		  ResultScanner scanner = table.getScanner(scan);
		
		  for (Result result : scanner) {// 按行去遍历
			  byte[] name = result.getValue(Bytes.toBytes("info"), Bytes.toBytes("name")); //读取单条记录
			  byte[] sex = result.getValue(Bytes.toBytes("info"), Bytes.toBytes("sex")); //读取单条记录
			  byte[] age = result.getValue(Bytes.toBytes("info"), Bytes.toBytes("age")); //读取单条记录
			  System.out.print(Bytes.toString(name) + ",");
			  System.out.print(Bytes.toString(sex) + ",");
			  System.out.print(Bytes.toString(age));
			  System.out.println();
		  }
		  
		  scanner.close();
	}
}
