package com.bosssoft.platform.installer.core.initdb.script;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class SqlScript {
	static Logger logger = LoggerFactory.getLogger(SqlScript.class);

	public void run(InputStream stream, String charset, Connection conn, StringBuffer resultBuf) {
		String sqlStatement = null;
		Statement stmt = null;
		try {
			BufferedReader r = new BufferedReader(new InputStreamReader(stream, charset));
			stmt = conn.createStatement();

			r.mark(500);
			String line = r.readLine();
			r.reset();

			if (line.toLowerCase().indexOf(" procedure ") != -1) {
				sqlStatement = getProcedureStatement(r);

				if (sqlStatement != null) {
					execStmt(sqlStatement, stmt, resultBuf);
				}

			} else {
				boolean isFinish = false;
				while (!isFinish) {
					sqlStatement = getNextStatement(r);

					if (sqlStatement != null) {
						if ((sqlStatement.length() >= 5) && (sqlStatement.length() != 0)) {
							execStmt(sqlStatement, stmt, resultBuf);
						}
					} else
						isFinish = true;
				}
			}
		} catch (Throwable e) {
			if (logger.isWarnEnabled())
				logger.warn(null, e);
		} finally {
			if (stmt != null)
				try {
					stmt.close();
				} catch (SQLException sqle) {
				}
		}
	}

	private void execStmt(String sql, Statement stmt, StringBuffer resultBuf) {
		try {
			stmt.execute(sql);
		} catch (SQLException sqle) {
			if ((sql != null) && (sql.trim().toUpperCase().startsWith("DROP"))) {
				return;
			}
			resultBuf.append(sqle.getMessage()).append("\n");

			if (logger.isWarnEnabled()) {
				logger.warn("SQL Error:[" + sql + "]" + sqle.getMessage(), sqle);
			}
			return;
		}
	}

	protected abstract String getNextStatement(BufferedReader bufferedReader) throws IOException;

	protected abstract String getProcedureStatement(BufferedReader bufferedReader) throws IOException;
}