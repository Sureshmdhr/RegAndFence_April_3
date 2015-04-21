package com.suresh.reporting;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.content.Intent;
import android.os.Bundle;
import android.service.textservice.SpellCheckerService.Session;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.suresh.extras.GridAdapter;
import com.suresh.extras.SessionManager;
import com.suresh.form.R;

public class Reporting_pg3 extends Activity
{
	private GridView need_gridview;
	private GridAdapter adapter;
	private ImageButton img_next;
	private ImageButton img_prev;
	private TextView skip,need_title;
	private AlertDialog alertDialog;
	private ArrayList<Integer> positions=new ArrayList<Integer>();
	protected JSONArray need_Array;
	public static JSONObject reporting;

	@SuppressLint("InflateParams")
	protected void onCreate(Bundle savedInstanceState)
	{
		setContentView(R.layout.reporting_pg2);
		super.onCreate(savedInstanceState);
		need_title=(TextView)findViewById(R.id.tv1);
		need_title.setText("Needs");
		
		Bundle extra = getIntent().getExtras();
		
		try 
		{
			reporting=new JSONObject(extra.getString("reporting"));
		}
		catch (JSONException e1) 
		{
			e1.printStackTrace();
		}

		JSONObject user = new JSONObject();
		SessionManager session=new SessionManager(Reporting_pg3.this);
		HashMap<String, String> hm = session.getUserDetails();
		String user_id = hm.get(SessionManager.KEY_ID);
		String user_name = hm.get(SessionManager.KEY_EMAIL);
		try 
		{
			user.put("id", user_id);
			user.put("email", user_name);
			reporting.put("user", user.toString());
		}
		catch (JSONException e1) 
		{
			e1.printStackTrace();
		}
		
		TextView tv_eventtitle=(TextView)findViewById(R.id.event_title);
		tv_eventtitle.setText(Reporting_pg1.disaster_incident);
		
		need_gridview=(GridView)findViewById(R.id.impact_gridView);
		adapter=new GridAdapter(Reporting_pg3.this, R.array.need_titles, R.array.need_icons);
		need_gridview.setAdapter(adapter);
		need_gridview.setOnItemClickListener(new OnItemClickListener()
		{
			public void onItemClick(AdapterView<?> arg0, View arg1, final int pos,
					long arg3) 
			{
			    AlertDialog.Builder builder = new AlertDialog.Builder(Reporting_pg3.this);

			    LayoutInflater inflater = Reporting_pg3.this.getLayoutInflater();
			    final View view = inflater.inflate(R.layout.impact_choose, null);
			    builder.setView(view);

			    builder.setTitle("Attributes");
			   // builder.setMessage("");

			    builder.setPositiveButton("OK", null);
			    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			        public void onClick(DialogInterface dialog, int whichButton) {
			        	dialog.cancel();
			        }
			    });
			    alertDialog = builder.create();
			    alertDialog.setOnShowListener(new OnShowListener() 
			    {
					
					@Override
					public void onShow(final DialogInterface dialog)
					{
						Button ok=((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
						ok.setOnClickListener(new OnClickListener()
						{
							public void onClick(View arg0) 
							{
								EditText et_count = (EditText)view.findViewById(R.id.count);
								EditText et_unit =(EditText)view.findViewById(R.id.unit);
								EditText et_describe =(EditText)view.findViewById(R.id.describe);
								
								String count = et_count.getText().toString();
								String describe = et_describe.getText().toString();
								String unit = et_unit.getText().toString();
								if(count.equals("")||unit.equals(""))
								{
									Toast.makeText(getApplicationContext(), "Empty Fields", Toast.LENGTH_SHORT).show();
								}
								else
								{
									adapter.setKeyvalue(pos, "count", count);
									adapter.setKeyvalue(pos, "unit", unit);
									adapter.setKeyvalue(pos, "describe", describe);
									if(!positions.contains(pos))
										positions.add(pos);
									dialog.dismiss();
								}
							}
						});
					}
				});
			    alertDialog.show();
			}
							
		});
		
		img_next=(ImageButton)findViewById(R.id.imageButton2);
		img_next.setOnClickListener(new OnClickListener()
		{
			private JSONObject need_Object;

			public void onClick(View v) 
			{
				if(positions.size()!=0)
					 need_Object=new JSONObject();
				for(int i=0;i<positions.size();i++)
				{
					List<NameValuePair> needs = adapter.getKeyvalue(positions.get(i));
					JSONObject jsonObject=new JSONObject();
				
					try 
					{
						//jsonObject.put("id",i+1);
						jsonObject.put("item_name", adapter.getItem(i));
						jsonObject.put("magnitude", needs.get(0).getValue());
						jsonObject.put("units", needs.get(1).getValue());
						jsonObject.put("description", needs.get(2).getValue());
						need_Object.put(String.valueOf(i+1),jsonObject);
					}
					catch (JSONException e) 
					{
						e.printStackTrace();
					}
				}
				try 
				{
					reporting.put("ReportItemNeed",need_Object);
				}
				catch (JSONException e) 
				{
					e.printStackTrace();
				}
				finish();
				Intent intent=new Intent(getApplicationContext(), Reporting_pg4.class);
				FileCache fc=new FileCache(getApplicationContext());
				try 
				{
					fc.storeFile(reporting.toString());
				}
				catch (FileNotFoundException e) 
				{
					e.printStackTrace();
					Log.e("error",e.toString());
				}
				catch (IOException e) 
				{
					e.printStackTrace();
					Log.e("error",e.toString());
				}
				intent.putExtra("reporting", reporting.toString());
				startActivity(intent);
			}
		});
		img_prev=(ImageButton)findViewById(R.id.rep_upload);
		img_prev.setOnClickListener(new OnClickListener() 
		{
			public void onClick(View arg0)
			{
				finish();
			}
		});
		skip=(TextView)findViewById(R.id.skip);
		skip.setOnClickListener(new OnClickListener() 
		{
			public void onClick(View arg0) 
			{
				Intent intent=new Intent(Reporting_pg3.this,Reporting_pg4.class);
				FileCache fc=new FileCache(getApplicationContext());
				try 
				{
					fc.storeFile(reporting.toString());
				}
				catch (FileNotFoundException e) 
				{
					e.printStackTrace();
					Log.e("error",e.toString());
				}
				catch (IOException e) 
				{
					e.printStackTrace();
					Log.e("error",e.toString());
				}

				intent.putExtra("reporting", reporting.toString());
				startActivity(intent);
				finish();
			}
		});
	}
}
