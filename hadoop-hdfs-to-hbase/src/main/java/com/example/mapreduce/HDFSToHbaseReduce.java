package com.example.mapreduce;

import java.io.IOException;

import org.apache.hadoop.hbase.client.Mutation;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.mapreduce.TableReducer;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;



public class HDFSToHbaseReduce extends TableReducer<Text, NullWritable, NullWritable>{

	@Override
	protected void reduce(Text key, Iterable<NullWritable> values,
			Reducer<Text, NullWritable, NullWritable, Mutation>.Context context) throws IOException, InterruptedException {
		String[] words = key.toString().split(",");
		if(words.length >=4){
			Put put = new  Put(words[0].getBytes());
			put.addColumn("info".getBytes(), "name".getBytes(),Bytes.toBytes(words[1]));
			put.addColumn("info".getBytes(), "sex".getBytes(), Bytes.toBytes(words[2]));
			put.addColumn("info".getBytes(), "age".getBytes(), Bytes.toBytes(words[3]));
			context.write(NullWritable.get(), put);
		}
	}

	

}
