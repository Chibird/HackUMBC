package com.example.maraudersmap;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

public class SplashActivity extends Activity implements View.OnClickListener {
	SharedPreferences sp;
	boolean clicked = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		sp = getPreferences(Activity.MODE_PRIVATE);

		ImageView myAnimation = (ImageView)findViewById(R.id.touchthispls);
		AnimationDrawable myAnimDrawable = (AnimationDrawable) myAnimation.getDrawable();
		myAnimDrawable.start();
		myAnimation.setOnClickListener(this);

	}

	class RegisterTask extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... params) {
			// TODO Auto-generated method stub
			String url = "http://54.165.53.129/create_user.php?name="
					+ params[0] + "&lat=0&lng=0";

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

			String contents = stringBuilder.toString();
			sp.edit().putInt("myID", Integer.parseInt(contents)).commit();
			Data.id = Integer.parseInt(contents);
			Log.i("baba", Data.id + " ");
			return null;
		}

	}

	@Override
	public void onClick(View v) {
		if (sp.contains("myID") && !clicked) {
			clicked = true;
			
			ImageView myAnimation = (ImageView)findViewById(R.id.touchthispls);
			myAnimation.setImageResource(R.drawable.bg_anim2);
			AnimationDrawable myAnimDrawable = (AnimationDrawable) myAnimation.getDrawable();
			myAnimDrawable.start();
			myAnimDrawable.setOneShot(true);
			
			new Handler().postDelayed(new Runnable() {
				
				@Override
				public void run() {
					Data.id = sp.getInt("myID", -1);
					startActivity(new Intent(SplashActivity.this, MainActivity.class));
					finish();					
				}
			}, 3000);
			
			
			
			
			
		} else {
			AlertDialog.Builder alert = new AlertDialog.Builder(this);

			alert.setTitle("Set Name");
			alert.setMessage("You don't have a name!");

			// Set an EditText view to get user input
			final EditText input = new EditText(this);
			alert.setView(input);

			alert.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							String value = input.getText().toString();

							new RegisterTask().execute(value);
							ImageView myAnimation = (ImageView)findViewById(R.id.touchthispls);
							myAnimation.setImageResource(R.drawable.bg_anim2);
							AnimationDrawable myAnimDrawable = (AnimationDrawable) myAnimation.getDrawable();
							myAnimDrawable.start();
							myAnimDrawable.setOneShot(true);
							
							new Handler().postDelayed(new Runnable() {
								
								@Override
								public void run() {
									Data.id = sp.getInt("myID", -1);
									startActivity(new Intent(SplashActivity.this, MainActivity.class));
									finish();					
								}
							}, 3000);
						}
					});

			alert.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
						}
					});
			clicked = false;
			alert.show();
		}
		// see http://androidsnippets.com/prompt-user-input-with-an-alertdialog

	}
}
