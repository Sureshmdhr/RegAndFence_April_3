package com.suresh.extras;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.suresh.form.R;

@SuppressLint("ViewHolder")
public class GridAdapter extends BaseAdapter {

	private Context mContext;
	int title_id,icon_id;
	private String[] navMenuTitles;
	private TypedArray navMenuIcons;
	private List<NameValuePair> nameValuePair=new ArrayList<NameValuePair>();
	private ArrayList<List<NameValuePair>> allvalues;
	
	@SuppressLint("Recycle")
	public GridAdapter(Context applicationContext,int titleid,int iconid) {
		mContext=applicationContext;
		this.title_id=titleid;
		this.icon_id=iconid;
		this.navMenuTitles =mContext.getResources().getStringArray(title_id);
		this.navMenuIcons = mContext.getResources().obtainTypedArray(icon_id);	
		allvalues=new ArrayList<List<NameValuePair>>(navMenuTitles.length);
		while(allvalues.size() < navMenuTitles.length)allvalues.add(null);
		Log.i("size", allvalues.size()+"");
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return navMenuTitles.length;
	}

	@Override
	public String getItem(int position) {
		// TODO Auto-generated method stub
		return navMenuTitles[position];
	}
	
	public String[] getallNames(){
		return navMenuTitles;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	
	public void setKeyvalue(int position,String paramString1, String paramString2) 
	{
		if(allvalues.get(position)==null)
			nameValuePair=new ArrayList<NameValuePair>();
		Log.i("si", nameValuePair.size()+"");
		if(nameValuePair.size()>2)
		{
			if(paramString1.equalsIgnoreCase("count"))
			{
				nameValuePair.set(0,new BasicNameValuePair(paramString1, paramString2));
			}
			else if (paramString1.equalsIgnoreCase("unit"))
			{
				nameValuePair.set(1,new BasicNameValuePair(paramString1, paramString2));
			}
			else if (paramString1.equalsIgnoreCase("describe"))
			{
				nameValuePair.set(2,new BasicNameValuePair(paramString1, paramString2));
			}
		}
		else
		{
			nameValuePair.add(new BasicNameValuePair(paramString1, paramString2));
		}
		allvalues.set(position, nameValuePair);
	}
	
	public List<NameValuePair> getKeyvalue(int position) 
	{
		return allvalues.get(position);
	}

	@SuppressLint("ResourceAsColor")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
        View myView;

        LayoutInflater li = ((Activity) mContext).getLayoutInflater();
        myView = li.inflate(R.layout.gridview_style, parent,false);
        ImageView imageView = (ImageView) myView.findViewById(R.id.img2);
        imageView.setImageResource(navMenuIcons.getResourceId(position, 0));
        TextView txt = (TextView) myView.findViewById(R.id.txt2);
        txt.setText(getItem(position));
        //txt.setTextColor(Color.BLACK);
        return myView;
	}
 

}
