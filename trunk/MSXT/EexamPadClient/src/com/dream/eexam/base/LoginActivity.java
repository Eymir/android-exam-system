package com.dream.eexam.base;

import java.io.ByteArrayInputStream;
import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.dream.eexam.model.InterviewBean;
import com.dream.eexam.util.SystemConfig;
import com.msxt.client.server.ServerProxy;
import com.msxt.client.server.WebServerProxy;
import com.msxt.client.server.ServerProxy.Result;
import com.msxt.client.server.ServerProxy.STATUS;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends BaseActivity {

	public final static String LOG_TAG = "LoginActivity";
	
	EditText idEt = null;
	EditText passwordET = null;
	String saveId = null;
	String savePassword = null;
	
	Button loginBtn = null;

	InterviewBean bean;
//	String loginURL = null;
	String loginResultFile = null;
	String loginResultFilePath = null;
//	String loginStatus;
	

	
	private Context mContext;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	Log.i(LOG_TAG,"onCreate...");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        mContext = getApplicationContext();
		
        idEt = (EditText) this.findViewById(R.id.idEt);
		saveId = sharedPreferences.getString("id", null);
		if(saveId!=null||!"".equals(saveId)){
			idEt.setText(saveId);
		}
		
		passwordET = (EditText) this.findViewById(R.id.passwordET);
		savePassword = sharedPreferences.getString("password", null);
		if(savePassword!=null||!"".equals(savePassword)){
			passwordET.setText(savePassword);
		}
		
		loginBtn = (Button) this.findViewById(R.id.loginBtn);
		loginBtn.setOnClickListener(loginListener);
		
    }

    View.OnClickListener loginListener = new View.OnClickListener() {  
        @Override  
        public void onClick(View v) { 
        	Button lBtn = (Button)v;
        	lBtn.setEnabled(false);
        	
        	String id = idEt.getText().toString();
        	String password = passwordET.getText().toString();
        	
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putString("id", id);
			editor.putString("password", password);
			editor.commit();		
    		
//        	loginURL = SystemConfig.getInstance().getPropertyValue("Login_URL")+"loginName="+id+"&loginPassword="+password;
        	loginResultFile = SystemConfig.getInstance().getPropertyValue("Login_Result");
        	loginResultFilePath = Environment.getExternalStorageDirectory().getPath()+ File.separator + "eExam";
        	if (getWifiIP() != null && getWifiIP().trim().length() > 0 && !getWifiIP().trim().equals("0.0.0.0")){
        		new DownloadXmlTask().execute(new String[]{id,password});
        	}else{
        		ShowDialog("Your mobile is not in a available NetWork!");
        	}
        	
        }  
    };
    
    
    private class DownloadXmlTask extends AsyncTask<String, Void, String> {
    	ProgressDialog progressDialog;
    	ServerProxy proxy;
    	Result loginResult;
    	
    	@Override
    	protected void onPreExecute() {
    		Log.i(LOG_TAG, "onPreExecute() called");
    		progressDialog = ProgressDialog.show(LoginActivity.this, null, "Login to server...", true, false); 
    	}
    	
        @Override
		protected String doInBackground(String... urls) {
/*			InputStream inputStream = downloadUrl(urls[0],urls[1]);// get inputStream// from server
        	if(inputStream!=null){
        		loginResult = new Result();
        		loginResult.setStatus(STATUS.SUCCESS);
        		try {
					loginResult.setSuccessMessage(inputStream2String(inputStream));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	}else{
        		loginResult = new Result();
        		loginResult.setStatus(STATUS.ERROR);
        	}*/
        	String host = mContext.getResources().getString(R.string.host);
        	Integer port = Integer.valueOf(mContext.getResources().getString(R.string.port));
        	proxy =  WebServerProxy.Factroy.createInstance(host, port);
    		loginResult = proxy.login(urls[0], urls[1]);
    		
//    		if(STATUS.SUCCESS.equals(loginResult.getStatus())){
//    			saveFile(loginResultFilePath, loginResultFile, loginResult.getSuccessMessage());
//    		}else if(STATUS.ERROR.equals(loginResult.getStatus())){
//    			ShowDialog(loginResult.getErrorMessage());
//    			this.cancel(true);
//    		}
//        	InputStream inputStream = LoginActivity.class.getClassLoader().getResourceAsStream(loginResultFile);
//			try {
//				//save login result to local
//				saveFile(loginResultFilePath, loginResultFile, inputStream2String(inputStream));
//				//read login result from local
//				FileInputStream inputStream2 = new FileInputStream(new File(loginResultFilePath + File.separator + loginResultFile));
//				loginStatus = XMLParseUtil.readLoginResultStatus(inputStream2);
//				inputStream2.close();
//			} catch (IOException e) {
//				Log.i(LOG_TAG,e.getMessage());
//			} catch (Exception e) {
//				Log.i(LOG_TAG,e.getMessage());
//			}
			return null;
		}

        @Override
        protected void onPostExecute(String result) {
        	progressDialog.dismiss();
        	loginBtn.setEnabled(true);

        	/*if(STATUS.SUCCESS.equals(loginResult.getStatus())){
    			Intent intent = new Intent();
    			intent.putExtra("loginResultFile", loginResultFile);
    			intent.putExtra("loginResultFilePath", loginResultFilePath);
    			intent.setClass( mContext, ExamListActivity.class);
    			startActivity(intent);        		
        	}else{
        		ShowDialog("Can not login server!");
        	}*/
        	
    		if( loginResult.getStatus() == STATUS.ERROR ) {
    			ShowDialog(loginResult.getErrorMessage());
        	} else {
        		saveFile(loginResultFilePath, loginResultFile, loginResult.getSuccessMessage());
        		
        		try{
    		    	DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    		        ByteArrayInputStream is = new ByteArrayInputStream( loginResult.getSuccessMessage().getBytes() );
    		        Document doc = db.parse( is );
    		        is.close();
    		        
    		        Element root = doc.getDocumentElement();
    		        String status = root.getElementsByTagName("status").item(0).getTextContent();
    		        if( status.equals("failed") ) {
    		        	String desc = root.getElementsByTagName("desc").item(0).getTextContent();
    		        	ShowDialog(desc);
    		        } else {
    		        	String conversation = root.getElementsByTagName( "conversation" ).item(0).getTextContent();
    		        	proxy.setConversationId( conversation );
    		        	
    		        	//go to examList page
    		        	Intent intent = new Intent();
    	    			intent.putExtra("loginResultFile", loginResultFile);
    	    			intent.putExtra("loginResultFilePath", loginResultFilePath);
    	    			intent.setClass( mContext, ExamListActivity.class);
    	    			startActivity(intent);   
    		        }
        		} catch (Exception e) {
        			ShowDialog("Invalid XML Data");
    			}
        	}
        }
    }
    
    @Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}


}