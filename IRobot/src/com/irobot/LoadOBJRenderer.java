package com.irobot;

import javax.microedition.khronos.opengles.GL10;

import rajawali.Object3D;
import rajawali.animation.mesh.SkeletalAnimationObject3D;
import rajawali.animation.mesh.SkeletalAnimationSequence;
import rajawali.lights.ALight;
import rajawali.lights.DirectionalLight;
import rajawali.parser.LoaderOBJ;
import rajawali.parser.ParsingException;
import rajawali.parser.md5.LoaderMD5Anim;
import rajawali.parser.md5.LoaderMD5Mesh;
import rajawali.renderer.RajawaliRenderer;
import android.content.Context;
import android.util.FloatMath;
import android.view.MotionEvent;
import com.irobot.R;
public class LoadOBJRenderer extends RajawaliRenderer {		
		private Object3D mObjectGroup;	
		private static IRobotActivity activity;	
		private float mNewDist;
		private float mOldDist;	
		private int mode,direction=0;
		private int ZOOM = 3;
		private int NONE = 1;	
		float mPreviousX, mPreviousY, dx, dy,x,y,yAngle,xAngle;
		float angle,dist=85;
		String action,status;
		private final float SCROLL_THRESHOLD = 10;
		private boolean isOnClick;
		public LoadOBJRenderer(Context context,IRobotActivity activity) {
			super(context);
			setFrameRate(30);	
			this.activity = activity;
		}	
		public void Switch() {
			
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
							if(dist<0)dist=0;				
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
		private DirectionalLight mLight;
		private SkeletalAnimationObject3D mObject;
		
		protected void initScene() {
	
			 ALight light = new DirectionalLight(-1, 0, -1);
             light.setPower(3);             
             getCurrentScene().addLight(light);   
             getCurrentCamera().setZ(dist);
             getCurrentCamera().setLookAt(0, 0, 0);   
             getCurrentCamera().setFarPlane(1000);
			LoaderOBJ objParser = new LoaderOBJ(mContext.getResources(),
					mTextureManager, R.raw.bolvanka_obj);
			try {
				objParser.parse();
				mObjectGroup = objParser.getParsedObject();
				addChild(mObjectGroup);
				mObjectGroup.setY(-20.0f);	
				mObjectGroup.setBackSided(true);
			} catch (ParsingException e) {
				e.printStackTrace();
			}
		}
		
		 @Override
		    public void onDrawFrame(GL10 glUnused) {
		        super.onDrawFrame(glUnused);
		        getCurrentCamera().setZ(dist);
		        if(direction==1)
		        mObjectGroup.setRotY(mObjectGroup.getRotY() - 1);	
		        else if(direction==2)
			        mObjectGroup.setRotY(mObjectGroup.getRotY() + 1);	
		        else if(direction==3)
			        mObjectGroup.setRotX(mObjectGroup.getRotX() - 1);	
		        else if(direction==4)
			        mObjectGroup.setRotX(mObjectGroup.getRotX() + 1);	
		    }		 

	}


