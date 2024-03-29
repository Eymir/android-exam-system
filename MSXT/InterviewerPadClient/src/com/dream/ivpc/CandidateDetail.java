package com.dream.ivpc;

import java.io.File;
import java.util.List;
import com.artifex.mupdfdemo.MuPDFActivity;
import com.dream.ivpc.R;
import com.dream.ivpc.activity.exam.ExamRptList;
import com.dream.ivpc.activity.interview.InterviewResult;
import com.dream.ivpc.activity.interview.InterviewResultSubmit;
import com.dream.ivpc.activity.resume.ResumePicture;
import com.dream.ivpc.activity.resume.ResumeWebView;
import com.dream.ivpc.adapter.PhaseHistoryAdapter;
import com.dream.ivpc.bean.CandidateBean;
import com.dream.ivpc.bean.Round;
import com.dream.ivpc.server.DAOProxy;
import com.dream.ivpc.server.DAOProxyLocalImp;
import com.dream.ivpc.util.NetWorkUtil;
import com.dream.ivpc.util.SPUtil;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class CandidateDetail extends BaseActivity {
	public final static String LOG_TAG = "CandidateDetail";
	Context mContext;
	
	ImageView imgGoBack = null;
	TextView canInfoTV;
	
	TextView currRoundTV;
	TextView currRoundTimeTV;
	
	ProgressBar progressBar;

	CandidateBean canBean;
	ListView listview;
	PhaseHistoryAdapter adapter;
	
	int resumeType = 1;
	
//	String candidateId;
	
    public void loadPictureResume(){
    	Intent intent = new Intent();
		intent.setClass( mContext, ResumePicture.class);
		startActivity(intent);     	
    }
    
    public void loadPdfResume(String adminFolder, String candidateFolder){
		String basePath = Environment.getExternalStorageDirectory()
				.getPath();
		String pdfPath = basePath + File.separator + "interviewer"
				+ File.separator + adminFolder 
				+ File.separator + candidateFolder 
				+ File.separator+ "resume.pdf";
		
		Uri uri = Uri.parse(pdfPath);
		Intent intent = new Intent(mContext, MuPDFActivity.class);
		intent.setAction(Intent.ACTION_VIEW);
		intent.setData(uri);
		startActivity(intent);
    }
    
    public void loadHtmlResume(){
    	Intent intent = new Intent();
		intent.setClass( mContext, ResumeWebView.class);
		startActivity(intent); 
    }
    
    public void submitResult(){
    	Intent intent = new Intent();
		intent.setClass( mContext, InterviewResultSubmit.class);
		startActivity(intent); 
    }
    
	public void loadCandidateBase(){
        //set candidate information value
		Bundle bundle = this.getIntent().getExtras();
		String name  = bundle.getString("name");
		String position  = bundle.getString("position");
//		String phase  = bundle.getString("phase");
//		candidateId = bundle.getString("candidateId");
		
		((TextView) this.findViewById(R.id.nameTV)).setText(name);
		((TextView) this.findViewById(R.id.positionTV)).setText(position);
		
		Button viewResume = (Button) this.findViewById(R.id.viewResume);
		viewResume.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				switch(resumeType){
					case 1: loadPictureResume();break;
					case 2: loadPdfResume("admin","tangqi");break;
					case 3: loadHtmlResume();break;
				}
				
			}
		});
		
		Button submitBtn = (Button) this.findViewById(R.id.submitBtn);
		submitBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				submitResult();
			}
		});
	}
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	Log.i(LOG_TAG,"onCreate...");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.candidate_detail);
        mContext = getApplicationContext();

        //set header
        imgGoBack = (ImageView) this.findViewById(R.id.imgGoBack);
        imgGoBack.setOnClickListener(goBackListener);
        canInfoTV = (TextView) this.findViewById(R.id.candidateInfo);
        canInfoTV.setText("Candidate Detail");
        
        progressBar = (ProgressBar) findViewById(R.id.loading_can_list);
        
        //set candidate detail
        loadCandidateBase();
		
        listview = (ListView) findViewById(R.id.listview);
        listview.setOnItemClickListener(new ItemClickListener());
        
        currRoundTV = (TextView) this.findViewById(R.id.currRoundTV);
        currRoundTimeTV = (TextView) this.findViewById(R.id.currRoundTimeTV);	
        
        String adminId = SPUtil.getFromSP(SPUtil.SP_KEY_USER, sharedPreferences);
        String candidateId = "tangqi";
        if(NetWorkUtil.isNetworkAvailable(mContext)){
        	new GetDataTask().execute(new String[]{adminId,candidateId});
        }else{
        	Toast.makeText(mContext, "Your network is not available!", Toast.LENGTH_LONG).show();
        }
    }

    View.OnClickListener goBackListener = new View.OnClickListener() {  
        @Override  
        public void onClick(View v) { 
        	Intent intent = new Intent();
			intent.setClass( mContext, CandidateList.class);
			startActivity(intent);  
        }  
    };
    
	class ItemClickListener implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
			Log.i(LOG_TAG,"arg2:"+String.valueOf(arg2));
			List<Round> doneRounds = canBean.getDoneRounds();
			if (doneRounds != null && doneRounds.size() > 0) {
				Round round = doneRounds.get(arg2);
				if (round.getType().equalsIgnoreCase("EXAM")) {
					openExamReport();
				} else {
					openInterviewResult();
				}
			}
		}
	}
 
    private class GetDataTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            // Simulates a background job.
            try {
                Thread.sleep(500);
        		DAOProxy proxy = new DAOProxyLocalImp();
        		canBean = proxy.getCandidateDetail(urls[0], urls[1]);
            } catch (InterruptedException e) {
            }
            return null;
        }
        
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            List<Round> doneRounds = canBean.getDoneRounds();
			if (doneRounds != null && doneRounds.size() > 0) {
				progressBar.setVisibility(View.GONE);
				if (adapter == null) {
					adapter = new PhaseHistoryAdapter(doneRounds, mContext);
					listview.setAdapter(adapter);
					listview.setOnItemClickListener(new ItemClickListener());
				} else {
					adapter.notifyDataSetChanged();
				}
				currRoundTV.setText(canBean.getCurrRound().getName());
				currRoundTimeTV.setText(canBean.getCurrRound().getPlanTime());
			} else {
				Toast.makeText(mContext, "Can not get any data!",Toast.LENGTH_LONG).show();
			}
        }
    }

    public void openInterviewResult(){
    	Intent intent = new Intent();
		intent.setClass( mContext, InterviewResult.class);
		startActivity(intent); 
    }
    
    public void openExamReport(){
    	Intent intent = new Intent();
		intent.setClass( mContext, ExamRptList.class);
		startActivity(intent);  
    }
    
}
