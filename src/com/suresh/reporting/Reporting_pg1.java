package com.suresh.reporting;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.suresh.extras.GPSTracker;
import com.suresh.extras.GridAdapter;
import com.suresh.extras.SessionManager;
import com.suresh.form.R;

public class Reporting_pg1 extends Activity 
{

	private Spinner event_spinner;
	static String disaster_event=null,disaster_incident=null;
	private GridView incidents_grid;
	private TextView textview_gridheading;
	private GridAdapter mAdapter;
	private double latitude;
	private double lonitude;
	private SessionManager session;
	private Uri fileUri;
	private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 0,MEDIA_TYPE_IMAGE = 1;
	static public JSONObject reporting;
	JSONObject photo;
	private File mediaFile;
	private File cacheDir;
	private String photoname;
	protected void onCreate(Bundle savedInstanceState)
	{
		setContentView(R.layout.reporting_pg1);
		super.onCreate(savedInstanceState);
		textview_gridheading=(TextView)findViewById(R.id.grid_head);
		incidents_grid=(GridView)findViewById(R.id.impact_gridView);
		session=new SessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        user.get(SessionManager.KEY_ID);
  		GPSTracker gps = new GPSTracker(this);
		if(gps.canGetLocation())
		{
			latitude=gps.getLatitude(); 
			lonitude=gps.getLongitude(); 

		
		mAdapter=new GridAdapter(Reporting_pg1.this,R.array.disaster_incidents,R.array.disaster_incidents_icon);
		incidents_grid.setAdapter(mAdapter);
		event_spinner=(Spinner)findViewById(R.id.spinner1);
		event_spinner.setOnItemSelectedListener(new OnItemSelectedListener()
		{
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long arg3) 
			{
				disaster_event=event_spinner.getSelectedItem().toString();
				incidents_grid.setVisibility(View.VISIBLE);
				textview_gridheading.setVisibility(View.VISIBLE);
			}
			
			public void onNothingSelected(AdapterView<?> arg0)
			{
				
			}
		});
		
		incidents_grid.setOnItemClickListener(new OnItemClickListener()
		{
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
					long arg3)
			{
				disaster_incident=mAdapter.getItem(pos);
				JSONObject incident=new JSONObject();
				try 
				{
					incident.put("item_name", disaster_incident);
					incident.put("address", "");
					incident.put("latitude", latitude);
					incident.put("longitude", lonitude);
					incident.put("incident_timestamp",new FileCache(Reporting_pg1.this).getDate());
					incident.put("description", "no description");
				} 
				catch (JSONException e) 
				{
					e.printStackTrace();
				}
				reporting=new JSONObject();
				try {
					reporting.put("ReportItemIncident", incident);
				} catch (JSONException e) 
				{
					e.printStackTrace();
				}
				openDialog();
			}
		});

	}
	else
	{
		gps.showSettingsAlert();
	}
	}
	
	@SuppressWarnings("deprecation")
	protected void openDialog()
	{
		
		AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setTitle("Do you Want to take Photo?");
		alertDialog.setMessage("Taking Photo helps in .............");
		alertDialog.setIcon(R.drawable.tick);
		photo=new JSONObject();

		alertDialog.setButton("No Thanks", new OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int which)
			{
				Intent intent=new Intent(getApplicationContext(),Reporting_pg2.class);
				intent.putExtra("event", disaster_event);
				intent.putExtra("incident", disaster_incident);
				Log.i("dis_sele", reporting.toString());
				try 
				{
					photo.put("photo_taken", false);
					reporting.put("Photo", photo);

				}
				catch (JSONException e) 
				{
					e.printStackTrace();
				}
				intent.putExtra("reporting", reporting.toString());
				startActivity(intent);
				dialog.cancel();
			}
		});
		alertDialog.setButton2("OK", new DialogInterface.OnClickListener() 
		{	
			public void onClick(final DialogInterface dialog, final int which) 
			{
				if (!isDeviceSupportCamera()) 
				{
					Intent intent=new Intent(getApplicationContext(),Reporting_pg2.class);

					toaster("Sorry! Your device doesn't support camera");
					Log.i("cam", "no");
					try 
					{
						photo.put("photo_taken", false);
						reporting.put("Photo", photo);

					}
					catch (JSONException e) 
					{
						e.printStackTrace();
					}
					intent.putExtra("reporting", reporting.toString());
					startActivity(intent);
				}
				else
				{
					takephoto(disaster_incident);
				}

			}
		});
		
		alertDialog.setButton3("Cancel", new OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which) 
			{
				dialog.dismiss();
			}
		});
		alertDialog.show();	                	
	}
	
	protected String takephoto(String incident_name)
	{
		photoname=incident_name;
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
		getOutputMediaFile(MEDIA_TYPE_IMAGE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
		startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
		return mediaFile.getName();
	}


	protected void toaster(String string)
	{
		Toast.makeText(getApplicationContext(), string, Toast.LENGTH_SHORT).show();
	}

	private boolean isDeviceSupportCamera() 
	{
		if (getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) 
		{
			return true;
		}
		else 
		{
			return false;
		}
	}
			
	private Uri getOutputMediaFileUri(int type) 
		{
			return Uri.fromFile(getOutputMediaFile(type));
		}

	private File getOutputMediaFile(int type)
		{
		   cacheDir=new FileCache(Reporting_pg1.this).getdir();
			if (type == MEDIA_TYPE_IMAGE) 
			{
				int i=0;
				  mediaFile=new File(cacheDir, photoname+".jpg");
				  while(mediaFile.exists())
				  {
					  mediaFile=new File(cacheDir,photoname+i+".jpg");
					  i=i+1;
				  }
				  Log.i("file_name",mediaFile.getName());	
			} 
			else 
			{
				return null;
			}
			return mediaFile;
		}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		Log.i("came", "start");
		if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) 
		{
			if (resultCode == RESULT_OK) 
			{
				resizeCapturedImage();
				GPSTracker gps=new GPSTracker(Reporting_pg1.this);
				double longitude = gps.getLongitude();
				double latitude = gps.getLatitude();
				geoTag(mediaFile.getAbsolutePath(),latitude,longitude);
				try 
				{
					photo.put("photo_taken", true);
					photo.put("name", mediaFile.getName());
				}
				catch (JSONException e) 
				{
					e.printStackTrace();
				}
				try
				{
					reporting.put("Photo", photo);
				}
				catch (JSONException e) 
				{
					e.printStackTrace();
				}

                Intent intent=new Intent(Reporting_pg1.this,Reporting_pg2.class);
				intent.putExtra("reporting", reporting.toString());
                startActivity(intent);

			}  		    
		}
		else if (resultCode == RESULT_CANCELED) 
		{
			toaster("User cancelled image capture");
		}
		else 
		{
			toaster("Sorry! Failed to capture image");
		}
	}

	private void resizeCapturedImage() 
	{
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 1;
		Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(),options);
		bitmap=getResizedBitmap(bitmap);
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 40, bytes);
		File f = new File(fileUri.getPath());
		try 
		{
			f.createNewFile();
			FileOutputStream fo = new FileOutputStream(f);
			fo.write(bytes.toByteArray());
			fo.close();
		}
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
			
	public Bitmap getResizedBitmap(Bitmap bm)
	{
		int width = bm.getWidth();
		int height = bm.getHeight();
		int newWidth=1000;
		int newHeight=1000*height/width;
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
		return resizedBitmap;
	}
		
	private void geoTag(String absolutePath, double latitude, double longitude) 
	{
		ExifInterface exif;
		try 
		{
			exif = new ExifInterface(absolutePath);
			int num1Lat = (int)Math.floor(latitude);
			int num2Lat = (int)Math.floor((latitude - num1Lat) * 60);
			double num3Lat = (latitude - ((double)num1Lat+((double)num2Lat/60))) * 3600000;
			int num1Lon = (int)Math.floor(longitude);
			int num2Lon = (int)Math.floor((longitude - num1Lon) * 60);
			double num3Lon = (longitude - ((double)num1Lon+((double)num2Lon/60))) * 3600000;
			exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, num1Lat+"/1,"+num2Lat+"/1,"+num3Lat+"/1000");
			exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, num1Lon+"/1,"+num2Lon+"/1,"+num3Lon+"/1000");
			if (latitude > 0) 
			{
				exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, "N"); 
			}
			else	 
			{
				exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, "S");
			}
			
			if (longitude > 0) 
			{
				exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, "E");    
			}
			else 
			{
				exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, "W");
			}
			exif.saveAttributes();
		} 
		catch (IOException e) 
		{
			Log.e("PictureActivity", e.getLocalizedMessage());
		} 
	}
}
