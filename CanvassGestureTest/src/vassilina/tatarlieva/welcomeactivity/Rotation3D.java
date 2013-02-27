package vassilina.tatarlieva.welcomeactivity;

import android.graphics.Camera;
import android.graphics.Matrix;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class Rotation3D extends Animation {
	
	    private final float fromDegrees;
	    private final float toDegrees;
	    private final float centerX;
	    private final float centerY;
	    private final float depthZ;
	    private final boolean reverse;
	    private Camera camera;
	    private View v;

	    public Rotation3D(float fromDegrees, float toDegrees,
	            float centerX, float centerY, float depthZ, boolean reverse, View v) {
	        this.fromDegrees = fromDegrees;
	        this.toDegrees = toDegrees;
	        this.centerX = centerX;
	        this.centerY = centerY;
	        this.depthZ = depthZ;
	        this.reverse = reverse;
	        this.v = v;
	    }

	    @Override
	    public void initialize(int width, int height, int parentWidth, int parentHeight) {
	        super.initialize(width, height, parentWidth, parentHeight);
	        
	        this.camera = new Camera();
	    }

	    @Override
	    protected void applyTransformation(float interpolatedTime, Transformation t) {
	        final float fromDegrees = this.fromDegrees;
	        float degrees = fromDegrees + ((this.toDegrees - fromDegrees) * interpolatedTime);

	        final float centerX = this.centerX;
	        final float centerY = this.centerY;
	        final Camera camera = this.camera;

	        if(!this.reverse && interpolatedTime > 0 && this.v!= null && this.v.getVisibility() == View.INVISIBLE){
	    		this.v.setVisibility(View.VISIBLE);
	        }
	        
	        
	        
	        
	        final Matrix matrix = t.getMatrix();

	        camera.save();
	        
	        if (this.reverse) {
	            camera.translate(0.0f, 0.0f, this.depthZ * interpolatedTime);
	        } else {
	            camera.translate(0.0f, 0.0f, this.depthZ * (1.0f - interpolatedTime));
	        }
	
	        camera.rotateY(degrees);
	        camera.getMatrix(matrix);
	        camera.restore();

	        matrix.preTranslate(-centerX, -centerY);
	        matrix.postTranslate(centerX, centerY);
	    }
}
