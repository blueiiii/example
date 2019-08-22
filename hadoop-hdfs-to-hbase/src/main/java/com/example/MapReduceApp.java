package com.example;

import org.apache.hadoop.util.ToolRunner;

import com.example.mapreduce.HDFSToHbaseDriver;

public class MapReduceApp {

	  public static void main(String[] args) throws Exception {
	        
	        int run = ToolRunner.run(new HDFSToHbaseDriver(), args);
	        System.exit(run);
	    }
}
