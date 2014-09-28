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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.MarkerOptionsCreator;
import com.google.android.gms.maps.model.PolylineOptions;

public class MiniMapFragment extends SupportMapFragment implements
		OnMarkerClickListener {

	int position, responses;
	private static double lat = 39.256116, lng = -76.710749;
	HashMap<Object, Marker> markers;
	Handler r;
	protected MainActivity context;

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
		r = new Handler();
		initMap();

	}

	public void initMap() {
		GoogleMap map = getMap();
		map.clear();
		BitmapDescriptor marauder_map = BitmapDescriptorFactory.fromResource(R.drawable.maraudersmapoverlay);
		

	}

	public void updateMap() {
		GoogleMap map = getMap();
		map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
				Data.lat, Data.lng), (float) 13.0));

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

//				float angle = (float) Math.toDegrees(Math.asin(Math.abs(m
//						.getPosition().latitude - p.getLat())
//						/ Math.sqrt(0.0000001 + Math.pow(
//								m.getPosition().latitude - p.getLat(), 2)
//								+ Math.pow(
//										m.getPosition().longitude - p.getLng(),
//										2))));
//				
//				Log.i("baba", angle + "");
//
//				m.setPosition(new LatLng(p.getLat(), p.getLng()));
//				m.setRotation((float) angle);
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