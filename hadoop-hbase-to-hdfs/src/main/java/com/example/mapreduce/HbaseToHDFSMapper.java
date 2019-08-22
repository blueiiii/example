package com.example.mapreduce;

import java.io.IOException;

import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class HbaseToHDFSMapper extends TableMapper<Text, IntWritable>{

	@Override
	protected void map(ImmutableBytesWritable key, Result value,
			Mapper<ImmutableBytesWritable, Result, Text, IntWritable>.Context context)
			throws IOException, InterruptedException {
		
		byte[] sex = value.getValue(Bytes.toBytes("info"), Bytes.toBytes("sex"));
		context.write(new Text(sex),new IntWritable(1));
 	}

	

}
