package vassilina.tatarlieva.canvassgesturetest;

import vassilina.tatarlieva.evaluations.CellContent;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class ClickManager implements  View.OnClickListener {
	private GridPoint p;
	private EditText input;
	private DataInputView dataInputView;
	private Grid grid;
	private boolean which;
	private DataProvider dataProvider;

	public ClickManager(GridPoint p, DataInputView dataInputView, Grid grid, boolean which, DataProvider dataProvider) {
		this.p = p;
		this.dataInputView = dataInputView;
		this.input = this.dataInputView.getDataInputField();
		this.grid = grid;
		this.which = which;
		this.dataProvider = dataProvider;
	}

	@Override
	public void onClick(View arg) {
		if(this.which){
			String inputText = input.getText().toString();
			this.dataProvider.setCellValue(p, new CellContent(inputText));
		}else{
			this.dataProvider.deleteCellValue(p);
			this.dataProvider.recalculate();
		}
		this.dataInputView.getDataInputView().setVisibility(View.GONE);
		this.grid.setOnDoubleTapPointToNull();
	}

}