package com.example.trabalhobeta;

import android.os.Bundle;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class ListarTipoPI extends ListActivity {
	private SimpleCursorAdapter adapter;
	private long position;
	private Context context = this;

	public void toastEliminado() {
		Toast.makeText(this, "Eliminado com sucesso!" /* + position */,
				Toast.LENGTH_LONG).show();
	}

	private void preencherLista() {
		try {
			DBAccess genadapter = DBAccess.getInstance(getApplicationContext());
			Cursor cursor = genadapter.getTipos();
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listtipopi);
		preencherLista();

		ListView lv = getListView();
		registerForContextMenu(lv);

		Button buttonAdd = (Button) findViewById(R.id.add);

		// add button listener
		buttonAdd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				// custom dialog
				final Dialog dialog = new Dialog(context);
				dialog.setContentView(R.layout.custom_dialog_tipopi);
				dialog.setTitle("Adicionar Tipo de PI");

				// set the custom dialog components - text, image and button
				final EditText text = (EditText) dialog
						.findViewById(R.id.txtTipo);

				final Button btnAdd2 = (Button) dialog.findViewById(R.id.addTipo);
				btnAdd2.setEnabled(false);
				
				
				text.addTextChangedListener(new TextWatcher() {
					
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
					           btnAdd2.setEnabled(false);
					        }
					        else {
					        	btnAdd2.setEnabled(true);
					        }
						
					}
				});
				
				// if button is clicked, close the custom dialog
				btnAdd2.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						DBAccess genadapter = DBAccess
								.getInstance(getApplicationContext());
						genadapter.insereTipo(text.getText().toString());
						preencherLista();
						dialog.dismiss();
					}
				});

				Button btnCancel = (Button) dialog
						.findViewById(R.id.cancelTipo);
				// if button is clicked, close the custom dialog
				btnCancel.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.cancel();
					}
				});

				dialog.show();
			}
		});

		final Button btnBack = (Button) findViewById(R.id.back);
		btnBack.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(ListarTipoPI.this, MainActivity.class);
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
		Cursor cursor = dbac.getTipos();
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

		menu.setHeaderTitle("Escolha uma opção");
		menu.add(0, v.getId(), 0, "Editar");
		menu.add(0, v.getId(), 0, "Eliminar");
	}

	@Override
	public boolean onContextItemSelected(final MenuItem item) {
		if (item.getTitle() == "Editar") {
			final AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
					.getMenuInfo();
			final Dialog dialog = new Dialog(context);
			dialog.setContentView(R.layout.custom_dialog_tipopi);
			dialog.setTitle("Editar Tipo de PI");

			// set the custom dialog components - text, image and button
			final EditText text = (EditText) dialog.findViewById(R.id.txtTipo);

			
			
			DBAccess genadapter = DBAccess
					.getInstance(getApplicationContext());
			
			text.setText(genadapter.devolveTipo(""+info.id));
			
			final Button btnAdd2 = (Button) dialog.findViewById(R.id.addTipo);
			
			btnAdd2.setEnabled(false);
			
			
			text.addTextChangedListener(new TextWatcher() {
				
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
				           btnAdd2.setEnabled(false);
				        }
				        else {
				        	btnAdd2.setEnabled(true);
				        }
					
				}
			});
			
			btnAdd2.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					DBAccess genadapter = DBAccess
							.getInstance(getApplicationContext());
					genadapter.editarTipo(genadapter.devolveTipo(""+info.id), text.getText().toString());
					preencherLista();
					dialog.dismiss();
				}
			});

			Button btnCancel = (Button) dialog.findViewById(R.id.cancelTipo);
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
					.setTitle("Confirmar Eliminação")
					.setMessage("Deseja Eliminar este Tipo de PI?")
					.setPositiveButton("Sim",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(
										DialogInterface dialogInterface, int i) {
									DBAccess genadapter = DBAccess
											.getInstance(getApplicationContext());

									AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
											.getMenuInfo();
									position = info.id;
									// this.adapter.notifyDataSetChanged();
									genadapter.deleteTipo(position);
									toastEliminado();
									genadapter.close();
									preencherLista();
								}
							})
					.setNegativeButton("Não",
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
		Intent i = new Intent(ListarTipoPI.this, MainActivity.class);
		startActivity(i);
		finish();
	}
}
