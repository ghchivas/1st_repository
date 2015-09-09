package subfile;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import subfile.EventDAO.ParamInfo;


public class Event {
	
	// Common fields
	private int length;
	private int type;
	private int scanID;
	private int hour;
	private int minute;
	private int second;
	private int miliSecond;
	private int eventID;
	private int ueContextID;
	private int rncModule;
	private int cID_1;
	private int rncID_1;
	private int cID_2;
	private int rncID_2;
	private int cID_3;
	private int rncID_3;
	private int cID_4;
	private int rncID_4;
	private String eventName;
	private int pduType;
	private int protocolID;
	private int direction;
	private int messageLength;
	private List<Integer> additionalList;
	
	public Event() {
		eventName = "";
	}
	
	public void processBin(BufferedInputStream in, BufferedWriter out) throws IOException {
		int[] b = new int[65535];
		int count = 0;
		int size = 0;
		additionalList = new ArrayList<Integer>();
		int [] a = new int [b.length/2];
		ProcessFile pf = new ProcessFile(in,out);
		EventDAO eDao = new EventDAO();
		int r = 10, j = 14;
		while (true) {
			if(count++ == 1000){
			//	sb = new StringBuilder();
				break;
			}
			b[0] = in.read();
			b[1] = in.read();
			length = (b[0] << 8) | b[1];
			//out.write(length + "\t");
			size += length;
//			if (length <= 10) {
//				out.write(size + "\n");
//				break;
//			}
			for (int i = 2; i < length; i++) {
				b[i] = in.read();
			}
			type = b[2];
			//   common event parameter
			scanID = (b[3]<<16)|((b[4]<<8)+b[5]); 
			hour = b[6]>>3;
			minute = (b[6]&7)<<3|(b[7]>>5);
			second = (b[7]&31)<<1|(b[8]>>7);
			miliSecond = (b[8]&127)<<4|(b[9]>>4);
			eventID = (b[9]&15)<<7|(b[10]>>1);
			eventName = eDao.getEventNameByEventID(eventID);
			ueContextID = ((b[10]&1)<<15)|((b[12]>>1)|(b[11]<<7));
			rncModule = ((b[12]&1)<<6)|(b[13]>>2);
			cID_1 = ((b[13]&3)<<15)|((b[14]<<7)|(b[15]>>1));
			rncID_1 = ((b[15]&1)<<12)|((b[16]<<4)|(b[17]>>4));
			cID_2 = ((b[17]&15)<<13)|((b[18]<<5)|(b[19]>>3));
			rncID_2 = ((b[19]&7)<<10)|((b[20]<<2)|(b[21]>>6));
			cID_3 = ((b[21]&63)<<11)|((b[22]<<3)|(b[23]>>5));
			rncID_3 = ((b[23]&31)<<8)|b[24];
			cID_4 = (b[25]<<9)|((b[26]<<1)|(b[27]>>7));
			rncID_4 = ((b[27]&127)<<6)|(b[28]>>2);

			for (int i = 0; i < a.length; i++) {
				a[i] = b[2*i]<<8|b[2*i+1];
			}
			String s = "EVENT_VALUE_INVALID\t";
			if(hour > -1 && hour < 24){out.write(hour + "\t");}else{out.write(s);}
			if(minute > -1 && minute < 60){out.write(minute + "\t");}else{out.write(s);}
			if(second > -1 && second < 62){out.write(second + "\t");}else{out.write(s);}
			if(miliSecond > -1 && miliSecond <1000){out.write(miliSecond + "\t");}else{out.write(s);}
			out.write(eventID + "\t");
			out.write(eventName + "\t");
			if(ueContextID < -1 || ueContextID > (Math.pow(2, 15)-1)){
				out.write(s);}else{out.write(ueContextID+"\t");}
			if(rncModule > 0 && rncModule < Math.pow(2, 6)){out.write(rncModule+"\t");}else{out.write(s);}
			if(checkCid(cID_1) != -1){out.write(cID_1 + "\t");}else{out.write(s);}
			if(checkRncID(rncID_1) != -1){out.write(rncID_1 + "\t");}else{out.write(s);}
			if(checkCid(cID_2) != -1){out.write(cID_2 + "\t");}else{out.write(s);}
			if(checkRncID(rncID_2) != -1){out.write(rncID_2 + "\t");}else{out.write(s);}
			if(checkCid(cID_3) != -1){out.write(cID_3 + "\t");}else{out.write(s);}
			if(checkRncID(rncID_3) != -1){out.write(rncID_3 + "\t");}else{out.write(s);}
			if(checkCid(cID_4) != -1){out.write(cID_4 + "\t");}else{out.write(s);}
			if(checkRncID(rncID_4) != -1){out.write(rncID_4 + "\t");}else{out.write(s);}

			// Check event is internal or external
			if (eventID >= 384 && eventID <= 475) {
				List<ParamInfo> listParam = eDao.getParamInfoByEventID(getEventID());
				for (int i = 0; i < listParam.size(); i++) {
					additionalList.add(pf.readnBit(listParam.get(i).pps, r, j, a)[0]);
					int r_temp = pf.readnBit(listParam.get(i).pps, r, j, a)[1];
					int j_temp = pf.readnBit(listParam.get(i).pps, r, j, a)[2];
					r = r_temp;j = j_temp;
					if(additionalList.get(i) >= listParam.get(i).param_Start && additionalList.get(i) <= listParam.get(i).param_Range){
						out.write(additionalList.get(i) + "\t");
					}else{
						out.write(s);
					}
				}
			} else {
				pduType = (b[28]&3) << 3 | (b[29]>>5);
				protocolID = (b[29] & 31) >> 1;
				direction = (b[29] &1 )<<1 | (b[30]>>7);
				messageLength = (b[30] & 127)<< 9| ((b[31]<<1)| (b[32] >> 7)); 
				out.write(pduType + "\t");
				out.write(protocolID + "\t");
				out.write(direction + "\t");
			} 
			out.write("\n");
		} // End while
		out.write(" " +size);
		//eDao.closeConnection();
	}

	public int checkRncID(int rncID) {
		if(rncID < 0 || rncID > 4095){
        	return -1;
        }
		return rncID;
	}
	
	/**
	 * @return the length
	 */
	public int getLength() {
		return length;
	}

	/**
	 * @return the type
	 */
	public int getType() {
		return type;
	}

	/**
	 * @return the scanID 
	 */
	public int getScanID() {
		return scanID;
	}

	/**
	 * @return the hour
	 */
	public int getHour() {
		return hour;
	}

	/**
	 * @return the minute
	 */
	public int getMinute() {
		return minute;
	}

	/**
	 * @return the second
	 */
	public int getSecond() {
		return second;
	}

	/**
	 * @return the miliSecond
	 */
	public int getMiliSecond() {
		return miliSecond;
	}

	/**
	 * @return the eventID
	 */
	public int getEventID() {
		return eventID;
	}

	/**
	 * @return the ueContextID
	 */
	public int getUeContextID() {
		return ueContextID;
	}

	/**
	 * @return the rncModule
	 */
	public int getRncModule() {
		return rncModule;
	}

	/**
	 * @return the cID_1
	 */
	public int getcID_1() {
		return cID_1;
	}

	/**
	 * @return the rncID_1
	 */
	public int getRncID_1() {
		return rncID_1;
	}

	/**
	 * @return the cID_2
	 */
	public int getcID_2() {
		return cID_2;
	}

	/**
	 * @return the rncID_2
	 */
	public int getRncID_2() {
		return rncID_2;
	}

	/**
	 * @return the cID_3
	 */
	public int getcID_3() {
		return cID_3;
	}

	/**
	 * @return the rncID_3
	 */
	public int getRncID_3() {
		return rncID_3;
	}

	/**
	 * @return the cID_4
	 */
	public int getcID_4() {
		return cID_4;
	}

	/**
	 * @return the rncID_4
	 */
	public int getRncID_4() {
		return rncID_4;
	}

	/**
	 * @return the eventName
	 */
	public String getEventName() {
		return eventName;
	}

	/**
	 * @return the pduType
	 */
	public int getPduType() {
		return pduType;
	}

	/**
	 * @return the protocolID
	 */
	public int getProtocolID() {
		return protocolID;
	}

	/**
	 * @return the direction
	 */
	public int getDirection() {
		return direction;
	}

	/**
	 * @return the messageLength
	 */
	public int getMessageLength() {
		return messageLength;
	}

	public int checkCid(int cid) {
		// TODO Auto-generated method stub
		if(cid < 0 || cid > 65535){
			return -1;
		}
		return cid;
	}
}
