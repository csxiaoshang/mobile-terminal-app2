package co.herxun.impp.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;
import co.herxun.impp.IMppApp;
import co.herxun.impp.model.Like;
import co.herxun.impp.model.Post;
import co.herxun.impp.model.Event;
import co.herxun.impp.model.User;
import co.herxun.impp.utils.DBug;

import com.activeandroid.query.Select;
import com.arrownock.exception.ArrownockException;
import com.arrownock.social.AnSocial;
import com.arrownock.social.AnSocialMethod;
import com.arrownock.social.IAnSocialCallback;

public class EventManager extends Observable{
	private String wallId;
	private Set<String> friendSet;
	private ArrayList<Event> eventList;
	private AnSocial anSocial;
	private Handler handler;
	private Context ct;
	private final static int POST_LIMIT = 20;
    static String eventId;
	
	private int page = 0;
	private int totalEventCount = 0;
	
	public EventManager(Context ct, String wallId, Set<String> friendSet){
		this.ct = ct;
		this.wallId = wallId;
		this.friendSet = friendSet;
		friendSet.add( UserManager.getInstance(ct).getCurrentUser().userId);
		
		handler = new Handler();
		anSocial = ((IMppApp)ct.getApplicationContext()).anSocial;
	}
	
	public boolean canLoadMore(){
		DBug.e("totalPostCount",eventList.size() +","+ totalEventCount);
		return eventList.size() < totalEventCount;
	}
	
	public void init(final FetchEventCallback callback){
		page = 0;
		eventList = new ArrayList<Event>();
		fetchRemoteEvent(++page,new FetchEventCallback(){
			@Override
			public void onFailure(String errorMsg) {
				page--;   
				//getLocalPosts(callback);
			}
			@Override
			public void onFinish(List<Event> data) {
				eventList.addAll(data);
				if(callback!=null){
					callback.onFinish(data);
				}
			}
		});

	}
	
	public void loadMore(final FetchEventCallback callback){
		fetchRemoteEvent(++page,new FetchEventCallback(){
			@Override
			public void onFailure(String errorMsg) {
				page--;
				if(callback!=null){
					callback.onFailure(errorMsg);
				}
			}
			@Override
			public void onFinish(List<Event> data) {
				eventList.addAll(data);
				if(callback!=null){
					callback.onFinish(eventList);
				}
			}
		});

	}
	
	
	
	private void fetchRemoteEvent(final int page,final FetchEventCallback callback){
		new Thread(new Runnable(){
			@Override
			public void run() {
				
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("event_id", eventId);
				try{
				anSocial.sendRequest("events/query.json", AnSocialMethod.GET, params,
					    new IAnSocialCallback() {
					        @Override
					        public void onFailure(final JSONObject response) {
					            try {
					                System.out.println("the error message: " + response.getJSONObject("meta").getString("message"));
					            } catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}

					            handler.post(new Runnable(){
									@Override
									public void run() {
										if(callback!=null){
											callback.onFailure(response.toString());
										}
									}
								});

					        }
					        @Override
					        public void onSuccess(JSONObject response) {
					            try {
					                JSONArray eventsArray = response.getJSONObject("response").getJSONArray("events");
					                for (int i = 0; i < eventsArray.length(); i++) {
					                    JSONObject event = (JSONObject) eventsArray.get(i);
					                    System.out.println(event.get("id"));
					                }
					            } catch (Exception e) {

					            }
					        }
					    });
				}catch (final ArrownockException e) {
					e.printStackTrace();
				}

				
				
				
			}
		}).start();
		
	}
	
	
	public static void createEvent(final Context context,final String user_id,final String str_type, 
			final String info,final String time,final IAnSocialCallback callback){
		 new Thread(new Runnable(){
			   @Override
			   public void run() {
		   try{
			   System.out.println(time);
			   Map<String, Object> params = new HashMap<String, Object>();
			   params.put("title", str_type);
			   params.put("information", info);
			   params.put("start_time", time);
			   params.put("stop_time", time);			  
			   AnSocial anSocial = ((IMppApp)context.getApplicationContext()).anSocial;
			   anSocial.sendRequest("events/create.json", AnSocialMethod.POST, params,new IAnSocialCallback() {
			           @Override
			           public void onFailure(JSONObject response) {
			
			               try {
			                   System.out.println("the error message: " + response.getJSONObject("meta").getString("message"));
			               } catch (Exception e1) {

			               }
			           }
			           @Override
			           public void onSuccess(JSONObject response) {
			        	  
			               try {	

			                   System.out.println("event id is: " + response.getJSONObject("response").getJSONObject("event").getString("id"));
			                   eventId=response.getJSONObject("response").getJSONObject("event").getString("id");
			               } catch (Exception e1) {

			               }
			           }
			       });
		   }catch(ArrownockException e){
			   e.printStackTrace();
		   }
				   	   	   
		}
		}).start();
	}
	public interface FetchEventCallback{
		public void onFailure(String errorMsg);
		public void onFinish(List<Event> data);
	}

	
	
}
