package subfile;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class EventDAO {

	private static Connection connection;
	
	/**
	 * Constructor of the Data Access object.
	 * Connection to SQL Server will be created by given information.
	 */
	public EventDAO() {
		connection = SQLConnection.getMySQLConnection();
	}

	public static final String[] EVENTS_COLUMNS = { "ID", "Event_ID",
			"Event_Name", "Description" };
	public static final String[] PARAMETERS_COLUMNS = { "ID", "Param_ID",
			"Param_Name", "PPS", "Param_Range", "Description", "Param_Start"};
	public static final String[] EVEPARAM_COLUMNS = { "ID", "Event_ID",
		"Param_ID"};
	
	/**
	 * Get Event_name that corresponds to the given Event_ID
	 * @param eID Event_ID
	 * @return String Event_Name 
	 */
	public String getEventNameByEventID(int eID) {
		String name = null;
		Statement statement = null;
		try {
			statement = connection.createStatement();
			String query = "SELECT " + EVENTS_COLUMNS[2]
					+ " FROM events WHERE " + EVENTS_COLUMNS[1] + " = " + eID;
			ResultSet resultSet = statement.executeQuery(query);
			if(!resultSet.next()){
				System.out.println("No result found.");
			} else {
				name = resultSet.getString(EVENTS_COLUMNS[2]);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
		    if (statement != null) {
		    	try {
					statement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		    }
		}
		return name;
	}
	/**
	 * Get ParamInfo that corresponds to the given Param_ID
	 * @param pID Param_ID
	 * @return ParamInfo an Parameter's ParamInfo
	 */
	public ParamInfo getParamInfoByParamID(int pID) {
		ParamInfo pInfo = null;
		Statement statement = null;
		try {
			statement = connection.createStatement();
			String query = "SELECT " + PARAMETERS_COLUMNS[3] + ","
					+ PARAMETERS_COLUMNS[4] + "," + PARAMETERS_COLUMNS[6]
					+ " FROM parameters WHERE " + PARAMETERS_COLUMNS[1] + " = '"
					+ pID + "'";
			ResultSet resultSet = statement.executeQuery(query);
			if(!resultSet.next()){
				System.out.println("No result found.");
			} else {
				pInfo = new ParamInfo(resultSet.getInt(PARAMETERS_COLUMNS[3]), resultSet.getInt(PARAMETERS_COLUMNS[4]), resultSet.getInt(PARAMETERS_COLUMNS[6]));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
		    if (statement != null) {
		    	try {
					statement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		    }
		}
		return pInfo;
	}
	
	/**
	 * Get List of Parameter's ParamInfo that corresponds to the given Event_ID
	 * @param eID Event_ID
	 * @return List<Integer> an ArrayList of ParamInfo
	 */
	public List<ParamInfo> getParamInfoByEventID(int eID) {
		List<ParamInfo> pInfos = new ArrayList<ParamInfo>();
		Statement statement = null;
		try {
			statement = connection.createStatement();
			String query = "SELECT " + EVEPARAM_COLUMNS[2]
					+ " FROM eveparam WHERE " + EVEPARAM_COLUMNS[1] + " = "
					+ eID;
			ResultSet resultSet = statement.executeQuery(query);
			while (resultSet.next()) {
				pInfos.add(getParamInfoByParamID(resultSet.getInt(EVEPARAM_COLUMNS[2])));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
		    if (statement != null) {
		    	try {
					statement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		    }
		}
		return pInfos;
	}
	
	
	/**
	 * Close the connection after using for Security purpose.
	 */
	public void closeConnection(){
		try {
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	class ParamInfo {
		
		int pps;
		int param_Range;
		int param_Start;

		ParamInfo(int pps, int param_range, int param_start) {
			this.pps = pps;
			this.param_Range = param_range;
			this.param_Start = param_start;
		}
	}
}
