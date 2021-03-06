package com.irobot;

import javax.microedition.khronos.opengles.GL10;

import rajawali.animation.mesh.SkeletalAnimationObject3D;
import rajawali.animation.mesh.SkeletalAnimationSequence;
import rajawali.lights.DirectionalLight;
import rajawali.parser.ParsingException;
import rajawali.parser.md5.LoaderMD5Anim;
import rajawali.parser.md5.LoaderMD5Mesh;
import rajawali.renderer.RajawaliRenderer;
import android.content.Context;
import android.util.FloatMath;
import android.view.MotionEvent;

import com.irobot.R;
public class LoadMD5Renderer extends RajawaliRenderer {		
	private DirectionalLight mLight;
	private SkeletalAnimationObject3D mObject;
	private SkeletalAnimationSequence mSequenceWalk;
	private SkeletalAnimationSequence mSequenceIdle;
	private SkeletalAnimationSequence mSequenceArmStretch;
	private SkeletalAnimationSequence mSequenceBend;
	private static IRobotActivity activity;	
	private float mNewDist;
	private float mOldDist;	
	private int mode,direction=0;
	private int ZOOM = 3;
	private int NONE = 1;	
	float mPreviousX, mPreviousY, dx, dy,x,y,yAngle,xAngle;
	float angle,dist=6;
	String action,status;
	private final float SCROLL_THRESHOLD = 10;
	private boolean isOnClick;
	public LoadMD5Renderer(Context context,IRobotActivity activity) {
		super(context);
		setFrameRate(30);	
		this.activity = activity;
	}		
	int i=1;		
	public void Switch() {
		 if(i==1)
		 {
			mObject.transitionToAnimationSequence(mSequenceBend, 1000);	   
	        i=2;
		 }
        else if(i==2)
        {
        	mObject.transitionToAnimationSequence(mSequenceArmStretch, 1000);        	
        	i=3;
        }
        else if(i==3)
        {
        	mObject.transitionToAnimationSequence(mSequenceIdle, 1000);        	
        	i=4;
        }
        else if(i==4)
        {        	
        	mObject.transitionToAnimationSequence(mSequenceWalk, 1000);	        	
        	i=1;
        }
	}
	public void SetOnTouchEvent(MotionEvent event) {			
		switch (event.getAction() & MotionEvent.ACTION_MASK) {	
		case MotionEvent.ACTION_DOWN:	
			if(!isOnClick)
			{
			mPreviousX = event.getRawX();
			mPreviousY = event.getRawY();
			direction=0;
			}
			action="ACTION_DOWN";
			isOnClick = true;
			break;			
		case MotionEvent.ACTION_POINTER_DOWN:			
			mOldDist = spacing(event);			
			if (mOldDist > 10f) {
				mode = ZOOM;				
			}	
			action="ACTION_POINTER_DOWN";
			break;	
		case MotionEvent.ACTION_POINTER_UP:						
			mode = NONE;
			isOnClick = false;
			action="ACTION_POINTER_UP";
			direction=0;
			break;
		case MotionEvent.ACTION_UP:			
			mode = NONE;	
			isOnClick = false;
			action="ACTION_UP";
			direction=0;
			break;		
		case MotionEvent.ACTION_MOVE:	
			if (isOnClick && (Math.abs(mPreviousX - event.getX()) > SCROLL_THRESHOLD ||
					Math.abs(mPreviousY - event.getY()) > SCROLL_THRESHOLD)) {
				action="ACTION_MOVE";
				if (mode == ZOOM) {
					mNewDist = spacing(event);					
						if(mNewDist>mOldDist)					
							dist=dist+(mNewDist/350);
						else					
							dist=dist-(mNewDist/350);
						if(dist>100)dist=100;
						if(dist<6)dist=6;				
				}	
				else{	
					x = event.getX();
					y = event.getY();
					dx = x - mPreviousX;
					dy = y - mPreviousY;	
					 if(Math.abs(dx) > Math.abs(dy)) 
		                {
		                    if(dx>0) 
		                    {
		                    	status="RIGHT";
		                    	direction=1;
		                    }
		                    else if (dx < 0)
		                    {
		                    	status="LEFT";    
		                    	direction=2;
		                    }
		                } 
		                else  if(Math.abs(dx) < Math.abs(dy)) 
		                {
		                	if(dy>0) 
		                    {
		                		status="DOWN";
		                    	direction=3;
		                    }
		                    else if (dy < 0)
		                    {
		                    	status="UP";    
		                    	direction=4;
		                    }           
		                }else
		                	direction=0;	                
				   	angle=(dy-dx);				   
				}
			}
			break;	
		}
		activity.setInfoText(				
				String.format("X: %.2f", x) + "\r\n" +String.format("DX: %.2f", dx)+ "\r\n" +
				String.format("Y: %.2f", y) + "\r\n" +String.format("DY: %.2f", dy) +"\r\n" +
				String.format("Angle: %.2f", angle) + "\r\n" +
				String.format("Zoom: %.2f", dist) + "\r\n" +
				status + "\r\n" + action
				);	
		
	}
	
 private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);		
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y); 
	}
	protected void initScene() {
		mLight = new DirectionalLight(0, -0.2f, -1.0f); // set the direction
		mLight.setColor(1.0f, 1.0f, .8f);
		mLight.setPower(1);

		getCurrentScene().addLight(mLight);
		getCurrentCamera().setZ(dist);

		try {
			LoaderMD5Mesh meshParser = new LoaderMD5Mesh(this,
					R.raw.ingrid_mesh);
			meshParser.parse();

			LoaderMD5Anim animParser = new LoaderMD5Anim("idle", this,
					R.raw.ingrid_idle);
			animParser.parse();

			mSequenceIdle = (SkeletalAnimationSequence) animParser
					.getParsedAnimationSequence();

			animParser = new LoaderMD5Anim("walk", this, R.raw.ingrid_walk);
			animParser.parse();

			mSequenceWalk = (SkeletalAnimationSequence) animParser
					.getParsedAnimationSequence();

			animParser = new LoaderMD5Anim("armstretch", this,
					R.raw.ingrid_arm_stretch);
			animParser.parse();

			mSequenceArmStretch = (SkeletalAnimationSequence) animParser
					.getParsedAnimationSequence();

			animParser = new LoaderMD5Anim("bend", this, R.raw.ingrid_bend);
			animParser.parse();

			mSequenceBend = (SkeletalAnimationSequence) animParser
					.getParsedAnimationSequence();

			mObject = (SkeletalAnimationObject3D) meshParser
					.getParsedAnimationObject();
			mObject.setAnimationSequence(mSequenceWalk);
			mObject.setFps(24);
			mObject.setScale(.8f);
			mObject.play();

			addChild(mObject);
		} catch (ParsingException e) {
			e.printStackTrace();
		}
	}
	 @Override
	    public void onDrawFrame(GL10 glUnused) {
	        super.onDrawFrame(glUnused);
	        getCurrentCamera().setZ(dist);	     
		       // mObject.setRotation(mAccValues.x, mAccValues.y, mAccValues.z);	 
		        getCurrentCamera().setZ(dist);
		        if(direction==1)
		        	mObject.setRotY(mObject.getRotY()-2);
		        else if(direction==2)
		        	mObject.setRotY(mObject.getRotY()+2);
		        else if(direction==3)
		        	mObject.setRotX(mObject.getRotX()-2);
		        else if(direction==4)
		        	mObject.setRotX(mObject.getRotX()+2);
	    }		 

}


