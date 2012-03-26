package com.tuqianyi.db;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBUtils
{
	static Logger _logger = Logger.getLogger(DBUtils.class.getName());

    public static final String LOCAL_DS_NAME = "jdbc/tuqianyi";

    public static Connection getConnection() throws NamingException, SQLException
    {
        Context initContext = new InitialContext();
        Context envContext = (Context) initContext.lookup("java:/comp/env");
        DataSource ds = (DataSource) envContext.lookup(LOCAL_DS_NAME);
        return ds.getConnection();
    }

    public static DataSource getDataSource(String dsName) throws NamingException, SQLException
    {
        if(dsName == null)
        {
            dsName = LOCAL_DS_NAME;
        }
        Context initContext = new InitialContext();
        Context envContext = (Context) initContext.lookup("java:/comp/env");
        return (DataSource) envContext.lookup(dsName);
    }

    public static void close(Connection conn, Statement statement, ResultSet rs)
    {
        try
        {
            if(rs != null)
            {
                rs.close();
            }
            if(statement != null)
            {
                statement.close();
            }
            if(conn != null && !conn.isClosed())
            {
                conn.close();
            }
        }
        catch(SQLException e)
        {
            _logger.log(Level.SEVERE, "error", e);
        }

    }

    public static void rollback(Connection conn)
    {
        try
        {
            if(conn != null)
            {
                conn.rollback();
            }
        }
        catch(SQLException ex)
        {
        	_logger.log(Level.SEVERE, "error", ex);
        }
    }
}
