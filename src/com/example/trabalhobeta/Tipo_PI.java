package com.example.trabalhobeta;

public class Tipo_PI {
	public static String TABLE_NAME = "tipo_pi";
	public static String coluna_id = "_id";
	public static String coluna_descricao = "descricao";

	public static String TABLE_CREATE = "create table " + TABLE_NAME + " ("
			+ coluna_id + " integer primary key AUTOINCREMENT,"
			+ coluna_descricao + " text NOT NULL"
			+ ");";
	
	
	private long id;
	private String descricao;
	
	public Tipo_PI(long id, String descricao) {
		super();
		setId(id);
		setDescricao(descricao);
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	

}
