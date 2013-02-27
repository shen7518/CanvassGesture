package vassilina.tatarlieva.welcomeactivity;

import java.util.ArrayList;
import java.util.Collections;

import vassilina.tatarlieva.canvassgesturetest.GridActivity;
import vassilina.tatarlieva.canvassgesturetest.R;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;


public class FileListLayout extends ListView implements android.widget.AdapterView.OnItemClickListener,
android.widget.AdapterView.OnItemLongClickListener{
	
	private String currentFileName;
	
	public FileListLayout(Context context) {
		super(context);
		this.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		this.setOnItemClickListener(this);
		this.setOnItemLongClickListener(this);
	}
	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
		String text = ((TextView) view.findViewById(R.id.fileName)).getText().toString();
		Intent i = new Intent(this.getContext(), GridActivity.class);
    	Bundle bundle = new Bundle();
    	bundle.putString(GridActivity.FILE_NAME_PARAM, text);
    	bundle.putBoolean(GridActivity.FROM_WHERE_PARAM, true);
    	i.putExtras(bundle);
    	this.getContext().startActivity(i);
	}
	
	
	
	public void bindData(String[] files){

		ArrayList<String> fileList = new ArrayList<String>();
		for(String file : files){
			fileList.add(file);
		}
		Collections.sort(fileList);
		this.setAdapter(new FileListAdapter(this.getContext(), R.layout.list_layout, fileList));
	}
	
	
	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View view, int position, long id) {
		this.currentFileName = ((TextView)view.findViewById(R.id.fileName)).getText().toString();
		this.showContextMenu();
		return true;
	}
	
	@Override
	protected void onCreateContextMenu(ContextMenu menu) {
		super.onCreateContextMenu(menu);
	  menu.add(Menu.NONE,WelcomeScreenActivity.MENU_ITEM_DELETE_FILE, Menu.NONE,
			  this.getContext().getString(R.string.deleteFile) + " \"" + this.currentFileName + "\"?");
	}
	
	public String getCurrentFilename() {
		return this.currentFileName;
	}
}
