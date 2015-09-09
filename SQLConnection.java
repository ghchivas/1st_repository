package subfile;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLConnection {
	
	private static String driver=null;
	private static String dburl=null;
	private static String username=null;
	private static String password=null;

	/**
	 * Get a Connection to the MySQL Server
	 * with database url, username and password
	 * from "config.txt"
	 * @return Connection connection'll be used by the Data Access Object
	 */
	public static Connection getMySQLConnection() {
		readDatabaseInfo();
		if(dburl!=null&&username!=null&&password!=null){
			try {
				Class.forName(driver);
				Connection connection = DriverManager.getConnection(dburl,username,password);
				return connection;
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				return null;
			} catch (SQLException e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}
	
	/**
	 * Read connection info from dbconfig.html
	 */
	private static void readDatabaseInfo(){
		BufferedReader br = null;
		int c = 0;
		try {
			String sCurrentLine;
			br = new BufferedReader(new FileReader("dbconfig.html"));
			while ((sCurrentLine = br.readLine()) != null) {
				switch (c) {
				case 10:
					driver = sCurrentLine;
					break;
				case 12:
					dburl = sCurrentLine;
					break;
				case 14:
					username = sCurrentLine;
					break;
				case 16:
					password = sCurrentLine;
					break;
				default:
					break;
				}
				c++;
				if(c>17){
					break;
				}
			}
 
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

}
