package co.herxun.impp.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import org.json.JSONException;
import org.json.JSONObject;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

public class Event  extends Model {
	@Column(name = "eventId")
	public String eventId; 
	
		@Column(name = "title")
		public String title; 
		
		@Column(name = "information")
		public String information; 
		
		@Column(name = "start_time")
		public String start_time; 
		
		
		public void parseJSON(JSONObject json){
			try {
				title = json.getString("title");
				information = json.getString("information");
				start_time = json.getString("start_time"); 			
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
	
}
