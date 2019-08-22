package com.example.mapreduce;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;

import com.example.MapReduceApp;

public class HbaseToHDFSDriver extends Configured implements Tool{


	@SuppressWarnings("deprecation")
	public int run(String[] arg0) throws Exception {
		if(arg0.length < 1){
			arg0 = new  String[]{"hdfs://192.168.12.122:9000/output/student"};
		}
		Configuration conf = HBaseConfiguration.create();
		// 设置zookeeper的地址，可以有多个，以逗号分隔
        conf.set("hbase.zookeeper.quorum","192.168.12.122");
	    // 设置zookeeper的端口
        conf.set("hbase.zookeeper.property.clientPort","2181");
        Job job = new Job(conf,"hbase-to-hdfs");
        job.setJarByClass(MapReduceApp.class);
        
        // 取对业务有用的数据 info,age
        Scan scan = new Scan();
        scan.addColumn(Bytes.toBytes("info"), Bytes.toBytes("sex"));
        
        TableMapReduceUtil.initTableMapperJob(
                "student".getBytes(), // 指定表名
                scan, // 指定扫描数据的条件
                HbaseToHDFSMapper.class, // 指定mapper class
                Text.class,     // outputKeyClass mapper阶段的输出的key的类型
                IntWritable.class, // outputValueClass mapper阶段的输出的value的类型
                job, // job对象
                false
                );
        
        job.setReducerClass(HbaseToHDFSReduce.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        Path out = new Path(arg0[0]);
        
        FileSystem fs = FileSystem.get(conf);
        if(fs.exists(out)) {
            fs.delete(out,true);
        }
       
        FileOutputFormat.setOutputPath(job, out);
        boolean isDone = job.waitForCompletion(true);
        
        return isDone ? 0 : 1;
	}

	
}
