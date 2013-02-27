package vassilina.tatarlieva.canvassgesturetest;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import vassilina.tatarlieva.canvassgesturetest.R;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;



public class GridActivity extends Activity {
	private Grid activityView;
	private ClipboardManager clipboardManager; 
	
	public static final String FILE_NAME_PARAM = "fileName";
	public static final String FROM_WHERE_PARAM = "fromFiles";
	
	private static final int MENU_ITEM_ID_START_SELECTION = 1;
	private static final int MENU_ITEM_ID_END_SELECTION = 2;
	private static final int MENU_ITEM_ID_CANCEL_SELECTION = 3;
	private static final int MENU_ITEM_ID_COPY = 4;
	private static final int MENU_ITEM_ID_CUT = 5;
	private static final int MENU_ITEM_ID_PASTE = 6;
	

	private DataInputView dataInputView;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//getting the file list to create everything
		String[] files =this.getApplicationContext().fileList();
		Bundle extras = getIntent().getExtras();
		String fileName = extras.getString(FILE_NAME_PARAM);
		boolean fromFiles = extras.getBoolean(FROM_WHERE_PARAM);
		ArrayList<String> fileNames = new ArrayList<String>();
		for(String file : files){
			fileNames.add(file);
		}
		try {
			//adding the onDouble tap EditText
			this.dataInputView = new DataInputView(this.activityView, this);
			this.dataInputView.getDataInputView().setVisibility(View.GONE);
			
			if(fromFiles){
				if( fileNames.contains(fileName) ){
				this.activityView = new Grid(this, fileName,this.dataInputView, false);
				}else{
					throw new FileNotFoundException();
				}
			}else{
				int i = 1;
				while(fileNames.contains((fileName + i).toString())){
					i++;
				}
				this.activityView = new Grid(this, (fileName + i).toString(),this.dataInputView, true);
			}
			
			
			
			
			//creating the wrapper content View
			LinearLayout ll = new LinearLayout(this);
			ll.setOrientation(LinearLayout.VERTICAL);
			ll.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
			ll.addView((new ToolbarsMenu(this.activityView, this)).getToolbarView());
			ll.addView(this.dataInputView.getDataInputView());
			ll.addView(this.activityView);
			setContentView(ll);
			registerForContextMenu(this.activityView);
			this.clipboardManager = (ClipboardManager)
					this.getSystemService(Context.CLIPBOARD_SERVICE);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	
	
	 @Override
		public boolean onCreateOptionsMenu(Menu menu) {
	    	super.onCreateOptionsMenu(menu);
	        MenuInflater inflater = this.getMenuInflater();
	        inflater.inflate(R.menu.menu, menu);
	        return true;
	    }
	 
	  //the menu for the complex selection of the items  
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case R.id.scroll:
			this.activityView.scrollMode();
			return true;
		case R.id.clear:
			this.activityView.clearSelection();
			return true;
		case R.id.delete:
			this.activityView.deleteContents();
			return true;
		case R.id.selection:
			this.activityView.massSelectionMode();
			return true;
		default:
			System.out.println(item.getItemId());
			return super.onOptionsItemSelected(item);
		}	
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
	                                ContextMenuInfo menuInfo) {
	  super.onCreateContextMenu(menu, v, menuInfo);
	  if(this.activityView.isRectangularSelectionStarted()){
		  menu.add(Menu.NONE,GridActivity.MENU_ITEM_ID_CANCEL_SELECTION, Menu.NONE,R.string.cancelRectangularSelection);
		  menu.add(Menu.NONE,GridActivity.MENU_ITEM_ID_END_SELECTION, Menu.NONE, R.string.endRectangularSelection);
	  }else{
		  menu.add(Menu.NONE,GridActivity.MENU_ITEM_ID_START_SELECTION, Menu.NONE, R.string.startRectangularSelection);
	  }
	  if(this.activityView.hasCellText()){
		  menu.add(Menu.NONE,GridActivity.MENU_ITEM_ID_CUT, Menu.NONE, R.string.cut);
		  menu.add(Menu.NONE,GridActivity.MENU_ITEM_ID_COPY, Menu.NONE, R.string.copy);
	  }
	  if(this.clipboardManager.hasText()){
		  menu.add(Menu.NONE,GridActivity.MENU_ITEM_ID_PASTE, Menu.NONE, R.string.paste);
	  }
	  
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case GridActivity.MENU_ITEM_ID_START_SELECTION: 
			this.activityView.startRectangularSelection();
			return true;
		case GridActivity.MENU_ITEM_ID_END_SELECTION:
			this.activityView.endRectangularSelection();
		    return true;
		case GridActivity.MENU_ITEM_ID_CANCEL_SELECTION:
			this.activityView.cancelRectangularSelection();
			return true;
		
		case GridActivity.MENU_ITEM_ID_COPY:
			this.activityView.copy(this.clipboardManager);
			return true;
		
		case GridActivity.MENU_ITEM_ID_CUT:
			this.activityView.cut(this.clipboardManager);
			return true;
		
		case GridActivity.MENU_ITEM_ID_PASTE:
			this.activityView.paste(this.clipboardManager);
			return true;
		
		default:
			return super.onContextItemSelected(item);
		}
	}

	public ClipboardManager getClipboardManager() {
		return this.clipboardManager;
	}
	
	
	@Override
	protected void onDestroy() {
		try {
			if(!this.isFinishing()){
			this.activityView.saveFile(this);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		super.onDestroy();
	}
	
	@Override
	public void onBackPressed() {
		this.activityView.closeFile(this);
	}
	


}

