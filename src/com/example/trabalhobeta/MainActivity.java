package com.example.trabalhobeta;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.os.Process;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		final Button btn1 = (Button) findViewById(R.id.pontos_interesse);
		btn1.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(MainActivity.this, ListarPontosInteresse.class);
				startActivity(i);
				finish();
			}
		});
		
		final Button btn2 = (Button) findViewById(R.id.mapa);
		btn2.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(MainActivity.this, Mapa.class);
				startActivity(i);
				finish();
			}
		});
		
		final Button btn3 = (Button) findViewById(R.id.categorias);
		btn3.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(MainActivity.this, ListarTipoPI.class);
				startActivity(i);
				finish();
			}
		});

		final Button btn4 = (Button) findViewById(R.id.sair);
		btn4.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Process.killProcess(Process.myPid());
			}
		});
		
		final Button btnweb = (Button) findViewById(R.id.btnwebservice);
		btnweb.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(MainActivity.this, TesteWebService.class);
				startActivity(i);
				finish();
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.menu_exit:
			Process.killProcess(Process.myPid());
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public void onBackPressed() {
	    new AlertDialog.Builder(this)
	        .setTitle("Sair?")
	        .setMessage("Tem a certeza que deseja sair?")
	        .setNegativeButton("Não", null)
	        .setPositiveButton("Sim", new OnClickListener() {

	            public void onClick(DialogInterface arg0, int arg1) {
	                MainActivity.super.onBackPressed();
	            }
	        }).create().show();
	}

}
