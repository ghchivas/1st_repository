package subfile;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

public class SubTest {
	public static void main(String[] args) throws IOException {
		String path = "D:/project_bit/bin/A20150527.0900+0700-0915+0700_SubNetwork=RSG091E,MeContext=RSG091E_rnc_gpehfile_Mp1.bin";
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(path));
		BufferedWriter out = new BufferedWriter(new FileWriter("D:/output.txt"));
		Event ev = new Event();
		long start = System.nanoTime();
		ev.processBin(in, out);
		long end = System.nanoTime();
		System.out.println(end - start);
		in.close();
		out.close();
	}
}
