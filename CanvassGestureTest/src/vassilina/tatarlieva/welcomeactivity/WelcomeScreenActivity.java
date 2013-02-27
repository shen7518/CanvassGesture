package vassilina.tatarlieva.welcomeactivity;

import java.lang.reflect.Array;

import vassilina.tatarlieva.canvassgesturetest.GridActivity;

import vassilina.tatarlieva.canvassgesturetest.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

public class WelcomeScreenActivity extends Activity implements View.OnClickListener {
	public static final String MAIN = "main";
	public static final String RECENT_FILES ="recentFiles";
	public static final int MENU_ITEM_DELETE_FILE = 1;
	private CardLayout cardLayout;
	private FileListLayout filesView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.cardLayout = new CardLayout(this);
		this.filesView = new FileListLayout(this);
		
		this.registerForContextMenu(this.cardLayout);
		
		LayoutInflater layoutInflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View welcomeView = layoutInflater.inflate(R.layout.main, this.cardLayout, false);
		welcomeView.findViewById(R.id.recentFiles).setOnClickListener(this);
		welcomeView.findViewById(R.id.createNewFile).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(WelcomeScreenActivity.this, GridActivity.class);
            	Bundle bundle = new Bundle();
            	bundle.putString(GridActivity.FILE_NAME_PARAM, "NewFile");
            	bundle.putBoolean(GridActivity.FROM_WHERE_PARAM, false);
            	i.putExtras(bundle);
            	startActivity(i);
			}
		});
		((ImageView)welcomeView.findViewById(R.id.logoImage)).setImageResource(R.drawable.icon);
		welcomeView.setBackgroundColor(Color.BLACK);
		this.cardLayout.add(this.filesView, WelcomeScreenActivity.RECENT_FILES );
		this.cardLayout.add(welcomeView, WelcomeScreenActivity.MAIN);
		setContentView(cardLayout);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		String[] files = this.getApplicationContext().fileList();
		((FileListLayout)this.cardLayout.getView(WelcomeScreenActivity.RECENT_FILES)).bindData(files);
		
	}
	
	@Override
	public void onBackPressed() {
		
		if(this.cardLayout.getCurrent() == WelcomeScreenActivity.RECENT_FILES){
			this.cardLayout.showView(WelcomeScreenActivity.MAIN, false);
		}else{
			super.onBackPressed();
		}
	}

	@Override
	public void onClick(View arg0) {
		this.cardLayout.showView(WelcomeScreenActivity.RECENT_FILES, true);
	}
	
	
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case WelcomeScreenActivity.MENU_ITEM_DELETE_FILE: 
			this.deleteFile( this.filesView.getCurrentFilename() );
			this.onResume();
			return true;
		default:
			return super.onContextItemSelected(item);
		}	
	}
	
	

}
