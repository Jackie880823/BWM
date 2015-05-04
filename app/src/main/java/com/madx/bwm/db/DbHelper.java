package com.madx.bwm.db;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;

public class DbHelper<T> {

	public int count(Class<T> c) {
		SQLiteHelperOrm db = new SQLiteHelperOrm();
		try {
			Dao<T, Long> dao = db.getDao(c);
			return (int) dao.countOf();
		} catch (SQLException e) {
			Log.e("DbHelper", "count", e);
		} finally {
			if (db != null)
				db.close();
		}
		return 0;
	}

	public int create(T po) {
		SQLiteHelperOrm db = new SQLiteHelperOrm();
		try {
			Dao<T, Long> dao = db.getDao((Class<T>) po.getClass());
			return dao.create(po);
		} catch (SQLException e) {
			Log.e("DbHelper", "create", e);
		} finally {
			if (db != null)
				db.close();
		}
		return -1;
	}

	public int createOrUpdate(T po) {
		SQLiteHelperOrm db = new SQLiteHelperOrm();
		try {
			Dao dao = db.getDao(po.getClass());
			return dao.createOrUpdate(po).getNumLinesChanged();
		} catch (SQLException e) {
			Log.e("DbHelper", "createOrUpdate", e);
		} finally {
			if (db != null)
				db.close();
		}
		return -1;
	}

	public int remove(T po) {
		SQLiteHelperOrm db = new SQLiteHelperOrm();
		try {
			Dao<T, Long> dao = db.getDao((Class<T>) po.getClass());
			return dao.delete(po);
		} catch (SQLException e) {
			Log.e("DbHelper", "remove", e);
		} finally {
			if (db != null)
				db.close();
		}
		return -1;
	}

	public int remove(Collection<T> po) {
		SQLiteHelperOrm db = new SQLiteHelperOrm();
		try {
			Dao dao = db.getDao(po.getClass());
			return dao.delete(po);
		} catch (SQLException e) {
			Log.e("DbHelper", "remove", e);
		} finally {
			if (db != null)
				db.close();
		}
		return -1;
	}

	/**
	 * 根据特定条件更新特定字段
	 * 
	 * @param c
	 * @param values
	 * @param columnName
	 *            where字段
	 * @param value
	 *            where值
	 * @return
	 */
	public int update(Class<T> c, HashMap<String, Object> values,
			String columnName, Object value) {
		SQLiteHelperOrm db = new SQLiteHelperOrm();
		try {
			Dao<T, Long> dao = db.getDao(c);
			UpdateBuilder<T, Long> updateBuilder = dao.updateBuilder();
			updateBuilder.where().eq(columnName, value);
			for (String key : values.keySet()) {
				updateBuilder.updateColumnValue(key, values.get(key));
			}
			return updateBuilder.update();
		} catch (SQLException e) {
			Log.e("DbHelper", "update", e);
		} finally {
			if (db != null)
				db.close();
		}
		return -1;
	}

	public int update(T po) {
		SQLiteHelperOrm db = new SQLiteHelperOrm();
		try {
			Dao<T, Long> dao = db.getDao((Class<T>) po.getClass());
			return dao.update(po);
		} catch (SQLException e) {
			Log.e("DbHelper", "update", e);
		} finally {
			if (db != null)
				db.close();
		}
		return -1;
	}

	public List<T> queryForAll(Class<T> c) {
		SQLiteHelperOrm db = new SQLiteHelperOrm();
		try {
			Dao<T, Long> dao = db.getDao(c);
			return dao.queryForAll();
		} catch (SQLException e) {
			Log.e("DbHelper", "queryForAll", e);
		} finally {
			if (db != null)
				db.close();
		}
		return new ArrayList<T>();
	}

	public List<T> queryForAllOrderby(Class<T> c, String orderFieldName) {
		return queryForAllOrderby(c, orderFieldName, false);
	}

	public List<T> queryForAllOrderby(Class<T> c, String orderFieldName,
			boolean asc) {
		SQLiteHelperOrm db = new SQLiteHelperOrm();
		try {
			Dao<T, Long> dao = db.getDao(c);
			QueryBuilder<T, Long> query = dao.queryBuilder();
			query.orderBy(orderFieldName, asc);
			return dao.query(query.prepare());
		} catch (SQLException e) {
			Log.e("DbHelper", "queryForAllOrderby", e);
		} finally {
			if (db != null)
				db.close();
		}
		return new ArrayList<T>();
	}

	public List<T> queryForAllOrderby(Class<T> c, String fieldName,
			Object value, String orderFieldName, boolean asc) {
		SQLiteHelperOrm db = new SQLiteHelperOrm();
		try {
			Dao<T, Long> dao = db.getDao(c);
			QueryBuilder<T, Long> query = dao.queryBuilder();
			query.orderBy(orderFieldName, asc);
			query.where().eq(fieldName, value);
			return dao.query(query.prepare());
		} catch (SQLException e) {
			Log.e("DbHelper", "queryForAllOrderby", e);
		} finally {
			if (db != null)
				db.close();
		}
		return new ArrayList<T>();
	}

	public List<T> queryForAll(Class<T> c, String fieldName, Object value) {
		SQLiteHelperOrm db = new SQLiteHelperOrm();
		try {
			Dao<T, Long> dao = db.getDao(c);
			return dao.queryForEq(fieldName, value);
		} catch (SQLException e) {
			Log.e("DbHelper", "queryForAll", e);
		} finally {
			if (db != null)
				db.close();
		}
		return new ArrayList<T>();
	}

	/**  */
	public T query(Class<T> c, String fieldName, Object value) {
		SQLiteHelperOrm db = new SQLiteHelperOrm();
		try {
			Dao<T, Long> dao = db.getDao(c);
			List<T> result = dao.queryForEq(fieldName, value);
			if (result != null && result.size() > 0)
				return result.get(0);
		} catch (SQLException e) {
			Log.e("DbHelper", "count", e);
		} finally {
			if (db != null)
				db.close();
		}
		return null;
	}

	public T query(Class<T> c, long id) {
		SQLiteHelperOrm db = new SQLiteHelperOrm();
		try {
			Dao<T, Long> dao = db.getDao(c);
			return dao.queryForId(id);
		} catch (SQLException e) {
			Log.e("DbHelper", "query", e);
		} finally {
			if (db != null)
				db.close();
		}
		return null;
	}
}
