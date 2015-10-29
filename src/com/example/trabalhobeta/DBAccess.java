package com.example.trabalhobeta;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBAccess extends SQLiteOpenHelper {

	public static final String DATABASE_NAME = "trabalhocm";
	private static final int DATABASE_VERSION = 65;
	private static final String TAG = "DBAdapter";

	private static SQLiteDatabase db;
	public static final String KEY_ID = "_id";

	private static DBAccess instance;

	public DBAccess(Context ctx) {
		super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
	}

	public DBAccess(Context ctx, String dbName, String sql, String tableName,
			int ver) {

		super(ctx, dbName, null, ver);
		Log.i(TAG, "Creating or opening database [ " + dbName + " ].");
	}

	public static DBAccess getInstance(Context ctx) {
		if (instance == null) {
			instance = new DBAccess(ctx);
			try {
				db = instance.getWritableDatabase();
				if (!db.isReadOnly()) {
					db.execSQL("PRAGMA foreign_keys = ON;");
				}
			} catch (SQLiteException se) {
				Log.e(TAG, "Cound not create and/or open the database [ "
						+ DATABASE_NAME
						+ " ] that will be used for reading and writing.", se);
			}
		}
		return instance;
	}

	public static DBAccess getInstance(Context ctx, String dbName, String sql,
			String tableName, int ver) {
		if (instance == null) {
			instance = new DBAccess(ctx, dbName, sql, tableName, ver);
			try {
				Log.i(TAG, "Creating or opening the database [ " + dbName
						+ " ].");
				db = instance.getWritableDatabase();
			} catch (SQLiteException se) {
				Log.e(TAG, "Cound not create and/or open the database [ "
						+ dbName
						+ " ] that will be used for reading and writing.", se);
			}
		}
		return instance;
	}

	public void close() {
		if (instance != null) {
			Log.i(TAG, "Closing the database [ " + DATABASE_NAME + " ].");
			db.close();
			instance = null;
		}
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.i(TAG, "Trying to create database table if it isn't existed.");
		try {
			db.execSQL(Tipo_PI.TABLE_CREATE);
			db.execSQL(PontosInteresse.TABLE_CREATE);
		} catch (SQLException se) {
			Log.e(TAG,
					"Could not create the database table according to the SQL statement ",
					se);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.i(TAG, "Upgrading database from version " + oldVersion + " to "
				+ newVersion + ", which will destroy all old data");
		try {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");
			db.execSQL("drop table if exists " + PontosInteresse.TABLE_NAME);
			db.execSQL("drop table if exists " + Tipo_PI.TABLE_NAME);
			onCreate(db);
		} catch (SQLException se) {
			Log.e(TAG, "Could not drop the database table ", se);
		}
	}

	protected void onDestroy() {
		close();
	}

	public void insereTipo(String descricao) {
		ContentValues cv = new ContentValues();
		cv.put(Tipo_PI.coluna_descricao, descricao);

		db.insert(Tipo_PI.TABLE_NAME, null, cv);

		close();
	}

	public long inserePontoInteresse(String descricao, double lat, double lon,
			int tipo, int favorito) {

		long id = -1;

		ContentValues cv = new ContentValues();
		cv.put(PontosInteresse.coluna_descricao, descricao);
		cv.put(PontosInteresse.coluna_lat, lat);
		cv.put(PontosInteresse.coluna_lon, lon);
		cv.put(PontosInteresse.coluna_tipo, tipo);
		cv.put(PontosInteresse.coluna_favorito, favorito);

		try {
			id = db.insert(PontosInteresse.TABLE_NAME, null, cv);
			return id;
		} catch (Exception e) {
			e.printStackTrace();
		}

		close();
		return id;
	}

	public void editaPontoInteresse(String descricao, long id, int tipo,
			int favorito) {

		ContentValues cv = new ContentValues();
		cv.put(PontosInteresse.coluna_descricao, descricao);
		cv.put(PontosInteresse.coluna_tipo, tipo);
		cv.put(PontosInteresse.coluna_favorito, favorito);

		try {
			db.update(PontosInteresse.TABLE_NAME, cv, PontosInteresse.coluna_id
					+ "=?", new String[] { "" + id });
		} catch (Exception e) {
			e.printStackTrace();
		}

		close();
	}

	public Cursor getTipos() {
		String[] columns = new String[] { Tipo_PI.coluna_id,
				Tipo_PI.coluna_descricao };
		Cursor c = db.query(Tipo_PI.TABLE_NAME, columns, null, null, null,
				null, null);
		return c;
	}

	public Cursor getPI() {
		String[] columns = new String[] { PontosInteresse.coluna_id,
				PontosInteresse.coluna_descricao, PontosInteresse.coluna_tipo,
				PontosInteresse.coluna_lat, PontosInteresse.coluna_lon,
				PontosInteresse.coluna_favorito };
		Cursor c = db.query(PontosInteresse.TABLE_NAME, columns, null, null,
				null, null, null);
		return c;
	}

	public Cursor getPIPorTipo(int tipo) {
		String[] columns = new String[] { PontosInteresse.coluna_id,
				PontosInteresse.coluna_descricao, PontosInteresse.coluna_tipo,
				PontosInteresse.coluna_lat, PontosInteresse.coluna_lon,
				PontosInteresse.coluna_favorito };
		Cursor c = db.query(PontosInteresse.TABLE_NAME, columns,
				PontosInteresse.coluna_tipo + "=?", new String[] { "" + tipo },
				null, null, null);
		return c;
	}

	public void editarTipo(String nome_antigo, String nome_novo) {
		ContentValues cv = new ContentValues();
		cv.put(Tipo_PI.coluna_descricao, nome_novo);
		db.update(Tipo_PI.TABLE_NAME, cv, Tipo_PI.coluna_descricao + "=?",
				new String[] { nome_antigo });
		close();
	}

	public void deleteTipo(long id) {
		try {
			db.delete(PontosInteresse.TABLE_NAME, PontosInteresse.coluna_tipo
					+ "=?", new String[] { "" + id });
			db.delete(Tipo_PI.TABLE_NAME, Tipo_PI.coluna_id + "=?",
					new String[] { "" + id });
		} catch (Exception e) {
			e.printStackTrace();
		}

		close();
	}

	public void deletePI(int id) {

		db.delete(PontosInteresse.TABLE_NAME, PontosInteresse.coluna_id + "=?",
				new String[] { "" + id });
		close();
	}

	public String devolveTipo(String id) {
		String[] columns = new String[] { Tipo_PI.coluna_id,
				Tipo_PI.coluna_descricao };
		String[] lol = { "" + id };
		Cursor c = db.query(Tipo_PI.TABLE_NAME, columns, Tipo_PI.coluna_id
				+ "=?", lol, null, null, null);
		if (c.getCount() > 0) {
			c.moveToFirst();
			return c.getString(c.getColumnIndex(Tipo_PI.coluna_descricao));
		}
		return null;
	}

	public long getFavoritoById(long id) {
		String[] columns = new String[] { PontosInteresse.coluna_favorito };
		String[] lol = { "" + id };
		Cursor c = db.query(PontosInteresse.TABLE_NAME, columns,
				PontosInteresse.coluna_id + "=?", lol, null, null, null);
		if (c.getCount() > 0) {
			c.moveToFirst();
			return c.getLong(0);
		}
		return -1;
	}

	public List<String> devolveCamposPI(String id) {
		String[] columns = new String[] { PontosInteresse.coluna_id,
				PontosInteresse.coluna_descricao, PontosInteresse.coluna_tipo,
				PontosInteresse.coluna_lat, PontosInteresse.coluna_lon,
				PontosInteresse.coluna_favorito };
		String[] lol = { "" + id };
		List<String> lista = new ArrayList<String>();
		Cursor c = db.query(PontosInteresse.TABLE_NAME, columns,
				PontosInteresse.coluna_id + "=?", lol, null, null, null);
		if (c.getCount() > 0) {
			c.moveToFirst();
			lista.add(c.getString(c
					.getColumnIndex(PontosInteresse.coluna_descricao)));
			lista.add(c.getString(c.getColumnIndex(PontosInteresse.coluna_tipo)));
			lista.add(c.getString(c.getColumnIndex(PontosInteresse.coluna_lat)));
			lista.add(c.getString(c.getColumnIndex(PontosInteresse.coluna_lon)));
			lista.add(c.getString(c
					.getColumnIndex(PontosInteresse.coluna_favorito)));
			return lista;
		}
		return null;
	}

	public ArrayList<PontosInteresse> getAllPIs(Cursor c) {
		ArrayList<PontosInteresse> itemList = new ArrayList<PontosInteresse>();
		try {
			if (c.moveToFirst()) {
				do {
					itemList.add(new PontosInteresse(Long.parseLong(c
							.getString(0)), c.getString(1), Long.parseLong(c
							.getString(2)), Double.parseDouble(c.getString(3)),
							Double.parseDouble(c.getString(4)), Long
									.parseLong(c.getString(5))));
				} while (c.moveToNext());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return itemList;
	}

	/**
	 * Getting all labels returns list of labels
	 * */
	public List<String> getAllLabels() {
		List<String> labels = new ArrayList<String>();

		String[] columns = new String[] { Tipo_PI.coluna_id,
				Tipo_PI.coluna_descricao };

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(Tipo_PI.TABLE_NAME, columns, null, null, null,
				null, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				labels.add(cursor.getString(1));
			} while (cursor.moveToNext());
		}

		// closing connection
		cursor.close();
		db.close();

		// returning lables
		return labels;
	}
}
