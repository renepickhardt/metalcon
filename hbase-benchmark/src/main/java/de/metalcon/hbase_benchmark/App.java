package de.metalcon.hbase_benchmark;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;

/**
 * Hello world!
 * 
 */
public class App {
	public static void main(String[] args) {
		Configuration conf = HBaseConfiguration.create();
		HTable table = null;
		try {
			table = new HTable(conf, "testtable");
			Put put = new Put(Bytes.toBytes("row1"));
			put.add(Bytes.toBytes("colfam1"), Bytes.toBytes("qual1"),
					Bytes.toBytes("val1"));
			put.add(Bytes.toBytes("colfam1"), Bytes.toBytes("qual2"),
					Bytes.toBytes("val2"));

			table.put(put);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
