package vassilina.tatarlieva.canvassgesturetest;

import java.io.IOException;
import java.util.ArrayList;

import vassilina.tatarlieva.canvassgesturetest.R;

import android.app.Dialog;
import android.text.InputFilter;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.OnEditorActionListener;

public class SaveDialog extends Dialog implements OnEditorActionListener, android.view.View.OnClickListener {
	private Dialog dialog;
	private EditText input;
	private String fileName;
	private DataProvider dataProvider;
	private Grid grid;
	private String[] fileList;
	private ArrayList<String> fileNames = new ArrayList<String>();
	private Button closeButton;
	private Button okButton;
	
	private Toast theToast;
	private boolean shouldClose = false;
	private GridActivity gridActivity;
	
	public void show(boolean shouldClose, GridActivity gridActivity) {
		this.dialog.show();
		this.shouldClose = shouldClose;
		this.gridActivity = gridActivity;
		this.fileList = this.gridActivity.getApplicationContext().fileList();
		
		for(String s: fileList){
			this.fileNames.add(s);
		}
		
	}

	public SaveDialog(DataProvider dataProvider, Grid grid) {
		super(grid.getContext());
		
		this.dataProvider = dataProvider;
		this.dialog = new Dialog(grid.getContext());
		this.dialog.setContentView(R.layout.save_dialog);
		this.dialog.setTitle(R.string.inputFileName);
		this.input = (EditText)this.dialog.findViewById(R.id.fileInputName);
		this.input.append(this.dataProvider.getFileName());
		this.input.setOnEditorActionListener(this);
		InputFilter[] inf = new InputFilter[1]; 
		inf[0] = new InputFilter.LengthFilter(Grid.FILENAME_MAX_LENGTH);
		this.input.setFilters(inf); 
	
		this.closeButton = (Button)this.dialog.findViewById(R.id.closeDialog);
		this.okButton = (Button)this.dialog.findViewById(R.id.okDialog);
		this.okButton.setOnClickListener(this);
		this.closeButton.setOnClickListener(this);
		this.grid = grid;
	
		
		this.theToast = Toast.makeText(this.getContext(), R.string.savedAlert, Toast.LENGTH_SHORT);
		this.theToast.setGravity(Gravity.CENTER, 0, 0);
	}

	@Override
	public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg) {

		this.fileName = this.input.getText().toString();
		
		if( this.fileName.length() <= Grid.FILENAME_MAX_LENGTH &&
				!this.fileNames.contains(this.fileName)
				&& this.fileName.length() >= Grid.FILENAME_MIN_LENGTH && arg.getKeyCode()==KeyEvent.KEYCODE_ENTER){
			try {
				this.grid.setIsFileNew(false);
				this.dataProvider.saveFile(fileName);
				this.dialog.dismiss();
				this.theToast.setText(R.string.savedAlert);
				this.theToast.show();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else{
			if(this.fileName.length() > Grid.FILENAME_MAX_LENGTH 
					|| this.fileName.length() < Grid.FILENAME_MIN_LENGTH){
				
				this.theToast.setText(R.string.filenameTooLong);
				this.theToast.show();
			}else{
				this.theToast.setText(R.string.filenameAlreadyInUse);
				this.theToast.show();
			}
		}
		return true;
	}

	@Override
	public void onClick(View arg) {
		this.fileName = input.getText().toString();
		switch(arg.getId()){
		case R.id.okDialog:
			if(this.fileName.length() <= Grid.FILENAME_MAX_LENGTH
			&& this.fileName.length() >= Grid.FILENAME_MIN_LENGTH &&
			!this.fileNames.contains(this.fileName)){
				try {
					dataProvider.saveFile(fileName);
					this.grid.setIsFileNew(false);
					this.dialog.dismiss();
					this.theToast.setText(R.string.savedAlert);
					this.theToast.show();
					if(this.shouldClose){
						this.gridActivity.finish();
					}
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			}else{
				if(this.fileName.length() > Grid.FILENAME_MAX_LENGTH 
						|| this.fileName.length() < Grid.FILENAME_MIN_LENGTH){
					
					this.theToast.setText(R.string.filenameTooLong);
					this.theToast.show();
				}else{
					this.theToast.setText(R.string.filenameAlreadyInUse);
					this.theToast.show();
				}
			}
			break;
		case R.id.closeDialog:
			this.dialog.dismiss();
			break;
		default: break;
		}
		
	}


}
