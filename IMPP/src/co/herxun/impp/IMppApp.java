package co.herxun.impp;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.app.Application;
import android.os.Handler;
import android.util.Log;
import co.herxun.impp.activity.BaseActivity;
import co.herxun.impp.im.controller.IMManager;
import co.herxun.impp.utils.Constant;
import co.herxun.impp.utils.DBug;

import com.activeandroid.ActiveAndroid;
import com.arrownock.exception.ArrownockException;
import com.arrownock.live.AnLive;
import com.arrownock.push.AnPush;
import com.arrownock.push.AnPushCallbackAdapter;
import com.arrownock.push.AnPushStatus;
import com.arrownock.push.IAnPushRegistrationCallback;
import com.arrownock.social.AnSocial;

public class IMppApp extends Application {
	public AnSocial anSocial;
	public AnLive anLive;
	public AnPush anPush;
	
	private Set<BaseActivity> activeActivitySet;
	private boolean activityStatusChanging = false;
	private ActivityStatusChangeRunnable mActivityStatusChangeRunnable;
	
	@Override
	public void onCreate(){
		init();
	}
	
	private void init(){
		ActiveAndroid.initialize(this);
		activeActivitySet = new HashSet<BaseActivity>();
		mActivityStatusChangeRunnable = new ActivityStatusChangeRunnable();
		
		try {
			anSocial = new AnSocial(this,getString(R.string.app_key));
			
			anPush = AnPush.getInstance(this);
			anPush.setSecureConnection(true);
			anPush.setCallback(new AnPushCallbackAdapter(){
				@Override
				public void statusChanged(AnPushStatus currentStatus, ArrownockException exception) {
					
					if (currentStatus == AnPushStatus.ENABLE) {
						Log.i("push.statusChanged","Push status enalbed");
						
					} else if (currentStatus == AnPushStatus.DISABLE) {
						Log.e("push.statusChanged","Push status disabled");
					}
					
					if (exception != null) {
						Log.e("push.statusChanged","Push status changed with error occuring = "+ exception.toString() );
					}
				}
			});
			
			anPush.enable();
			
//			List<String> channel = new ArrayList<String>();
//			channel.add(Constant.PUSH_CHANNEL);
//			anPush.register(channel, new IAnPushRegistrationCallback() {
//                
//                @Override
//                public void onSuccess(String anid) {
//                    DBug.d("anPush register", "register successful. anid = " + anid);
//                    try {
//                        anPush.enable();
//                    } catch (ArrownockException e) {
//                        e.printStackTrace();
//                    }
//                }
//                
//                @Override
//                public void onError(ArrownockException exception) {
//                    DBug.e("anPush register", "register failed. exception=" + exception.getMessage());
//                    
//                }
//            });
		} catch (ArrownockException e) {
			e.printStackTrace();
		}
	}
	
	public void activityToForeground(BaseActivity act){
		activeActivitySet.add(act);
		activityStatusChanged();
	}
	
	public void activityToBackground(BaseActivity act){
		if(activeActivitySet.contains(act)){
			activeActivitySet.remove(act);
		}
		activityStatusChanged();
	}
	
	private void activityStatusChanged(){
		if(!activityStatusChanging){
			if(!mActivityStatusChangeRunnable.isRunning()){
				mActivityStatusChangeRunnable.reset();
				mActivityStatusChangeRunnable.run();
			}
		}else{
			mActivityStatusChangeRunnable.reset();
		}
		activityStatusChanging = true;
	}
	
	private class ActivityStatusChangeRunnable implements Runnable{
		private Handler handler;
		private int count = 0;
		private boolean isRunning = false;
		
		public ActivityStatusChangeRunnable(){
			handler = new Handler();
			reset();
		}
		@Override
		public void run() {
			if(count>0){
				isRunning = true;
				count--;
				handler.postDelayed(this, 500);
			}else{
				appStatusChanged();
				isRunning = false;
			}
		}
		
		public void reset(){
			count = 1;
		}
		
		public boolean isRunning(){
			return isRunning;
		}
	}
	
	
	private void appStatusChanged(){
		activityStatusChanging = false;
		boolean isAppForeground = activeActivitySet.size() > 0;
		DBug.e("isAppForeground",isAppForeground+"?");
		
		if(isAppForeground ){
			if(!IMManager.getInstance(this).getAnIM().isOnline() && IMManager.getInstance(this).getCurrentClientId()!=null){
				IMManager.getInstance(this).connect(IMManager.getInstance(this).getCurrentClientId());
			}
		}else{
			IMManager.getInstance(this).disconnect(false);
		}
	}
}
