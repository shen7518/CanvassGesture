package vassilina.tatarlieva.canvassgesturetest;


import vassilina.tatarlieva.canvassgesturetest.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.LinearLayout;

public class DataInputView{
	private LinearLayout dataInputView;
	private Grid grid;
	private GridActivity gridActivity;
	private EditText dataInputField;
	
	public DataInputView(Grid grid, GridActivity gridActivity) {
		this.grid = grid;
		this.gridActivity = gridActivity;
		LayoutInflater layoutInflater = (LayoutInflater)gridActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.dataInputView =(LinearLayout) layoutInflater.inflate(R.layout.cell_data_input_feature, null);
		this.dataInputField = (EditText)this.dataInputView.findViewById(R.id.dataInputField);
	}
	
	public LinearLayout getDataInputView(){
		return this.dataInputView;
	}
	
	public EditText getDataInputField(){
		return this.dataInputField;
	}
	
}
