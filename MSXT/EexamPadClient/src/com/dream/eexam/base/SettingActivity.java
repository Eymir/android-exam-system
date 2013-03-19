package com.dream.eexam.base;

import com.dream.eexam.util.SPUtil;
import com.dream.eexam.util.ValidateUtil;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class SettingActivity extends BaseActivity {

	public final static String LOG_TAG = "LoginActivity";
	
	Context mContext;
	
	ImageView imgHome = null;
	
	TextView valiMessageTV = null;
	String[] valiMessageArray = null;
	EditText hostET = null;
	String saveHost = null;
	
	Button saveBtn = null;
	Button clearBtn = null;
	Button cancelBtn = null;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	Log.i(LOG_TAG,"onCreate...");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);
        
        mContext = getApplicationContext();
        
		imgHome = (ImageView) findViewById(R.id.imgHome);
		imgHome.setOnClickListener(cancelListener);
		
        valiMessageTV = (TextView) this.findViewById(R.id.valiMessage);
        valiMessageArray = getResources().getStringArray(R.array.msg_settings_invalid);
        
        hostET = (EditText) this.findViewById(R.id.hostET);
        saveHost = sharedPreferences.getString("host", null);
		if(saveHost!=null||!"".equals(saveHost)){
			hostET.setText(saveHost);
		}
		
		saveBtn = (Button) this.findViewById(R.id.saveBtn);
		saveBtn.setOnClickListener(saveListener);
		
		clearBtn = (Button) this.findViewById(R.id.clearBtn);
		clearBtn.setOnClickListener(clearListener);
		
		cancelBtn = (Button) this.findViewById(R.id.cancelBtn);
		cancelBtn.setOnClickListener(saveListener);
		
    }

    View.OnClickListener saveListener = new View.OnClickListener() {  
        @Override  
        public void onClick(View v) { 
        	String host = hostET.getText().toString();
        	if(ValidateUtil.validateIP4(host)){
//    			SharedPreferences.Editor editor = sharedPreferences.edit();
//    			editor.putString("host", host);
//    			editor.commit();	
    			SPUtil.save2SP(SPUtil.SP_KEY_HOST, host, sharedPreferences);
            	//go to login page
            	Intent intent = new Intent();
    			intent.setClass( SettingActivity.this, LoginActivity.class);
    			startActivity(intent);          		
        	}else{
        		valiMessageTV.setVisibility(View.VISIBLE);
        		valiMessageTV.setText(valiMessageArray[0]);
        	}

        }  
    };
    
    
    View.OnClickListener clearListener = new View.OnClickListener() {  
        @Override  
        public void onClick(View v) { 
        	clearSP();
        	clearDB(mContext);
        	
        	ShowDialog(mContext.getResources().getString(R.string.dialog_note),
        			mContext.getResources().getString(R.string.msg_history_be_cleared));	
        }  
    };   
    
    View.OnClickListener cancelListener = new View.OnClickListener() {  
        @Override  
        public void onClick(View v) { 
        	//go to login page
        	Intent intent = new Intent();
			intent.setClass( SettingActivity.this, LoginActivity.class);
			startActivity(intent);  	
        }  
    };
    


}