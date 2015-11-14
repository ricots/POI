package com.example.zecoxao.poi;

import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class ListarPontosInteresse extends ListActivity {
	private SimpleCursorAdapter adapter;
	private long pos;
	private Context context = this;
	private Spinner spinner;
	private Spinner spinner1;
	private int ifavorito;
	private CheckBox favorito;
	private DBAccess db;

	public int getFavorito() {
		if (favorito.isChecked()) {
			ifavorito = 0;
		} else {
			ifavorito = 1;
		}
		return ifavorito;
	}

	public void setFavorito() {
		if (ifavorito == 0) {
			favorito.setChecked(true);
		} else {
			favorito.setChecked(false);
		}

	}

	public void toastEliminado() {
		Toast.makeText(this, "Eliminado com sucesso!" /* + position */,
				Toast.LENGTH_LONG).show();
	}

	private void preencherLista() {
		try {
			DBAccess genadapter = DBAccess.getInstance(getApplicationContext());
			Cursor cursor = genadapter.getPIPorTipo((int) pos);
			String[] from = new String[] { "descricao" };
			int[] to = new int[] { android.R.id.text1 };
			adapter = new SimpleCursorAdapter(this,
					android.R.layout.simple_list_item_1, cursor, from, to,
					SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			setListAdapter(adapter);
			genadapter.close();
		} catch (Exception ex) {
			throw new Error(ex.getMessage());
		}
	}

	/**
	 * Function to load the spinner data from SQLite database
	 * */
	private void loadSpinnerData(Spinner s) {
		String[] from = new String[] { "descricao" };
		int[] to = new int[] { android.R.id.text1 };

		// Spinner Drop down elements
		Cursor c = db.getTipos();

		// Creating adapter for spinner
		SimpleCursorAdapter dataAdapter = new SimpleCursorAdapter(this,
				android.R.layout.simple_list_item_1, c, from, to,
				SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

		// Drop down layout style - list view with radio button
		dataAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		// attaching data adapter to spinner
		s.setAdapter(dataAdapter);
	}

	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listpi);
		preencherLista();

		ListView lv = getListView();
		registerForContextMenu(lv);

		spinner = (Spinner) findViewById(R.id.spinner);

		// Spinner click listener
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				pos = id;
				preencherLista();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});

		db = new DBAccess(this);
		DBAccess.getInstance(this);

		// Loading spinner data from database
		loadSpinnerData(spinner);

		Button buttonAdd = (Button) findViewById(R.id.add);
		
		

		// add button listener
		buttonAdd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				DBAccess genadapter = DBAccess
						.getInstance(getApplicationContext());

				if (genadapter.getTipos().getCount() == 0) {
					Toast.makeText(
							getApplicationContext(),
							"Insira uma Categoria antes de adicionar um Ponto!",
							Toast.LENGTH_LONG).show();
					Intent i = new Intent(ListarPontosInteresse.this,
							ListarTipoPI.class);
					startActivity(i);
					finish();

				} else {
					Intent i = new Intent(ListarPontosInteresse.this,
							Mapa.class);
					startActivity(i);
					finish();
				}

			}
		});

		final Button btnBack = (Button) findViewById(R.id.back);
		btnBack.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(ListarPontosInteresse.this,
						MainActivity.class);
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
	protected void onResume() {
		super.onResume();
		DBAccess dbac = DBAccess.getInstance(getApplicationContext());
		Cursor cursor = dbac.getPI();
		adapter.changeCursor(cursor);
		preencherLista();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		DBAccess.getInstance(getApplicationContext()).close();
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		menu.setHeaderTitle("Escolha uma op��o");
		menu.add(0, v.getId(), 0, "Editar");
		menu.add(0, v.getId(), 0, "Eliminar");
	}

	@Override
	public boolean onContextItemSelected(final MenuItem item) {
		if (item.getTitle() == "Editar") {
			final AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
					.getMenuInfo();
			final Dialog dialog = new Dialog(context);
			dialog.setContentView(R.layout.custom_dialog_pi);
			dialog.setTitle("Editar Ponto de Interesse");

			// set the custom dialog components - text, image and button
			final EditText txtdescricao = (EditText) dialog
					.findViewById(R.id.txtPIdescricao);
			spinner1 = (Spinner) dialog.findViewById(R.id.spinnertipopi);
			final EditText txtlat = (EditText) dialog
					.findViewById(R.id.txtPIlat);
			final EditText txtlon = (EditText) dialog
					.findViewById(R.id.txtPIlon);
			txtlat.setEnabled(false);
			txtlon.setEnabled(false);

			favorito = (CheckBox) dialog.findViewById(R.id.favorite);

			spinner1.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> parent, View view,
						int position, long id) {

				}

				@Override
				public void onNothingSelected(AdapterView<?> parent) {

				}
			});

			db = new DBAccess(this);
			DBAccess.getInstance(this);

			// Loading spinner data from database
			loadSpinnerData(spinner1);

			DBAccess genadapter = DBAccess.getInstance(getApplicationContext());

			List<String> lista = genadapter.devolveCamposPI("" + info.id);

			txtdescricao.setText(lista.get(0));
			txtlat.setText(lista.get(2));
			txtlon.setText(lista.get(3));
			ifavorito = Integer.parseInt(lista.get(4));
			setFavorito();

			final Button btnEdit = (Button) dialog.findViewById(R.id.addeditPI);
			
			btnEdit.setEnabled(false);
			
			
			txtdescricao.addTextChangedListener(new TextWatcher() {
				
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count,
						int after) {
					
				}
				
				@Override
				public void afterTextChanged(Editable s) {
					 if (s == null || s.length() == 0) {
				           btnEdit.setEnabled(false);
				        }
				        else {
				        	btnEdit.setEnabled(true);
				        }
					
				}
			});

			btnEdit.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {

					DBAccess genadapter = DBAccess
							.getInstance(getApplicationContext());

					genadapter.editaPontoInteresse(txtdescricao.getText()
							.toString(), info.id, (int) spinner1
							.getSelectedItemId(), getFavorito());
					genadapter.close();
					preencherLista();
					dialog.dismiss();
				}
			});

			Button btnCancel = (Button) dialog.findViewById(R.id.cancelPI);
			// if button is clicked, close the custom dialog
			btnCancel.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.cancel();
				}
			});

			dialog.show();

			return true;
		} else if (item.getTitle() == "Eliminar") {
			new AlertDialog.Builder(this)
					.setTitle("Confirmar Elimina��o")
					.setMessage("Deseja apagar este ponto de interesse?")
					.setPositiveButton("Sim",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(
										DialogInterface dialogInterface, int i) {
									DBAccess genadapter = DBAccess
											.getInstance(getApplicationContext());

									AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
											.getMenuInfo();
									// this.adapter.notifyDataSetChanged();
									genadapter.deletePI((int) info.id);
									toastEliminado();
									genadapter.close();
									preencherLista();
								}
							})
					.setNegativeButton("N�o",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(
										DialogInterface dialogInterface, int i) {
									dialogInterface.cancel();
								}
							}) // don't need to do anything but dismiss here
					.create().show();
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void onBackPressed() {
		Intent i = new Intent(ListarPontosInteresse.this, MainActivity.class);
		startActivity(i);
		finish();
	}
}
