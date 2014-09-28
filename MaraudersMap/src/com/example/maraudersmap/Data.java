package com.example.maraudersmap;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.PictureDrawable;

import com.larvalabs.svgandroid.SVG;
import com.larvalabs.svgandroid.SVGParser;

public class Data {
	public static HashMap<Integer, Person> people = new HashMap<Integer, Person>();
	public static Bitmap rightFoot, leftFoot, umbcMap;
	public static int id, orientation;
	public static double lat, lng;

	public static Person addPerson (JSONObject person){
		Person newPerson = null;
		try {
			newPerson = new Person(person.getString("name"), person.getInt("id"), person.getDouble("lat"),
								   person.getDouble("lng"), person.getInt("bearing"));
		} catch (JSONException e) {
		}
		people.put(newPerson.getId(), newPerson);
		return newPerson;
	}
	
	public static void init(Context context){
		{
			SVG svg = SVGParser.getSVGFromResource(context.getResources(), R.drawable.footleft);
			PictureDrawable pictureDrawable = svg.createPictureDrawable();
			rightFoot = Bitmap.createBitmap(pictureDrawable.getIntrinsicWidth(),pictureDrawable.getIntrinsicHeight(), Config.ARGB_8888);
		}
		
		{
			SVG svg = SVGParser.getSVGFromResource(context.getResources(), R.drawable.footright);
			PictureDrawable pictureDrawable = svg.createPictureDrawable();
			leftFoot = Bitmap.createBitmap(pictureDrawable.getIntrinsicWidth(),pictureDrawable.getIntrinsicHeight(), Config.ARGB_8888);
		}
		
		{
			SVG svg = SVGParser.getSVGFromResource(context.getResources(), R.drawable.maraudermaphome);
			PictureDrawable pictureDrawable = svg.createPictureDrawable();
			umbcMap = Bitmap.createBitmap(pictureDrawable.getIntrinsicWidth(),pictureDrawable.getIntrinsicHeight(), Config.ARGB_8888);
		}
	}
}