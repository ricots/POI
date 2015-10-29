package com.example.trabalhobeta;

public class PontosInteresse {
	public static String TABLE_NAME = "pontos_interesse";
	public static String coluna_id = "_id";
	public static String coluna_descricao = "descricao";
	public static String coluna_tipo = "tipo";
	public static String coluna_lat = "lat";
	public static String coluna_lon = "lon";
	public static String coluna_favorito = "favorito";

	public static String TABLE_CREATE = "create table " + TABLE_NAME + "("
			+ coluna_id + " integer primary key AUTOINCREMENT,"
			+ coluna_descricao + " text NOT NULL," + "lat double NOT NULL,"
			+ "lon double NOT NULL," 
			+ coluna_favorito + " integer NOT NULL,"
			+ coluna_tipo + " integer NOT NULL REFERENCES " + Tipo_PI.TABLE_NAME + "(" + Tipo_PI.coluna_id + ") ON DELETE CASCADE"
			+ ");";
	
	public PontosInteresse(long id,String descricao,long tipo, double lat, double lon, long favorito) {
		setId(id);
		setDescricao(descricao);
		setTipo(tipo);
		setLat(lat);
		setLon(lon);
		setFavorito(favorito);
	}
	
	private long id;
	private String descricao;
	private long tipo;
	private double lat;
	private double lon;
	private long favorito;
	
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
	public long getTipo() {
		return tipo;
	}
	public void setTipo(long tipo) {
		this.tipo = tipo;
	}
	public double getLat() {
		return lat;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}
	public double getLon() {
		return lon;
	}
	public void setLon(double lon) {
		this.lon = lon;
	}
	public long getFavorito() {
		return favorito;
	}
	public void setFavorito(long favorito) {
		this.favorito = favorito;
	}
}
