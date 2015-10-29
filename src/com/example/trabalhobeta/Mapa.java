package com.example.trabalhobeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.AlertDialog.Builder;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.*;
import android.location.*;
import android.net.Uri;
import android.os.*;
import android.provider.Settings;
import android.support.v4.app.*;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.model.*;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;

public class Mapa extends FragmentActivity implements OnMapLongClickListener,
		OnItemSelectedListener, OnMarkerClickListener, LocationListener,
		OnMyLocationChangeListener {
	private GoogleMap map;
	private Spinner spinner1;
	private DBAccess db;
	private Context ctx = this;
	private Polyline newPolyline;
	private LatLngBounds latlngBounds;
	private LatLng p1 = null;
	private LatLng p2 = null;
	private int ifavorito;
	private CheckBox favorito;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mapa);

		map = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map)).getMap();
		if (map == null) {
			Toast.makeText(this, "Google Maps not available", Toast.LENGTH_LONG)
					.show();
		}

		map.setMyLocationEnabled(true);
		map.setOnMapLongClickListener(this);
		map.setOnMarkerClickListener(this);
		map.setOnMyLocationChangeListener(this);

		db = new DBAccess(ctx);
		DBAccess.getInstance(ctx);

		setMarkersTest();

		Button Navegar = (Button) findViewById(R.id.navigate);
		Navegar.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (p1 == null && p2 == null) {
					Toast.makeText(getApplicationContext(),
							"Marque os pontos primeiro!", Toast.LENGTH_LONG)
							.show();

				} else if (p1 != null && p2 != null) {
					findDirections(p1.latitude, p1.longitude, p2.latitude,
							p2.longitude, GMapV2Direction.MODE_DRIVING);

				} else if (p1 != null && p2 == null) {
					Location location = map.getMyLocation();
					findDirections(p1.latitude, p1.longitude,
							location.getLatitude(), location.getLongitude(),
							GMapV2Direction.MODE_DRIVING);
				}
			}
		});
	}

	public void setMarkersTest() {
		if (!db.getAllPIs(db.getPI()).isEmpty()) {
			for (PontosInteresse pi : db.getAllPIs(db.getPI())) {
				if (pi.getFavorito() == 0) {
					map.addMarker(new MarkerOptions()
							.position(new LatLng(pi.getLat(), pi.getLon()))
							.title(pi.getDescricao())
							.snippet("" + pi.getId())
							.icon(BitmapDescriptorFactory
									.fromResource(R.drawable.star)));
				} else if (pi.getFavorito() == 1) {
					map.addMarker(new MarkerOptions()
							.position(new LatLng(pi.getLat(), pi.getLon()))
							.title(pi.getDescricao())
							.snippet("" + pi.getId())
							.icon(BitmapDescriptorFactory
									.fromResource(R.drawable.regular)));
				}
			}
		}
	}

	public void setMarkersTestByCat(int tipo) {
		map.clear();
		if (!db.getAllPIs(db.getPIPorTipo(tipo)).isEmpty()) {
			for (PontosInteresse pi : db.getAllPIs(db.getPIPorTipo(tipo))) {
				if (pi.getFavorito() == 0) {
					map.addMarker(new MarkerOptions()
							.position(new LatLng(pi.getLat(), pi.getLon()))
							.title(pi.getDescricao())
							.snippet("" + pi.getId())
							.icon(BitmapDescriptorFactory
									.fromResource(R.drawable.star)));
				} else if (pi.getFavorito() == 1) {
					map.addMarker(new MarkerOptions()
							.position(new LatLng(pi.getLat(), pi.getLon()))
							.title(pi.getDescricao())
							.snippet("" + pi.getId())
							.icon(BitmapDescriptorFactory
									.fromResource(R.drawable.regular)));
				}
			}
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		map.animateCamera(CameraUpdateFactory.newLatLngZoom(
				new LatLng(location.getLatitude(), location.getLongitude()), 15));

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_mapa, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

		case R.id.menu_sethybrid:
			if (map.getMapType() == GoogleMap.MAP_TYPE_NORMAL) {
				map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
			} else {
				map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
			}
			break;
		case R.id.menu_getmarkercat:
			getMarkerCat();
			break;
		case R.id.menu_showAll:
			setMarkersTest();
			break;
		}
		return true;
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

	public int getFavorito() {
		if (favorito.isChecked()) {
			ifavorito = 0;
		} else {
			ifavorito = 1;
		}
		return ifavorito;
	}

	public void setFavorito(Marker arg0) {
		if (db.getFavoritoById(Long.parseLong(arg0.getSnippet())) == 0) {
			favorito.setChecked(true);
		} else {
			favorito.setChecked(false);
		}

	}

	@Override
	public void onMapLongClick(LatLng arg0) {
		DBAccess genadapter = DBAccess.getInstance(getApplicationContext());

		if (genadapter.getTipos().getCount() == 0) {
			Toast.makeText(getApplicationContext(),
					"Insira uma Categoria antes de adicionar um Ponto!",
					Toast.LENGTH_LONG).show();
			Intent i = new Intent(Mapa.this, ListarTipoPI.class);
			startActivity(i);
			finish();
		} else {
			final Dialog dialog = new Dialog(this);
			dialog.setContentView(R.layout.custom_dialog_pi);
			dialog.setTitle("Adicionar PI");
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

			spinner1.setOnItemSelectedListener(this);

			db = new DBAccess(ctx);
			DBAccess.getInstance(ctx);

			// Loading spinner data from database
			loadSpinnerData(spinner1);

			txtlat.setText(("" + arg0.latitude).substring(0, 7));
			txtlon.setText(("" + arg0.longitude).substring(0, 7));

			final Button btnAdd = (Button) dialog.findViewById(R.id.addeditPI);

			btnAdd.setEnabled(false);

			txtdescricao.addTextChangedListener(new TextWatcher() {

				@Override
				public void onTextChanged(CharSequence s, int start,
						int before, int count) {
					// TODO Auto-generated method stub

				}

				@Override
				public void beforeTextChanged(CharSequence s, int start,
						int count, int after) {

				}

				@Override
				public void afterTextChanged(Editable s) {
					if (s == null || s.length() == 0) {
						btnAdd.setEnabled(false);
					} else {
						btnAdd.setEnabled(true);
					}

				}
			});

			btnAdd.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					LatLng spot = new LatLng(Double.parseDouble(txtlat
							.getText().toString()), Double.parseDouble(txtlon
							.getText().toString()));

					DBAccess genadapter = DBAccess
							.getInstance(getApplicationContext());

					long id = genadapter.inserePontoInteresse(txtdescricao
							.getText().toString(), spot.latitude,
							spot.longitude, (int) spinner1.getSelectedItemId(),
							getFavorito());
					if (getFavorito() == 1)
						map.addMarker(new MarkerOptions()
								.position(spot)
								.title(txtdescricao.getText().toString())
								.snippet("" + id)
								.icon(BitmapDescriptorFactory
										.fromResource(R.drawable.regular)));
					else if (getFavorito() == 0)
						map.addMarker(new MarkerOptions()
								.position(spot)
								.title(txtdescricao.getText().toString())
								.snippet("" + id)
								.icon(BitmapDescriptorFactory
										.fromResource(R.drawable.star)));

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

		}

	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {

	}

	@Override
	public void onBackPressed() {
		Intent i = new Intent(Mapa.this, MainActivity.class);
		startActivity(i);
		finish();
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {

	}

	@Override
	public boolean onMarkerClick(final Marker arg0) {
		new AlertDialog.Builder(this)
				.setTitle("Editar/Eliminar PI")
				.setMessage("Escolha uma opção")
				.setPositiveButton("Eliminar",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.cancel();
								eliminarBuilder(arg0).show();
							}
						})
				.setNeutralButton("Editar",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.cancel();
								editarDialog(arg0).show();
							}
						})
				.setNegativeButton("Marcar",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								verificaMarkers(arg0);
								dialog.cancel();

							}
						}).create().show();
		return true;
	}

	public Builder eliminarBuilder(final Marker arg0) {
		Builder builder = new AlertDialog.Builder(this)
				.setTitle("Confirmar Eliminação")
				.setMessage("Deseja apagar este ponto de interesse?")
				.setPositiveButton("Sim",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(
									DialogInterface dialogInterface, int i) {
								DBAccess genadapter = DBAccess
										.getInstance(getApplicationContext());
								genadapter.deletePI(Integer.parseInt(arg0
										.getSnippet()));
								genadapter.close();
								arg0.remove();
							}
						})
				.setNegativeButton("Não",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(
									DialogInterface dialogInterface, int i) {
								dialogInterface.cancel();
							}
						});
		return builder;
	}

	public Dialog editarDialog(final Marker arg0) {
		final Dialog dialog = new Dialog(this);
		dialog.setContentView(R.layout.custom_dialog_pi);
		dialog.setTitle("Editar Ponto de Interesse");
		final EditText txtdescricao = (EditText) dialog
				.findViewById(R.id.txtPIdescricao);
		spinner1 = (Spinner) dialog.findViewById(R.id.spinnertipopi);
		final EditText txtlat = (EditText) dialog.findViewById(R.id.txtPIlat);
		final EditText txtlon = (EditText) dialog.findViewById(R.id.txtPIlon);
		txtlat.setEnabled(false);
		txtlon.setEnabled(false);

		favorito = (CheckBox) dialog.findViewById(R.id.favorite);

		spinner1.setOnItemSelectedListener(this);

		db = new DBAccess(ctx);
		DBAccess.getInstance(ctx);

		setFavorito(arg0);

		// Loading spinner data from database
		loadSpinnerData(spinner1);

		txtdescricao.setText("" + arg0.getTitle());
		txtlat.setText(("" + arg0.getPosition().latitude).substring(0, 7));
		txtlon.setText(("" + arg0.getPosition().longitude).substring(0, 7));

		final Button btnEdit = (Button) dialog.findViewById(R.id.addeditPI);

		btnEdit.setEnabled(false);

		txtdescricao.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
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
				} else {
					btnEdit.setEnabled(true);
				}

			}
		});

		btnEdit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				db = new DBAccess(ctx);
				DBAccess.getInstance(ctx);

				db.editaPontoInteresse(txtdescricao.getText()
						.toString(), Long.parseLong(arg0.getSnippet()),
						(int) spinner1.getSelectedItemId(), getFavorito());
				if (getFavorito() == 1)
					arg0.setIcon(BitmapDescriptorFactory
							.fromResource(R.drawable.regular));

				else if (getFavorito() == 0)
					arg0.setIcon(BitmapDescriptorFactory
							.fromResource(R.drawable.star));
				
				arg0.setTitle(txtdescricao.getText().toString());
				
				
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

		return dialog;
	}

	
	
	

	public void handleGetDirectionsResult(ArrayList<LatLng> directionPoints) {
		PolylineOptions rectLine = new PolylineOptions().width(5).color(
				Color.RED);

		for (int i = 0; i < directionPoints.size(); i++) {
			rectLine.add(directionPoints.get(i));
		}
		if (newPolyline != null) {
			newPolyline.remove();
		}
		newPolyline = map.addPolyline(rectLine);

		latlngBounds = createLatLngBoundsObject(p1, p2);
		// map.animateCamera(CameraUpdateFactory.newLatLngBounds(latlngBounds,
		// 150));

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {

	}

	@Override
	public void onProviderEnabled(String provider) {

	}

	@Override
	public void onProviderDisabled(String provider) {

	}

	@Override
	public void onMyLocationChange(Location arg0) {
		map.animateCamera(CameraUpdateFactory.newLatLngZoom(
				new LatLng(arg0.getLatitude(), arg0.getLongitude()), 15));

	}

	public void verificaMarkers(Marker arg0) {
		if (p1 == null) {
			p1 = arg0.getPosition();
		} else if (p2 == null && p1 != null) {
			p2 = arg0.getPosition();
		} else if (p2 != null && p1 != null) {
			p1 = p2 = null;
		}
	}

	private LatLngBounds createLatLngBoundsObject(LatLng firstLocation,
			LatLng secondLocation) {
		if (firstLocation != null && secondLocation != null) {
			LatLngBounds.Builder builder = new LatLngBounds.Builder();
			builder.include(firstLocation).include(secondLocation);

			return builder.build();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public void findDirections(double fromPositionDoubleLat,
			double fromPositionDoubleLong, double toPositionDoubleLat,
			double toPositionDoubleLong, String mode) {
		Map<String, String> map = new HashMap<String, String>();
		map.put(GetDirectionsAsyncTask.USER_CURRENT_LAT,
				String.valueOf(fromPositionDoubleLat));
		map.put(GetDirectionsAsyncTask.USER_CURRENT_LONG,
				String.valueOf(fromPositionDoubleLong));
		map.put(GetDirectionsAsyncTask.DESTINATION_LAT,
				String.valueOf(toPositionDoubleLat));
		map.put(GetDirectionsAsyncTask.DESTINATION_LONG,
				String.valueOf(toPositionDoubleLong));
		map.put(GetDirectionsAsyncTask.DIRECTIONS_MODE, mode);

		GetDirectionsAsyncTask asyncTask = new GetDirectionsAsyncTask(this);
		asyncTask.execute(map);
	}

	public void getLocation() {
		Location location = map.getMyLocation();

		LatLng currentPosition = new LatLng(location.getLatitude(),
				location.getLongitude());

		Toast.makeText(this, "Lat:" + currentPosition.latitude,
				Toast.LENGTH_LONG).show();

	}

	public void getMarkerCat() {
		final Dialog dialog = new Dialog(this);
		dialog.setContentView(R.layout.spinnercat);
		dialog.setTitle("Seleccionar Categoria");
		final Spinner spinner2 = (Spinner) dialog
				.findViewById(R.id.spinnercateg);
		spinner2.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});

		db = new DBAccess(ctx);
		DBAccess.getInstance(ctx);

		// Loading spinner data from database
		loadSpinnerData(spinner2);

		Button btnConfirm = (Button) dialog.findViewById(R.id.confirmCat);
		Button btnCancel = (Button) dialog.findViewById(R.id.cancelCat);

		btnConfirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setMarkersTestByCat((int) spinner2.getSelectedItemId());
				dialog.cancel();
			}
		});

		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.cancel();

			}
		});
		dialog.show();

	}

}
