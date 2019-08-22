package com.example;

import org.apache.hadoop.util.ToolRunner;

import com.example.mapreduce.HbaseToHDFSDriver;


public class MapReduceApp {

	  public static void main(String[] args) throws Exception {
	        
	        int run = ToolRunner.run(new HbaseToHDFSDriver(), args);
	        System.exit(run);
	    }
}
