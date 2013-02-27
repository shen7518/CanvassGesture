package vassilina.tatarlieva.welcomeactivity;

import java.util.HashMap;
import android.content.Context;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

public class CardLayout extends FrameLayout implements Animation.AnimationListener {
	private HashMap<String, View> cards = new HashMap<String, View>();
	private String current;
	private boolean toFront;
	
	
	public CardLayout(Context context) {
		super(context);
		this.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
	}
	
	public void add(View v, String name){
		v.setVisibility(VISIBLE);
		if( this.current != null )
		{
			this.getView( this.current ).setVisibility(INVISIBLE);
		}
		cards.put(name, v);
		v.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		this.addView(v);
		this.bringChildToFront(v);
		this.current = name;
	}
	
	public View getView(String name){
		return this.cards.get(name);
	}
	
	public String getCurrent(){
		return this.current;
	}
	
	public void showView(String name, boolean toFront){
		if(this.current == name){
			return;
		}
		this.toFront = toFront;
		this.current = name;
		if(this.toFront){
			this.applyRotation(0, 90);
		}else{
			this.applyRotation(0, -90);
			
		}
	}
	
	 private void applyRotation(float start, float end) {
        final float centerX = this.getWidth() / 2.0f;
        final float centerY = this.getHeight() / 2.0f;
        Rotation3D rotation =
                new Rotation3D(start, end, centerX, centerY, 310.0f, true, null);
        rotation.setDuration(200);
        rotation.setFillAfter(true);
        rotation.setFillEnabled(true);
        rotation.setInterpolator(new AccelerateInterpolator());
        rotation.setAnimationListener(this);
        this.startAnimation(rotation);
    }

	@Override
	public void onAnimationEnd(Animation animation) {
		for(View v : cards.values()){
			v.setVisibility(View.INVISIBLE);
		}
    	View v1 =cards.get(this.current);
		Rotation3D rotation;
		
		if(this.toFront){			
			rotation = new Rotation3D(
					-90, 0, this.getWidth() / 2.0f, this.getHeight() / 2.0f, 310.0f, false, v1);
		}else{
			rotation = new Rotation3D(
					90, 0, this.getWidth() / 2.0f, this.getHeight() / 2.0f, 310.0f, false, v1);	
		}
		
        rotation.setDuration(200);
        rotation.setFillAfter(false);
        rotation.setFillBefore(false);
        rotation.setFillEnabled(true);
        rotation.setInterpolator(new DecelerateInterpolator());
        
		this.startAnimation(rotation);
	}

	@Override
	public void onAnimationRepeat(Animation arg0) {}

	@Override
	public void onAnimationStart(Animation arg0) {
	}
	

}