package vassilina.tatarlieva.canvassgesturetest;

import java.io.IOException;

import vassilina.tatarlieva.canvassgesturetest.R;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.InputFilter;
import android.view.Gravity;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class SaveCloseToolbarDialogues extends AlertDialog.Builder implements OnEditorActionListener, DialogInterface.OnClickListener {
	private EditText input;
	private DataProvider dataProvider;
	private AlertDialog alertDialog;
	private AlertDialog alertDialog1;
	private Grid grid;
	private Toast savedToast;
	private String fileName;
	
	public SaveCloseToolbarDialogues(DataProvider dataProvider, Grid grid) {
		super(grid.getContext());
		this.input = new EditText(grid.getContext());
		this.dataProvider = dataProvider;
		this.grid = grid;
		this.savedToast = Toast.makeText(grid.getContext(), R.string.savedAlert, Toast.LENGTH_SHORT);
		this.savedToast.setGravity(Gravity.CENTER, 0, 0);
		
		
		this.alertDialog1 = this.createAlertDialogue();
	}

	@Override
	public boolean onEditorAction(TextView view, int arg1, KeyEvent arg) {
		
		this.fileName = view.getText().toString();
		if( this.fileName.length() <= Grid.FILENAME_MAX_LENGTH
				&& this.fileName.length() >= Grid.FILENAME_MIN_LENGTH && arg.getKeyCode()==KeyEvent.KEYCODE_ENTER){
			try {
				this.grid.setIsFileNew(false);
				this.dataProvider.saveFile(fileName);
				this.alertDialog.dismiss();
				this.savedToast.show();			
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return true;
	}
	
	

	
	public AlertDialog createAlertDialogue(){
		this.setTitle("Input File Name");
		this.input.setText(this.dataProvider.getFileName());
		this.input.setOnEditorActionListener(this);
		InputFilter[] inf = new InputFilter[1]; 
		inf[0] = new InputFilter.LengthFilter(Grid.FILENAME_MAX_LENGTH);
		this.input.setFilters(inf); 
		this.setView(input);
		this.setPositiveButton("OK", this); 
		this.setNegativeButton("Cancel", null);
		return this.create();
	}

	@Override
	public void onClick(DialogInterface di, int arg1) {
		this.fileName = input.getText().toString();
		if(this.fileName.length() <= Grid.FILENAME_MAX_LENGTH
				&& this.fileName.length() >= Grid.FILENAME_MIN_LENGTH){
			try {
				dataProvider.saveFile(fileName);
				this.savedToast.show();		
				this.grid.setIsFileNew(false);
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else{
			this.alertDialog.dismiss();
			this.alertDialog1.show();
		}
	}	
	
	public void showDialog(){
		if(this.alertDialog == null){
			this.alertDialog = this.createAlertDialogue();
		}
		this.input.setText(this.dataProvider.getFileName());
		this.alertDialog.show();
	}

}
