package com.tuqianyi.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.naming.NamingException;

import com.tuqianyi.model.ImageLabel;
import com.tuqianyi.model.Item;
import com.tuqianyi.model.LabelCategory;
import com.tuqianyi.model.User;
import com.tuqianyi.utils.IDGenerator;

public class Dao {
	
	static Logger _logger = Logger.getLogger(Dao.class.getName());
	
	public static final Dao INSTANCE = new Dao();
	
	private Dao()
	{
		
	}
	
	public List<LabelCategory> getLabelCategories() throws Exception
	{
		Connection conn = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		try
		{
			conn = DBUtils.getConnection();
			String sql = "select * from category_t";
			statement = conn.prepareStatement(sql);
			rs = statement.executeQuery();
			List<LabelCategory> categories = new ArrayList<LabelCategory>();
			while (rs.next())
			{
				LabelCategory category = new LabelCategory();
				category.setCategoryID(rs.getLong("category_id_c"));
				category.setName(rs.getString("category_name_c"));
				categories.add(category);
			}
			return categories;
		}
		finally
		{
			DBUtils.close(conn, statement, rs);
		}
	}
	
	public long addLabel(ImageLabel label, String user) throws Exception
	{
		Connection conn = null;
		PreparedStatement statement = null;
		try
		{
			conn = DBUtils.getConnection();
			String sql = "insert into label_t(label_id_c, category_id_c, owner_c, public_c, storage_c, url_c) values(?,?,?,?,?,?)";
			statement = conn.prepareStatement(sql);
			if (label.getId() == -1)
			{
				label.setId(IDGenerator.generateID());
			}
			statement.setLong(1, label.getId());
			statement.setLong(2, label.getCategoryID());
			statement.setString(3, user);
			statement.setBoolean(4, user == null);
			statement.setShort(5, ImageLabel.STORAGE_FILE);
//			String path = Label.isLocal(label.getSrc()) ? ("images/" + FilenameUtils.getName(label.getSrc())) : label.getSrc();
			statement.setString(6, label.getSrc());
			statement.executeUpdate();
//			ResultSet newid = statement.getGeneratedKeys();
//			newid.next();
//			return newid.getInt(1);
			return label.getId();
		}
		finally
		{
			DBUtils.close(conn, statement, null);
		}
	}
	
	public String deleteLabel(ImageLabel label) throws Exception
	{
		Connection conn = null;
		PreparedStatement statement = null;
		try
		{
			conn = DBUtils.getConnection();
			String sql = "delete from label_t where label_id_c=?";
			statement = conn.prepareStatement(sql);
			statement.setLong(1, label.getId());
			statement.executeUpdate();
		}
		finally
		{
			DBUtils.close(conn, statement, null);
		}
		return null;
	}
	
	public List<ImageLabel> getLabels(long categoryID, String user) throws Exception
	{
		Connection conn = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		try
		{
			conn = DBUtils.getConnection();
			String sql = "select * from label_t where category_id_c=? and (public_c=? || owner_c=?)";
			statement = conn.prepareStatement(sql);
			statement.setLong(1, categoryID);
			statement.setBoolean(2, true);
			statement.setString(3, user);
			rs = statement.executeQuery();
			List<ImageLabel> labels = new ArrayList<ImageLabel>();
			while (rs.next())
			{
				ImageLabel label = new ImageLabel();
				label.setId(rs.getLong("label_id_c"));
				label.setCategoryID(categoryID);
				label.setSrc(rs.getString("url_c"));
				labels.add(label);
			}
			return labels;
		}
		finally
		{
			DBUtils.close(conn, statement, rs);
		}
	}
	
	public List<ImageLabel> getCustomLabels(String user) throws Exception
	{
		Connection conn = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		try
		{
			conn = DBUtils.getConnection();
			String sql = "select * from label_t where owner_c=?";
			statement = conn.prepareStatement(sql);
			statement.setString(1, user);
			rs = statement.executeQuery();
			List<ImageLabel> labels = new ArrayList<ImageLabel>();
			while (rs.next())
			{
				ImageLabel label = new ImageLabel();
				label.setId(rs.getLong("label_id_c"));
				label.setSrc(rs.getString("url_c"));
				labels.add(label);
			}
			return labels;
		}
		finally
		{
			DBUtils.close(conn, statement, rs);
		}
	}
	
	public String merging(Item item, String owner, String oldUrl, long timeLimit, Connection conn) throws Exception
	{
		deleteMergedItem(item.getNumIid(), conn);
		String sql = "insert into merged_item_t(num_iid_c, owner_c, old_pic_url_c, time_limit_c, action_c, status_c, title_c, price_c) " +
				"values(?,?,?,?,?,?,?,?)";
		PreparedStatement statement = conn.prepareStatement(sql);
		statement.setLong(1, item.getNumIid());
		statement.setString(2, owner);
		statement.setString(3, oldUrl);
		statement.setLong(4, timeLimit);
		statement.setShort(5, Item.ACTION_MERGE);
		statement.setShort(6, Item.STATUS_PENDING);
		statement.setString(7, item.getTitle());
		statement.setString(8, item.getPrice());
		statement.executeUpdate();
		statement.close();
		return null;
	}
	
	public String merged(long numIid, String newUrl, Date modified, short status, String msg, String errorCode, Connection conn) throws Exception
	{
		String sql = "update merged_item_t set new_pic_url_c=?, last_update_c=?, status_c=?, msg_c=?, error_code_c=? where num_iid_c=?";
		PreparedStatement statement = conn.prepareStatement(sql);
		statement.setString(1, newUrl);
		statement.setTimestamp(2, modified == null ? null : new Timestamp(modified.getTime()));
		statement.setShort(3, status);
		statement.setString(4, msg);
		statement.setString(5, errorCode);
		statement.setLong(6, numIid);
		statement.executeUpdate();
		statement.close();
		return null;
	}
	
	public String recovering(long numIid, Connection conn) throws Exception
	{
		String sql = "update merged_item_t set action_c=?, status_c=? where num_iid_c=?";
		PreparedStatement statement = conn.prepareStatement(sql);
		statement.setShort(1, Item.ACTION_RECOVER);
		statement.setShort(2, Item.STATUS_PENDING);
		statement.setLong(3, numIid);
		statement.executeUpdate();
		statement.close();
		return null;
	}
	
	public String unmerged(long numIid, boolean ok, String msg, Connection conn) throws Exception
	{
		if (ok)
		{
			String sql = "delete from merged_item_t where num_iid_c=?";
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setLong(1, numIid);
			statement.executeUpdate();
			statement.close();
		}
		else
		{
			String sql = "update merged_item_t set status_c=?, msg_c=? where num_iid_c=?";
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setShort(1, Item.STATUS_FAILED);
			statement.setString(2, msg);
			statement.setLong(3, numIid);
			statement.executeUpdate();
			statement.close();
		}
		return null;
	}
	
	public String deleteMergedItem(long numIid, Connection conn) throws Exception
	{
		String sql = "delete from merged_item_t where num_iid_c=?";
		PreparedStatement statement = conn.prepareStatement(sql);
		statement.setLong(1, numIid);
		statement.executeUpdate();
		statement.close();
		return null;
	}
	
	public Map<Long, com.tuqianyi.model.Item> getMergedItems(String user) throws Exception
	{
		Connection conn = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		try
		{
			conn = DBUtils.getConnection();
			String sql = "select * from merged_item_t where owner_c=?";
			statement = conn.prepareStatement(sql);
			statement.setString(1, user);
			rs = statement.executeQuery();
			Map<Long, Item> items = new HashMap<Long, Item>();
			while (rs.next())
			{
				Item item = new Item();
				item.setNumIid(rs.getLong("num_iid_c"));
				item.setOldPicUrl(rs.getString("old_pic_url_c"));
				item.setPicUrl(rs.getString("new_pic_url_c"));
				item.setStatus(rs.getShort("status_c"));
				item.setAction(rs.getShort("action_c"));
				item.setErrorMsg(rs.getString("msg_c"));
				item.setErrorCode(rs.getString("error_code_c"));
				items.put(item.getNumIid(), item);
			}
			return items;
		}
		finally
		{
			DBUtils.close(conn, statement, rs);
		}
	}
	
	public List<Long> getMergedItemIds(String user, Connection conn) throws Exception
	{
		PreparedStatement statement = null;
		ResultSet rs = null;
		try
		{
			String sql = "select * from merged_item_t where owner_c=?";
			statement = conn.prepareStatement(sql);
			statement.setString(1, user);
			rs = statement.executeQuery();
			List<Long> ids = new ArrayList<Long>();
			while (rs.next())
			{
				ids.add(rs.getLong("num_iid_c"));
			}
			return ids;
		}
		finally
		{
			DBUtils.close(null, statement, rs);
		}
	}
	
	public long getMergedItems(String user, short status, int offset, int limit, List<Item> container) throws Exception
	{
		Connection conn = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		try
		{
			conn = DBUtils.getConnection();
			String sql = "select * from merged_item_t where owner_c=? and status_c=?";
			if (offset != -1 && limit != -1)
			{
				sql = sql + " limit " + limit + " offset " + offset;
			}
			statement = conn.prepareStatement(sql);
			statement.setString(1, user);
			statement.setShort(2, status);
			rs = statement.executeQuery();
			while (rs.next())
			{
				Item item = new Item();
				item.setNumIid(rs.getLong("num_iid_c"));
				item.setOldPicUrl(rs.getString("old_pic_url_c"));
				item.setPicUrl(rs.getString("new_pic_url_c"));
				item.setStatus(rs.getShort("status_c"));
				item.setAction(rs.getShort("action_c"));
				item.setErrorMsg(rs.getString("msg_c"));
				item.setErrorCode(rs.getString("error_code_c"));
				item.setTitle(rs.getString("title_c"));
				item.setPrice(rs.getString("price_c"));
				container.add(item);
			}
			sql = "select count(*) from merged_item_t where owner_c=? and status_c=?";
			statement = conn.prepareStatement(sql);
			statement.setString(1, user);
			statement.setShort(2, status);
			rs = statement.executeQuery();
			rs.next();
			return rs.getLong(1);
		}
		finally
		{
			DBUtils.close(conn, statement, rs);
		}
	}
	
	public long getMergedItemsCount(String user, short status) throws Exception
	{
		Connection conn = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		try
		{
			conn = DBUtils.getConnection();
			String sql = "select count(*) from merged_item_t where owner_c=? and status_c=?";
			statement = conn.prepareStatement(sql);
			statement.setString(1, user);
			statement.setShort(2, status);
			rs = statement.executeQuery();
			rs.next();
			return rs.getLong(1);
		}
		finally
		{
			DBUtils.close(conn, statement, rs);
		}
	}
	
	public Map<Long, Item> getWorkingItems() throws Exception
	{
		Connection conn = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		try
		{
			conn = DBUtils.getConnection();
			String sql = "select * from merged_item_t where status_c<>? and status_c<>?";
			statement = conn.prepareStatement(sql);
			statement.setShort(1, Item.STATUS_FAILED);
			statement.setShort(2, Item.STATUS_OK);
			rs = statement.executeQuery();
			Map<Long, Item> items = new HashMap<Long, Item>();
			while (rs.next())
			{
				Item item = new Item();
				item.setNumIid(rs.getLong("num_iid_c"));
				item.setOldPicUrl(rs.getString("old_pic_url_c"));
				item.setPicUrl(rs.getString("new_pic_url_c"));
				item.setAction(rs.getShort("action_c"));
				items.put(item.getNumIid(), item);
			}
			return items;
		}
		finally
		{
			DBUtils.close(conn, statement, rs);
		}
	}
	
	public void addNotify(long userId, long leaseId, String nick, Date validateDate, Date invalidateDate, double factMoney, 
			long subscType, int version, int oldVersion, long status, Date gmtCreateDate) throws SQLException, NamingException
	{
		Connection conn = null;
		PreparedStatement statement = null;
		try
		{
			conn = DBUtils.getConnection();
			String sql = "insert into notify_t(user_id_c, lease_id_c, nick_c, validate_date_c, invalidate_date_c, " +
					"fact_money_c, subsc_type_c, version_no_c, old_version_no_c, status_c, gmt_create_date_c) " +
					"values(?,?,?,?,?,?,?,?,?,?,?)";
			statement = conn.prepareStatement(sql);
			statement.setLong(1, userId);
			statement.setLong(2, leaseId);
			statement.setString(3, nick);
			statement.setTimestamp(4, new Timestamp(validateDate.getTime()));
			statement.setTimestamp(5, new Timestamp(invalidateDate.getTime()));
			statement.setDouble(6, factMoney);
			statement.setLong(7, subscType);
			statement.setInt(8, version);
			statement.setInt(9, oldVersion);
			statement.setLong(10, status);
			statement.setTimestamp(11, new Timestamp(gmtCreateDate.getTime()));
			statement.executeUpdate();
		}
		finally
		{
			DBUtils.close(conn, statement, null);
		}
	}
	
	public void setShowNotice(boolean show) throws NamingException, SQLException
	{
		Connection conn = null;
		PreparedStatement statement = null;
		try
		{
			conn = DBUtils.getConnection();
			String sql = "update properties_t set value_c=? where name_c=?";
			statement = conn.prepareStatement(sql);
			statement.setString(1, String.valueOf(show));
			statement.setString(2, "showNotice");
			statement.executeUpdate();
		}
		finally
		{
			DBUtils.close(conn, statement, null);
		}
	}
	
	public boolean getShowNotice() throws NamingException, SQLException
	{
		Connection conn = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		try
		{
			conn = DBUtils.getConnection();
			String sql = "select value_c from properties_t where name_c=?";
			statement = conn.prepareStatement(sql);
			statement.setString(1, "showNotice");
			rs = statement.executeQuery();
			if (rs.next())
			{
				String s = rs.getString(1);
				return Boolean.valueOf(s);
			}
			return false;
		}
		finally
		{
			DBUtils.close(conn, statement, rs);
		}
	}
	
	public void updateUser(long uid, String nick, String session) throws NamingException, SQLException
	{
		Connection conn = null;
		PreparedStatement statement = null;
		try
		{
			conn = DBUtils.getConnection();
			String sql = "update user_t set session_c=?, last_login_c=? where user_id_c=?";
			statement = conn.prepareStatement(sql);
			statement.setString(1, session);
			statement.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
			statement.setLong(3, uid);
			int result = statement.executeUpdate();
			if (result == 0)
			{
				sql = "insert into user_t(user_id_c, nick_c, session_c, last_login_c) values(?,?,?,?)";
				statement = conn.prepareStatement(sql);
				statement.setLong(1, uid);
				statement.setString(2, nick);
				statement.setString(3, session);
				statement.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
			}
			statement.executeUpdate();
		}
		finally
		{
			DBUtils.close(conn, statement, null);
		}
	}
	
	public List<User> getUsers() throws NamingException, SQLException
	{
		Connection conn = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		try
		{
			conn = DBUtils.getConnection();
			String sql = "select * from user_t order by last_login_c limit 50 offset 0";
			statement = conn.prepareStatement(sql);
			rs = statement.executeQuery();
			List<User> users = new ArrayList<User>();
			while (rs.next())
			{
				User user = new User();
				user.setNick(rs.getString("nick_c"));
				user.setLastLogin(rs.getDate("last_login_c"));
				users.add(user);
			}
			return users;
		}
		finally
		{
			DBUtils.close(conn, statement, rs);
		}
	}
}
