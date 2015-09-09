package subfile;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
public class ProcessFile {
	BufferedInputStream in;
	BufferedWriter out;
	
	/**
	 * @param in
	 * @param out
	 */
	public ProcessFile(BufferedInputStream in, BufferedWriter out) {
		this.in = in;
		this.out = out;
	}

	/**
	 * Get value multiple of 16 bit
	 * @param s quotient of number_bit and 16
	 * @param i index of element in array b
	 * @param b array of integer read from file
	 * @return value multiple
	 */
	public int multi(int s,int i, int b[]) {
		int num = 0;
		for (int j = 0; j < s; j++) {
			num += b[i+j]<<(16*(s - 1 - j));
		} 
		return num;
	}
	
	/**
	 * Get n bits in bits array
	 * @param n number_bit need get value 
	 * @param r residual after read bit
	 * @param i index of element of array
	 * @param b array of integer read from file
	 * @return array list include number_bit, residual, index of element
	 */
	public int[] readnBit(int n, int r, int i, int b[]) {
		int value = 0;
		int []data = new int[3];
		int s = n/16;
		if(r == 0){
			if(n - 16*s == 0){
				value = multi (s, i, b);
				r = 0;
				i += s;
			}else if(n - 16*s > 0){
				value = ((multi(s, i, b)<<(n - 16*s))|(b[i + s]>>(16*(s + 1) - n)));
				r = 16*(s + 1) - n;
				i += s;
			}else{
				value = (multi(s, i, b))>>(16*s-n);
				r = 16*s - n;
				i = i + s -1;
			}
		}else{
			if(s == 0){
				if(n - r > 0 && n - r < 16){
					value = ((b[i]&(int)(Math.pow(2, r) - 1))<<(n-r))|(b[i+1]>>(16 + r - n));
					i += 1;
					r = 16 + r - n;
				}else if(n - r == 0){
					value = b[i]&(int)(Math.pow(2, r) - 1);
					r = 0; 
					i += 1;
				}else if(n- r < 0){
					value = (b[i]&((int)(Math.pow(2, r) - 1)))>>(r - n);
					r = r - n; 
				}
			}else{
				if(n - 16*s - r > 0){
					value = (((b[i]&(int)(Math.pow(2, r) - 1))<<(n - r))|(((multi(s, i + 1, b)<<(n - 16*s - r))|(b[i + s + 1]>>(16*(s + 1) + r - n)))));
					r = 16*(s + 1) + r - n;
					i = i + s + 1;
				}else if(n - 16*s - r == 0){
					value = ((b[i]&(int)(Math.pow(2, r) - 1))<<(16*s))|(multi(s, i + 1, b));
					r = 0;
					i = i + s + 1;
				}else if(n - 16*s - r < 0){
					value = ((b[i]&(int)(Math.pow(2, r) - 1))<<(n - r))|(multi(s, i + 1, b)>>(16*s + r - n));
					r = 16*s + r - n;
					i += s;
				}
			}
		}
		data[0] = value;
		data[1] = r;
		data[2] =i;
		return data;
	}
}
