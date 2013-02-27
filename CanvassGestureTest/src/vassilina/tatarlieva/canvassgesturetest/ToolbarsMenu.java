package vassilina.tatarlieva.canvassgesturetest;

import java.io.IOException;

import vassilina.tatarlieva.canvassgesturetest.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

public class ToolbarsMenu implements View.OnClickListener {
	private LinearLayout toolbarLayout;
	private Grid grid;
	private GridActivity gridActivity;
	public ToolbarsMenu(Grid grid, GridActivity gridActivity) {
		this.grid = grid;
		this.gridActivity = gridActivity;
		LayoutInflater layoutInflater = (LayoutInflater)gridActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.toolbarLayout =(LinearLayout) layoutInflater.inflate(R.layout.toolbar, null);
		this.toolbarLayout.findViewById(R.id.save).setOnClickListener(this);
		this.toolbarLayout.findViewById(R.id.close).setOnClickListener(this);
		this.toolbarLayout.findViewById(R.id.relaod).setOnClickListener(this);
	}
	
	public LinearLayout getToolbarView(){
		return this.toolbarLayout;
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
			case R.id.save:
				try {
					this.grid.saveFile(this.gridActivity);
					break;
				} catch (IOException e) {
					e.printStackTrace();
				}
			case R.id.relaod:
				try {
					this.grid.reloadFile();
					break;
					} catch (IOException e) {
					e.printStackTrace();
				}
			case R.id.close:
				this.grid.closeFile(this.gridActivity);
				break;
			default: break;
				
		}
	}

}
