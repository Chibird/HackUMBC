package com.example.maraudersmap;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.MarkerOptionsCreator;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.VisibleRegion;

public class MiniMapFragment extends SupportMapFragment implements
		OnMarkerClickListener {
	GoogleMap map;
	int position, responses;
	protected Marker selectedId;
	private static double lat = 39.256116, lng = -76.710749;
	HashMap<Object, Marker> markers;
	HashMap<Marker, Person> markersp;
	Handler r;
	protected MainActivity context;
	private final LatLngBounds BOUNDS = LatLngBounds.builder()
			.include(new LatLng(39.259805f, -76.705385 + 0.0025))
			.include(new LatLng(39.262330f, -76.718217f + 0.0025))
			.include(new LatLng(39.245349f, -76.706200f + 0.0025))
			.include(new LatLng(39.249703f, -76.723624f + 0.0025)).build();
	private final int MAX_ZOOM = 20;
	private final int MIN_ZOOM = 16;

	public MiniMapFragment() {
		this.position = -1;
		markers = new HashMap<Object, Marker>();
		markersp = new HashMap<Marker, Person>();
		context = (MainActivity) getActivity();
	}

	@Override
	public void onDestroy() {
		r.removeCallbacks(null);
		r.removeCallbacksAndMessages(null);
		super.onDestroy();
	}

	public static MiniMapFragment newInstance() {
		MiniMapFragment frag = new MiniMapFragment();
		return frag;
	}

	@Override
	public View onCreateView(LayoutInflater arg0, ViewGroup arg1, Bundle arg2) {
		View v = super.onCreateView(arg0, arg1, arg2);
		return v;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		r = new Handler() {
			int counter = 0;
			
			public Bitmap createDrawableFromView(Context context, View view) {
				view.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
				view.measure(140, 50);
				view.layout(0, 0, 140, 50);
				view.buildDrawingCache();
				Bitmap bitmap = Bitmap.createBitmap(140, 50, Bitmap.Config.ARGB_8888);
		 
				Canvas canvas = new Canvas(bitmap);
				view.draw(canvas);
		 
				return bitmap;
			}


			@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
			@SuppressLint("NewApi")
			@Override
			public void handleMessage(Message msg) {
				CameraPosition position = map.getCameraPosition();
				VisibleRegion region = map.getProjection().getVisibleRegion();
				float zoom = 0;
				if (position.zoom < MIN_ZOOM)
					zoom = MIN_ZOOM;
				if (position.zoom > MAX_ZOOM)
					zoom = MAX_ZOOM;
				LatLng correction = getLatLngCorrection(region.latLngBounds);
				if (zoom != 0 || correction.latitude != 0
						|| correction.longitude != 0) {
					zoom = (zoom == 0) ? position.zoom : zoom;
					double lat = position.target.latitude + correction.latitude;
					double lon = position.target.longitude
							+ correction.longitude;
					CameraPosition newPosition = new CameraPosition(new LatLng(
							lat, lon), zoom, position.tilt, position.bearing);
					CameraUpdate update = CameraUpdateFactory
							.newCameraPosition(newPosition);
					map.moveCamera(update);
				}
				for (int i : Data.people.keySet()) {
					Person p = Data.people.get(i);
					if(!markers.containsKey(p.getId()*10 + 2)){
						TextView tv = new TextView(getActivity());
						tv.setText(p.getName());
						tv.setPadding(15, 0, 0, 0);
						tv.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
						tv.setTextColor(Color.rgb(80, 40, 22));
						tv.setBackground(getActivity().getResources().getDrawable(
								R.drawable.banner));
						Marker m = map.addMarker(new MarkerOptions()
						.position(new LatLng(p.getLat(), p.getLng()))
						.icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(getActivity(), tv))));
						markers.put(p.getId() * 10 + 2, m);
						
					} else {
						Marker m = markers.get(p.getId()*10 + 2);
						m.setPosition(new LatLng(p.getLat(), p.getLng()));
					}
					Marker m = null;
					if (!markers.containsKey(p.getId() * 10
							+ (p.getFoot() ? 1 : 0))) {
						m = map.addMarker(new MarkerOptions()
								.position(new LatLng(p.getLat(), p.getLng()))
								.title(p.getName())
								.icon(BitmapDescriptorFactory
										.fromResource(R.drawable.leftfoot))
								.rotation(p.getBearing()));
						markers.put(p.getId() * 10 + (p.getFoot() ? 1 : 0), m);
						markersp.put(m, p);
					} else {
						m = markers.get(p.getId() * 10 + (p.getFoot() ? 1 : 0));

						LatLng cPos = m.getPosition();
						LatLng nPos = new LatLng(
								(cPos.latitude + p.getLat()) / 2,
								(cPos.longitude + p.getLng()) / 2);
						p.toggleFoot();
						m.setPosition(nPos);
						float angle = -map.getCameraPosition().bearing;
						double feetAngle = calculateAngle(cPos, nPos);
						m.setRotation(angle - (float) (180 - feetAngle));
						if (p.getFoot()) {
							m.setIcon(BitmapDescriptorFactory
									.fromResource(R.drawable.rightfoot));
						} else {
							m.setIcon(BitmapDescriptorFactory
									.fromResource(R.drawable.leftfoot));
						}
					}
					if (m.getAlpha() < 0.4) {
						m.setAlpha(1.0f);
					} else {
						m.setAlpha(m.getAlpha() / 2f);
					}
				}
				if(selectedId != null){
					selectedId.showInfoWindow();
					Log.i("baba", selectedId.toString());
				}
				/* Recursively call handler every 100ms */
				if (counter == 10) {
					new getUpdateFeedTask()
							.execute("http://54.165.53.129/get_map.php");
					counter = 0;
				} else {
					counter++;
				}
				sendEmptyMessageDelayed(0, 200);
			}
		};
		initMap();
		r.sendEmptyMessageDelayed(0, 100);

	}

	double calculateAngle(LatLng loc1, LatLng loc2) {
		double y = loc1.latitude - loc2.latitude;
		double x = loc1.longitude - loc2.longitude;
		double angle = Math.atan2(x, y);
		angle = Math.toDegrees(angle);
		return angle;
	}

	class getUpdateFeedTask extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... params) {

			String url = params[0];
			HttpGet httpGet = new HttpGet(url);
			HttpClient client = new DefaultHttpClient();
			HttpResponse response;
			StringBuilder stringBuilder = new StringBuilder();

			try {
				response = client.execute(httpGet);

				HttpEntity entity = response.getEntity();
				InputStream stream = entity.getContent();
				int b;
				while ((b = stream.read()) != -1) {
					stringBuilder.append((char) b);
				}
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			String q = stringBuilder.toString();
			Log.i("baba", q);

			try {
				JSONArray jsonData = new JSONArray(q);
				for (int i = 0; i < jsonData.length(); i++) {
					JSONObject jsonPerson = jsonData.getJSONObject(i);
					int jsonID = jsonPerson.getInt("id");

					if (Data.people.containsKey(jsonID)) {
						Data.people.get(jsonID).update(jsonPerson);
					} else {
						Data.addPerson(jsonPerson);
					}
				}
			} catch (Exception e) {
			}
			;

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
		}

	}

	public void initMap() {
		map = getMap();
		// map.setMyLocationEnabled(true);
		map.setMapType(GoogleMap.MAP_TYPE_NONE);
		map.clear();
		map.getUiSettings().setZoomControlsEnabled(false);
		map.getUiSettings().setRotateGesturesEnabled(false);
		map.moveCamera(CameraUpdateFactory
				.newCameraPosition(new CameraPosition(new LatLng(39.256116,
						-76.710749), 17.0f, 0, 23)));
		BitmapDescriptor marauder_map = BitmapDescriptorFactory
				.fromResource(R.drawable.maraudersmapoverlay);

		GroundOverlay groundOverlay = map
				.addGroundOverlay(new GroundOverlayOptions()
						.image(marauder_map).positionFromBounds(BOUNDS)
						.transparency((float) 0.0));

	}

	private LatLng getLatLngCorrection(LatLngBounds cameraBounds) {
		double latitude = 0, longitude = 0;
		if (cameraBounds.southwest.latitude < BOUNDS.southwest.latitude) {
			latitude = BOUNDS.southwest.latitude
					- cameraBounds.southwest.latitude;
		}
		if (cameraBounds.southwest.longitude < BOUNDS.southwest.longitude) {
			longitude = BOUNDS.southwest.longitude
					- cameraBounds.southwest.longitude;
		}
		if (cameraBounds.northeast.latitude > BOUNDS.northeast.latitude) {
			latitude = BOUNDS.northeast.latitude
					- cameraBounds.northeast.latitude;
		}
		if (cameraBounds.northeast.longitude > BOUNDS.northeast.longitude) {
			longitude = BOUNDS.northeast.longitude
					- cameraBounds.northeast.longitude;
		}
		return new LatLng(latitude, longitude);
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		selectedId = marker;
		return true;
	}
}