package com.example.maraudersmap;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
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
	private static double lat = 39.256116, lng = -76.710749;
	HashMap<Object, Marker> markers;
	Handler r;
	protected MainActivity context;
	private final LatLngBounds BOUNDS = LatLngBounds.builder().include(new LatLng(39.259805f, -76.705385+ 0.0025)).include(new LatLng(39.262330f, -76.718217f+ 0.0025)).include(new LatLng(39.245349f, -76.706200f+ 0.0025)).include(new LatLng(39.249703f, -76.723624f+ 0.0025)).build();
	private final int MAX_ZOOM = 20;
	private final int MIN_ZOOM = 16;

	public MiniMapFragment() {
		this.position = -1;
		markers = new HashMap<Object, Marker>();
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
		r = new Handler(){
			@Override
		    public void handleMessage(Message msg) {
		        CameraPosition position = map.getCameraPosition();
		        VisibleRegion region = map.getProjection().getVisibleRegion();
		        float zoom = 0;
		        if(position.zoom < MIN_ZOOM) zoom = MIN_ZOOM;
		        if(position.zoom > MAX_ZOOM) zoom = MAX_ZOOM;
		        LatLng correction = getLatLngCorrection(region.latLngBounds);
		        if(zoom != 0 || correction.latitude != 0 || correction.longitude != 0) {
		            zoom = (zoom==0)?position.zoom:zoom;
		            double lat = position.target.latitude + correction.latitude;
		            double lon = position.target.longitude + correction.longitude;
		            CameraPosition newPosition = new CameraPosition(new LatLng(lat,lon), zoom, position.tilt, position.bearing);
		            CameraUpdate update = CameraUpdateFactory.newCameraPosition(newPosition);
		            map.moveCamera(update);
		        }
		        /* Recursively call handler every 100ms */
		        new getUpdateFeedTask().execute("http://54.165.53.129/get_map.php");
		        sendEmptyMessageDelayed(0,1000);
		    }
		};
		initMap();
		r.sendEmptyMessageDelayed(0, 100);

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
		map.setMyLocationEnabled(true);
		map.setMapType(GoogleMap.MAP_TYPE_NONE);
		map.clear();
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(39.256116,-76.710749), 16.0f));
		BitmapDescriptor marauder_map = BitmapDescriptorFactory.fromResource(R.drawable.maraudersmapoverlay);
		
		GroundOverlay groundOverlay = map.addGroundOverlay(new GroundOverlayOptions().image(marauder_map).positionFromBounds(BOUNDS).transparency((float)0.0));
		
	}
	
	private LatLng getLatLngCorrection(LatLngBounds cameraBounds) {
	    double latitude=0, longitude=0;
	    if(cameraBounds.southwest.latitude < BOUNDS.southwest.latitude) {
	        latitude = BOUNDS.southwest.latitude - cameraBounds.southwest.latitude;
	    }
	    if(cameraBounds.southwest.longitude < BOUNDS.southwest.longitude) {
	        longitude = BOUNDS.southwest.longitude - cameraBounds.southwest.longitude;
	    }
	    if(cameraBounds.northeast.latitude > BOUNDS.northeast.latitude) {
	        latitude = BOUNDS.northeast.latitude - cameraBounds.northeast.latitude;
	    }
	    if(cameraBounds.northeast.longitude > BOUNDS.northeast.longitude) {
	        longitude = BOUNDS.northeast.longitude - cameraBounds.northeast.longitude;
	    }
	    return new LatLng(latitude, longitude);
	}

	public void updateMap() {
//		map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
//				Data.lat, Data.lng), 17.0f));
		for (int i : Data.people.keySet()) {
			Person p = Data.people.get(i);
			Marker m = null;
			if (!markers.containsKey(p.getId())) {
				m = map.addMarker(new MarkerOptions()
						.position(new LatLng(p.getLat(), p.getLng()))
						.title(p.getName())
						.icon(BitmapDescriptorFactory
								.fromResource(R.drawable.leftfoot))
						.rotation(p.getBearing()));
				markers.put(p.getId(), m);
			} else {
				m = markers.get(p.getId());
				if(Math.random()*2 > 1){
					m.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.rightfoot));
					Data.foot = false;
				} else {
					m.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.leftfoot));
					Data.foot = true;
				}				m.setPosition(new LatLng(p.getLat(), p.getLng()));
			}
			Log.i("baba", Data.people.get(i).getLat() + " "
					+ Data.people.get(i).getLng());
		}
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		marker.showInfoWindow();
		return false;
	}

}