package com.irobot;
import rajawali.RajawaliActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
public class IRobotActivity extends RajawaliActivity{ 
	public LoadMD5Renderer mmd5Renderer; 
	public LoadOBJRenderer mobjRenderer; 
	public LoadMD5AccRenderer mmd5accRenderer; 
	
    TextView info;
    Button switchaction;	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/*mobjRenderer = new LoadOBJRenderer(this,this);
		mobjRenderer.setSurfaceView(mSurfaceView);	    
	    super.setRenderer(mobjRenderer);*/
	    mmd5Renderer = new LoadMD5Renderer(this,this);
	    mmd5Renderer.setSurfaceView(mSurfaceView);	    
	    super.setRenderer(mmd5Renderer);		
		LayoutInflater inflater = getLayoutInflater();
	    View mainView = inflater.inflate(R.layout.activity_main, null);	   
	    getWindow().addContentView(mainView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,ViewGroup.LayoutParams.FILL_PARENT));		    
	    info=(TextView) findViewById(R.id.textinfo);	
	    mSurfaceView.bringToFront();
	    switchaction=(Button) findViewById(R.id.btn_switch);
	    switchaction.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {		
				mmd5Renderer.Switch();
			}
	    });
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		mmd5Renderer.SetOnTouchEvent(event);
    	return true;
    }
	public void setInfoText(String text)
	{
		info.setText(text);
	}
	
}
