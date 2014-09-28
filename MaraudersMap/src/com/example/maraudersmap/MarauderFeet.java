package com.example.maraudersmap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.drawable.PictureDrawable;

import com.google.android.gms.maps.model.Marker;
import com.larvalabs.svgandroid.SVG;
import com.larvalabs.svgandroid.SVGParser;


public class MarauderFeet {
	private Marker person;
	private float alpha;
	private Context context;
	
	
	public MarauderFeet(Marker person, Context context) {
		this.person = person;
		this.context = context;

	}
	
	public void update() {
		person.setAlpha(alpha);
		alpha /= 2;
	}
	
	public Marker getMarker(){
		return person;
	}
	
	
}
