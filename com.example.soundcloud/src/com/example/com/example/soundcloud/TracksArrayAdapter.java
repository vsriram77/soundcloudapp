package com.example.com.example.soundcloud;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.souncloud.models.Track;
import com.nostra13.universalimageloader.core.ImageLoader;

public class TracksArrayAdapter extends ArrayAdapter<Track> {

	public TracksArrayAdapter(Context context, List<Track> tweets) {
		super(context, 0, tweets);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
	    View view = convertView;
	    if (view == null) {
	    	LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    	view = inflater.inflate(R.layout.track_item, null);
	    }
	     
        Track track = getItem(position);
        String date = "";
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss Z");
        try {
			Date parsedDate = dateFormat.parse(track.getCreatedAt());
			date = convertMillis(System.currentTimeMillis() - parsedDate.getTime()) + " ago";
		} catch (ParseException e) {
			Log.e("DEBUG", "error parsing date " + track.getCreatedAt());
		}
        
        TextView nameView = (TextView) view.findViewById(R.id.trackName);
        String formattedName = "<b> <font color='#7B0099'>" + track.getTitle() + "</font></b>\n";
        nameView.setText(Html.fromHtml(formattedName));

        TextView usernameView = (TextView) view.findViewById(R.id.list_username);
        String formattedUsername = "<b><small><font color='#7B0099'>" + track.getUser().getName() + "</font></small></b>\n";
        usernameView.setText(Html.fromHtml(formattedUsername));
        
        TextView metaView = (TextView) view.findViewById(R.id.trackMeta);
        String formattedMeta = "<b> <small><font color='#7B0099'>" + track.getSharing() + "</font></small></b>";
        formattedMeta = formattedMeta + " <small><font color='#777777'> " +
                date + "</font></small>";
        metaView.setText(Html.fromHtml(formattedMeta));
        
        return view;
	}
	
	public static String convertMillis(long Millis){
		long years = Millis/(1000*60*60*24*30*12);
		long months = Millis/(1000*60*60*24*30) - years*12;
		long days = Millis/(1000*60*60*24) - months*30;
		long hours = Millis/(1000*60*60) - days*24;
		long mins = (Millis%(1000*60*60))/(1000*60);
		long seconds = ((Millis%(1000*60*60))%(1000*60))/1000;
		String output = "";
		if (years > 0) {
			output = output + String.format(Locale.US, "%d year(s) ,", years);
		}
		if (months > 0) {
			output = output + String.format(Locale.US, "%d month(s) ,", months);
		}
		if (days > 0) {
			output = output + String.format(Locale.US, "%d day(s) ,", days);
		}
		if (hours > 0) {
			output = output + String.format(Locale.US, "%d hour(s) ,", hours);
		}
		if (mins > 0) {
			output = output + String.format(Locale.US, "%d min(s) ,", mins);
		}
		if (seconds > 0) {
			output = output + String.format(Locale.US, "%d seconds", seconds);
		}
		
		
//		String convert = String.format(Locale.US, "%d hour(s), %d minute(s), and %d second(s)",
//                Millis/(1000*60*60), (Millis%(1000*60*60))/(1000*60), ((Millis%(1000*60*60))%(1000*60))/1000);
		
		String convert = String.format(Locale.US, "%d years, %d months, %d days, %d hour(s), %d minute(s), and %d second(s)",years, months, days
                ,hours, (Millis%(1000*60*60))/(1000*60), ((Millis%(1000*60*60))%(1000*60))/1000);
		
        return output;
    }
}
