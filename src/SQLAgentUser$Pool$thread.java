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
			System.out.println(" ConnPool �s������: ��]��" + ex.toString());
		}

		SQLAgentUser$Pool$thread sr1 = new SQLAgentUser$Pool$thread(
				"select * from employees", connPool);
		SQLAgentUser$Pool$thread sr2 = new SQLAgentUser$Pool$thread(
				"select * from coffees", connPool);
		SQLAgentUser$Pool$thread sr3 = new SQLAgentUser$Pool$thread(
				"select * from suppliers", connPool);
		SQLAgentUser$Pool$thread sr4 = new SQLAgentUser$Pool$thread(
				"select * from departments", connPool);

		Thread t1 = new Thread(sr1, "�����1");
		Thread t2 = new Thread(sr2, "�����2");
		Thread t3 = new Thread(sr3, "�����3");
		Thread t4 = new Thread(sr4, "�����4");
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

		System.out.println("����SQL���O: " + sqlStatements + "......");

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
						// �]�i�H��System.out.print("\t"+getField( k )+" ");
						// (SQLAgent �� ResultSet���Oprivate��) �i�H��
						// System.out.print("\t"+rs.getObject(k)); �� getXXX()
						// (SQLAgent �� ResultSet���Oprivate��) �i�H��
						// System.out.print("\t"+rs.getString(k)); �� getXXX()

					}
					System.out.println();

				}

				System.out.println("���榨�\!");
				try {
					Thread.sleep(100);
				} catch (Exception e) {
				}

			} else {
				System.out.println("�w��" + numRows + "�ӦC�Q���.");
			}

			setConnectionSwitch("off");

		} catch (SQLException ex) {
			System.out.println("���楢��: ��]��" + ex.toString());
		}
	} // �غc�� ends

}