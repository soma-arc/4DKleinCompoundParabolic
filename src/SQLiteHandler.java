import java.sql.*;
import java.util.*;

public class SQLiteHandler {
	private Connection sqliteConnection;
	private Statement sqliteStatement;
	public SQLiteHandler(String dbName) throws SQLException, ClassNotFoundException {
		Class.forName("org.sqlite.JDBC");
		Properties prop = new Properties();
		prop.put("SyncMode","Off");
		prop.put("JournalMode","Wal");
		sqliteConnection = DriverManager.getConnection("jdbc:sqlite:"+ dbName, prop);
		sqliteStatement = sqliteConnection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
		sqliteStatement.setFetchSize(10000);
		sqliteConnection.setAutoCommit(false);
		createTables();
	}
	
	private void createTables()throws SQLException{
		sqliteStatement.execute(
				"create table if not exists sakugawa_parabolic_table("
				+ "id integer primary key autoincrement,"
				+ "z0 real,"
				+ "theta_a real,"
				+ "theta_b real,"
				+ "max_level integer,"
				+ "points text)");
		commit();
	}
	
	public ResultSet query(String query)throws SQLException{
		sqliteStatement.setFetchSize(1000);
		return sqliteStatement.executeQuery(query);
	}
	
	public void execute(String sql) throws SQLException{
		sqliteStatement.execute(sql);
	}
	
	
	public void commit()throws SQLException{
		sqliteConnection.commit();
	}
}
