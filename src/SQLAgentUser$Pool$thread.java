import java.sql.*;

import com.mysql.jdbc.Driver;

class SQLAgentUser$Pool$thread extends SQLAgent implements Runnable {

	String sqlStatements;
	ConnPool connPool;

	public static void main(String argv[]) throws SQLException {
		ConnPool connPool = new ConnPool();
		/*
		 * connPool.setDriverName("COM.ibm.db2.jdbc.app.DB2Driver");
		 * connPool.setJdbcURL("jdbc:db2:sample");
		 * connPool.setUserName("administrator"); connPool.setPassword("678");
		 */

		String url = "jdbc:mysql://localhost:3306/XE";
		//connPool.setDriverName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		Driver driver = (Driver) DriverManager.getDriver(url);
		connPool.setDriverName(driver.getClass().getName());
		connPool.setJdbcURL(url);
		connPool.setUserName("root");
		connPool.setPassword("kent1011");
		try {
			connPool.setConnectionSwitch("on");
		} catch (SQLException ex) {
			System.out.println(" ConnPool 連結失敗: 原因為" + ex.toString());
		}

		SQLAgentUser$Pool$thread sr1 = new SQLAgentUser$Pool$thread(
				"select * from employees", connPool);
		SQLAgentUser$Pool$thread sr2 = new SQLAgentUser$Pool$thread(
				"select * from coffees", connPool);
		SQLAgentUser$Pool$thread sr3 = new SQLAgentUser$Pool$thread(
				"select * from suppliers", connPool);
		SQLAgentUser$Pool$thread sr4 = new SQLAgentUser$Pool$thread(
				"select * from departments", connPool);

		Thread t1 = new Thread(sr1, "執行緒1");
		Thread t2 = new Thread(sr2, "執行緒2");
		Thread t3 = new Thread(sr3, "執行緒3");
		Thread t4 = new Thread(sr4, "執行緒4");
		t1.start();
		t2.start();
		t3.start();
		t4.start();
	}

	public SQLAgentUser$Pool$thread(String sqlStatements, ConnPool connPool) {
		this.sqlStatements = sqlStatements;
		this.connPool = connPool;
	}

	public void run() {
		setConnPool(connPool);

		System.out.println("執行SQL指令: " + sqlStatements + "......");

		try {

			setConnectionSwitch("on");

			int numRows = 0;
			if ((numRows = execSQL(sqlStatements)) == -1) {
				String[] columnName = getColumnNames();
				for (int j = 0; j < columnName.length; j++) {
					System.out.print("\t" + columnName[j] + " ");
				}
				System.out.println();

				while (nextRow()) {
					for (int k = 1; k <= getColumnCount(); k++) {
						System.out.print("\t" + getFieldString(k) + " ");
						// 也可以用System.out.print("\t"+getField( k )+" ");
						// (SQLAgent 的 ResultSet不是private時) 可以用
						// System.out.print("\t"+rs.getObject(k)); 及 getXXX()
						// (SQLAgent 的 ResultSet不是private時) 可以用
						// System.out.print("\t"+rs.getString(k)); 及 getXXX()

					}
					System.out.println();

				}

				System.out.println("執行成功!");
				try {
					Thread.sleep(100);
				} catch (Exception e) {
				}

			} else {
				System.out.println("已有" + numRows + "個列被更動.");
			}

			setConnectionSwitch("off");

		} catch (SQLException ex) {
			System.out.println("執行失敗: 原因為" + ex.toString());
		}
	} // 建構元 ends

}