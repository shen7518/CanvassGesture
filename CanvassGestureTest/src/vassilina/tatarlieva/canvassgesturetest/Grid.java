package vassilina.tatarlieva.canvassgesturetest;


import java.io.FileNotFoundException;
import java.io.IOException;

import vassilina.tatarlieva.evaluations.CellContent;
import vassilina.tatarlieva.welcomeactivity.Rotation3D;

import vassilina.tatarlieva.canvassgesturetest.R;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.text.ClipboardManager;
import android.view.ContextMenu;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.OnEditorActionListener;

public class Grid extends View implements
		GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener,
		ContextMenu.ContextMenuInfo, Animation.AnimationListener{
	
	//dimensions of the cells
	public static int  FILENAME_MIN_LENGTH = 5;
	public static int  FILENAME_MAX_LENGTH = 30;
	public static int  TEXT_FONT = 24;
	public static float HEADER_WIDTH = 150;
	public static float HEADER_NUMBER_WIDTH = 80;
	public static float HEADER_HEIGHT = 50;
	public static float HEADER_LETTER_HEIGHT = 50;
	public static int ROW_COUNT = 100;
	public static int COL_COUNT = 100;
	public static int CELL_WIDTH = 150;
	public static int CELL_HEIGHT = 50;
	private static float tableHeight = ROW_COUNT * CELL_HEIGHT + HEADER_LETTER_HEIGHT;
	private static float tableWidth = COL_COUNT * CELL_WIDTH + HEADER_NUMBER_WIDTH;

	//the clip rectangles
	Rect clipRect;
	Rect letterClip;
	Rect numbersClip;
	Rect tableClip;
	
	//important points
	private GridPoint onDoubleTapPoint = null;
	public void setOnDoubleTapPointToNull() {
		this.onDoubleTapPoint = null;
		
	}
	
	
	//alert dialogue
	private SaveDialog saveDialog;
	
	//the DataInputView box regarding the cell data input
	private DataInputView dataInputView;
	
	//offsets
	private float offsetX = 0;
	private float offsetY = 0;
	private float xDist = 0;
	private float yDist = 0;
	
	//paints
	private Paint whitePaint = new Paint();
	private Paint blackPaint = new Paint();
	private Paint blackBorderPaint = new Paint();
	
	//other assets
	private GestureDetector gestures;
	private DataProvider dataProvider;
	private SelectionManager selectionManager;
	
	
	//flags
	private boolean appendSelection= false;
	private boolean somethingWasChosen = false;
	private boolean isFileNew;
	
	public void setIsFileNew(boolean flag){
		this.isFileNew = flag;
	}
	
	private Toast theToast;
	
	//rectangular selection point
	private GridPoint tempRectPoint = null;
	
	public Grid(Context context, String fileName,DataInputView dataInputView,boolean isFileNew) throws FileNotFoundException {
		super(context);
		this.dataInputView = dataInputView;
		setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT));
		this.whitePaint.setARGB(255, 255, 255, 255);
		this.whitePaint.setStyle(Paint.Style.FILL);
		
		this.blackPaint.setARGB(255, 0, 0, 0);
		this.blackPaint.setTextSize(Grid.TEXT_FONT);
		this.blackPaint.setAntiAlias(true);
		
		this.blackBorderPaint.setARGB(255, 0, 0, 0);
		this.blackBorderPaint.setStyle(Paint.Style.STROKE);
		
		this.gestures = new GestureDetector(context, this);
		this.dataProvider = new DataProvider(this, fileName);
		this.dataProvider.load();
		this.selectionManager = new SelectionManager();
		
		this.isFileNew = isFileNew;
		
		this.theToast = Toast.makeText(this.getContext(), R.string.savedAlert, Toast.LENGTH_SHORT);
		this.theToast.setGravity(Gravity.CENTER, 0, 0);
	}
	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		//clip rectangles calculated
		clipRect = canvas.getClipBounds();
		letterClip = new Rect((int) HEADER_NUMBER_WIDTH, 0, clipRect.width(),
				(int) HEADER_LETTER_HEIGHT);
		numbersClip = new Rect(0, (int) HEADER_LETTER_HEIGHT, (int) HEADER_NUMBER_WIDTH,
				clipRect.height());
		tableClip = new Rect((int) HEADER_NUMBER_WIDTH , (int) HEADER_LETTER_HEIGHT ,
				clipRect.width(), clipRect.height());
		//offset calculation
		PointF offset = new PointF();
		offset.set(this.xDist + this.offsetX, this.yDist + this.offsetY);
		PointF cellOffset = new PointF();
		cellOffset.set(offset.x + HEADER_NUMBER_WIDTH, offset.y + HEADER_LETTER_HEIGHT);
	
		// calculating the offset so that not everything is drawn on the canvas!
				int maxCol = (int) Math.ceil((-offset.x + clipRect.width())
						/ CELL_WIDTH);
				int maxRow = (int) Math.ceil((-offset.y + clipRect.height())
						/ CELL_HEIGHT);

				int colSkip = (int) (-offset.x / CELL_WIDTH);
				int rowSkip = (int) (-offset.y / CELL_HEIGHT);
		
		//clipping the rectangle and drawing the letters
		canvas.clipRect(letterClip, Region.Op.REPLACE);
		for (int i = colSkip; i < maxCol; i++) {
			canvas.drawRect(i * HEADER_WIDTH + cellOffset.x, 0, (i + 1)
					* HEADER_WIDTH + cellOffset.x, HEADER_LETTER_HEIGHT,
					this.whitePaint);
			canvas.drawRect(i * HEADER_WIDTH + cellOffset.x, 0, (i + 1)
					* HEADER_WIDTH + cellOffset.x, HEADER_LETTER_HEIGHT,
					this.blackBorderPaint);
			String temp = Utils.getColumnHeaderText(i);
			Rect rect = new Rect();
			this.blackPaint.getTextBounds(temp, 0, temp.length(), rect);
			canvas.drawText(temp, i * HEADER_WIDTH + HEADER_WIDTH / 2
					+ cellOffset.x - rect.width() / 2,
					HEADER_LETTER_HEIGHT / 2 + rect.height() / 2, this.blackPaint);
		}

		// drawing the numbers
		canvas.clipRect(numbersClip, Region.Op.REPLACE);
		for (int i = rowSkip; i < maxRow; i++) {
			canvas.drawRect(0, (i + 1) * HEADER_HEIGHT + offset.y,
					HEADER_NUMBER_WIDTH, (i + 2) * HEADER_HEIGHT + offset.y,
					this.whitePaint);
			canvas.drawRect(0, (i + 1) * HEADER_HEIGHT + offset.y,
					HEADER_NUMBER_WIDTH, (i + 2) * HEADER_HEIGHT + offset.y,
					this.blackBorderPaint);
			Rect rect = new Rect();
			this.blackPaint.getTextBounds(Integer.toString(i + 1), 0, Integer
					.toString(i + 1).length(), rect);
			canvas.drawText(Integer.toString(i + 1),
					HEADER_NUMBER_WIDTH / 2 - rect.width() / 2, (i + 1)
							* HEADER_HEIGHT + HEADER_HEIGHT / 2 + offset.y
							+ rect.height() / 2, this.blackPaint);
		}

		
	
		//clipping the drawing rectangle
		canvas.clipRect(tableClip, Region.Op.REPLACE);
		
		//drawing the cell designating the start of the rectangular selection
		if(this.tempRectPoint != null && this.somethingWasChosen){
			RectF rect = new RectF(
					this.tempRectPoint.column() * CELL_WIDTH + cellOffset.x,//left
					this.tempRectPoint.row() * CELL_HEIGHT	+ cellOffset.y,//top
					(this.tempRectPoint.column()+1) * CELL_WIDTH + cellOffset.x,//right
					(this.tempRectPoint.row()+1) * CELL_HEIGHT	+ cellOffset.y//bottom
					);
			canvas.drawRect( rect , SelectionManager.rectPaint());	
		}
		
		
		GridPoint currentCell = new GridPoint();
		RectF drawRect = new RectF();
		Rect textRect = new Rect();
		String cellData = null;
		
		//drawing the data in the cells and the selections
		for (int col = colSkip; col < maxCol; col++) {
			for (int row = rowSkip; row < maxRow; row++) {
				
				currentCell.setLocation(row, col);
				cellData = this.dataProvider.getCellContent(currentCell).getDisplayContents();
					
				
				if(selectionManager.isSelected(currentCell)){
					drawRect = new RectF(
							col * CELL_WIDTH + cellOffset.x,//left
							row * CELL_HEIGHT	+ cellOffset.y,//top
							(col+1) * CELL_WIDTH + cellOffset.x,//right
							(row+1) * CELL_HEIGHT	+ cellOffset.y//bottom
							);
					canvas.drawRect( drawRect , SelectionManager.selectionPaint());
				}
				
				
				
				if(cellData != null){
					DataProvider.dataPaint().getTextBounds(cellData, 0, cellData.length(), textRect);
					canvas.drawText(cellData,
							col*CELL_WIDTH + CELL_WIDTH/2 - textRect.width()/2 + cellOffset.x	,
							row* CELL_HEIGHT + CELL_HEIGHT/2 +  textRect.height()/2 + cellOffset.y,
							DataProvider.dataPaint() );
				}
				
			}
		}
		// drawing the cells of the table
		for (int col = colSkip; col < maxCol; col++) {
			for (int row = rowSkip; row < maxRow; row++) {
				// horizontal lines
				canvas.drawLine(
						col * CELL_WIDTH + cellOffset.x,
						row * CELL_HEIGHT	+ cellOffset.y,
						(col + 1) * CELL_WIDTH + cellOffset.x,
						row * CELL_HEIGHT + cellOffset.y,
						this.whitePaint);
				// vertical lines
				canvas.drawLine(
						(col + 1) * CELL_WIDTH + cellOffset.x,
						row * CELL_HEIGHT + cellOffset.y,
						(col + 1) * CELL_WIDTH + cellOffset.x,
						(row+1) * CELL_HEIGHT + cellOffset.y,
						this.whitePaint);	
			}
		}
		//drawing the double border for the selection
		if(this.onDoubleTapPoint != null && this.onDoubleTapPoint.isCell()){
			RectF rect = new RectF(
					this.onDoubleTapPoint.column() * CELL_WIDTH + cellOffset.x,//left
					this.onDoubleTapPoint.row() * CELL_HEIGHT	+ cellOffset.y,//top
					(this.onDoubleTapPoint.column()+1) * CELL_WIDTH + cellOffset.x,//right
					(this.onDoubleTapPoint.row()+1) * CELL_HEIGHT	+ cellOffset.y//bottom
					);
			Paint paint = SelectionManager.rectPaint();
			paint.setStyle(Paint.Style.STROKE);
			paint.setStrokeWidth(4);
			canvas.drawRect( rect , paint);	
		}
	}
	
	//functions for the menu!!!!
	public void saveFile(final GridActivity gridActivity) throws IOException{
		if(this.isFileNew){
			this.saveDialog = new SaveDialog(dataProvider, this);
			this.saveDialog.show(false, gridActivity);
		}else{
			this.dataProvider.saveFile(null);
			this.applyRotation(0, 90);
		}
		
		
	}
	public void reloadFile() throws IOException{
		AlertDialog.Builder alert = new AlertDialog.Builder(this.getContext());
		alert.setTitle("Do you really want to reload?");
		alert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				try {
					dataProvider.reloadFile();
					invalidate();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
		});
		alert.setNegativeButton(R.string.cancel, null);
		alert.create().show();
	}
	public void closeFile(final GridActivity gridActivity){
		this.saveDialog = new SaveDialog(dataProvider, this);
		final AlertDialog.Builder alert = new AlertDialog.Builder(this.getContext());
		alert.setMessage(R.string.quitAlert);
		alert.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				try {
					if(isFileNew){
						saveDialog.show(true, gridActivity);
					}else{
						//isFileNew = false;
						dataProvider.closeFile();
						gridActivity.finish();
					}
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			}});
		alert.setNegativeButton("No", null);
		alert.show();
	}
	
	public void deleteContents(){
		//visitor pattern for the deleting of the cell contents!!!
		DeleteVisitor v = new DeleteVisitor( this.dataProvider );
		this.selectionManager.visit( v );
		GridPoint p = this.selectionManager.getSelection();
		this.dataProvider.deleteCellValue(p);
		this.dataProvider.recalculate();
	}
	
	public void massSelectionMode(){
		this.selectionManager.clearSelection();
		this.invalidate();
		this.appendSelection= true;
	}
	
	public void scrollMode(){
		this.selectionManager.clearSelection();
		this.appendSelection = false;
		this.invalidate();
	}
	
	public void clearSelection(){
		this.tempRectPoint = null;
		this.selectionManager.clearSelection();
		this.invalidate();
	}
	
	//CONTEXT MENU methods
	
	public boolean isRectangularSelectionStarted(){
		return this.selectionManager.isRectangularSelectionStarted();
	}
	public void startRectangularSelection(){
		this.somethingWasChosen = true;
		this.selectionManager.startRectangularSelection(this.tempRectPoint, this.appendSelection);
		this.invalidate();
		
	}
	
	public void endRectangularSelection(){
		this.selectionManager.endRectangularSelection(this.tempRectPoint , this.appendSelection);
		this.tempRectPoint = null;
		this.invalidate();
		this.somethingWasChosen = false;
	}
	
	public void cancelRectangularSelection(){
		this.tempRectPoint = null;
		this.selectionManager.cancelRectangularSelection(this.appendSelection);
		this.invalidate();
	}
	
	
	
	//CUT, COPY, PASTE
	public boolean hasCellText() {
		return this.dataProvider.getCellValue(this.tempRectPoint) != null;
	}

	public void copy(ClipboardManager clipboardManager) {
		String data = this.dataProvider.getCellValue(this.tempRectPoint);
		if(data != null){
			clipboardManager.setText(data);
		}
		this.tempRectPoint = null;
	}
	public void cut(ClipboardManager clipboardManager) {
		String data ;
		if(this.tempRectPoint!= null ){
			data = this.dataProvider.getCellValue(this.tempRectPoint);
			clipboardManager.setText(data);
			this.dataProvider.deleteCellValue(this.tempRectPoint);
			this.tempRectPoint = null;
			this.dataProvider.recalculate();
		}
	}
	public void paste(ClipboardManager clipboardManager) {
		if(this.tempRectPoint!= null){
			this.dataProvider.setCellValue(this.tempRectPoint, new CellContent(clipboardManager.getText().toString()));
		}
		this.tempRectPoint = null;
		this.invalidate();
	}
	
	//the hit point
	private GridPoint hitTest(MotionEvent arg){
		int colSelect = (int) (Math.floor((-this.offsetX - HEADER_NUMBER_WIDTH + arg.getX()) / CELL_WIDTH));
		int rowSelect = (int) (Math.floor((-this.offsetY - HEADER_LETTER_HEIGHT + arg.getY()) / CELL_HEIGHT));
		if(arg.getX()< HEADER_NUMBER_WIDTH && arg.getY() <HEADER_LETTER_HEIGHT){
			return new GridPoint(-1,-1);
		}
		if(arg.getX()< HEADER_NUMBER_WIDTH){
			return new GridPoint(rowSelect, -1);
		}
		if(arg.getY() <HEADER_LETTER_HEIGHT){
			return new GridPoint(-1, colSelect);
		}
		return new GridPoint(rowSelect,colSelect);
	}
	
	//event handling
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(event.getAction() == MotionEvent.ACTION_UP){
			this.offsetX += this.xDist;
			this.offsetY += this.yDist;
			this.xDist = 0;
			this.yDist = 0;
			this.offsetX = Math.max(this.offsetX, this.getWidth() - tableWidth);
			this.offsetX = Math.min(this.offsetX, 0);
			this.offsetY = Math.max(this.offsetY, this.getHeight() - tableHeight);
			this.offsetY = Math.min(this.offsetY, 0);	
		}
		return gestures.onTouchEvent(event);
	}
	
	
	

	@Override
	public boolean onDoubleTap(MotionEvent arg) {
		this.onDoubleTapPoint = this.hitTest(arg);
		if(this.onDoubleTapPoint.isCell()){
			this.dataInputView.getDataInputView().findViewById(R.id.okDialog).
						setOnClickListener(new ClickManager(onDoubleTapPoint, this.dataInputView, this, true, this.dataProvider));
			this.dataInputView.getDataInputView().findViewById(R.id.delete).
								setOnClickListener(new ClickManager(onDoubleTapPoint, this.dataInputView, this, false, this.dataProvider));	
			
			if( onDoubleTapPoint.isCell() && dataProvider.hasCellValue(onDoubleTapPoint)){
				String data = dataProvider.getCellValue(onDoubleTapPoint);
				this.dataInputView.getDataInputField().setText(data);
				this.dataInputView.getDataInputField().setSelection(this.dataInputView.getDataInputField().getText().length());

			}else{
				this.dataInputView.getDataInputField().setText(null);
			}
			
			this.dataInputView.getDataInputField().setOnEditorActionListener(new OnEditorActionListener() {
				@Override
				public boolean onEditorAction(TextView view, int arg1, KeyEvent arg) {
					if(arg.getKeyCode() == KeyEvent.KEYCODE_ENTER){
						String inputText = dataInputView.getDataInputField().getText().toString();
						dataProvider.setCellValue(onDoubleTapPoint, new CellContent(inputText));
						dataInputView.getDataInputView().setVisibility(View.GONE);
						onDoubleTapPoint = null;
					}
					return true;
				}
			});
			
			
			this.dataInputView.getDataInputView().setVisibility(View.VISIBLE);
			this.invalidate();
			
			
		
		}
		return true;
	}

	@Override
	public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float x, float y) {
		this.xDist = arg1.getX() - arg0.getX();
		this.yDist = arg1.getY() - arg0.getY();
		
		if (offsetX + xDist > 0 && xDist > 0) {
			this.xDist = -this.offsetX;
		}
		if (offsetY + yDist > 0 && yDist > 0) {
			this.yDist = -this.offsetY;
		}
		if (offsetX + xDist < this.getWidth() - tableWidth && xDist < 0) {
			this.xDist = this.getWidth() - tableWidth - offsetX;
		}
		if (offsetY + yDist < this.getHeight() - tableHeight && yDist < 0) {
			this.yDist = this.getHeight() - tableHeight - offsetY;
		}
		this.invalidate();
		
		return true;
	}

	@Override
	public boolean onSingleTapUp(MotionEvent arg) {
		GridPoint point = this.hitTest(arg);
		if(point.isRow()){
			this.selectionManager.selectRow(point.row(), this.appendSelection);
		}else if ( point.isColumn() ){
			this.selectionManager.selectCol(point.column(), this.appendSelection);
		}
		else if( point.isCorner())
		{
			this.selectionManager.selectAll();
		}else{
			this.selectionManager.selectCell(point, this.appendSelection);
		}
		
		this.invalidate();
		return true;
	}	
	
	
	@Override
	public void onShowPress(MotionEvent arg) {}
	@Override
	public boolean onDoubleTapEvent(MotionEvent arg) {return false;}
	@Override
	public boolean onSingleTapConfirmed(MotionEvent arg) {
		GridPoint point = this.hitTest(arg);
		StringBuilder sb = new StringBuilder();
		int start = this.dataInputView.getDataInputField().getSelectionStart();
		int end = this.dataInputView.getDataInputField().getSelectionEnd();
		String text = this.dataInputView.getDataInputField().getText().toString();
		if(start < end){
			if(start > 0){
				sb.append(text.substring(0, start));
			}
			String cell = Utils.getColumnHeaderText(point.column())+ Integer.toString(point.row()+1) ;
			sb.append(cell);
			if(end < text.length()){
				sb.append(text.substring(end, text.length()));
			}
			this.dataInputView.getDataInputField().setText(null);
			this.dataInputView.getDataInputField().setText(sb.toString());
			this.dataInputView.getDataInputField().setSelection(start + cell.length());
		}else{
			if(end > 0){
				sb.append(text.substring(0, end));
			}
			String cell = Utils.getColumnHeaderText(point.column())+ Integer.toString(point.row()+1) ;
			sb.append(cell);
			if(start < text.length()){
				sb.append(text.substring(start, text.length()));
			}
			this.dataInputView.getDataInputField().setText(null);
			this.dataInputView.getDataInputField().setText(sb.toString());
			this.dataInputView.getDataInputField().setSelection(end + cell.length());
		}
		return true;}
	@Override
	public boolean onDown(MotionEvent arg) {return true;}
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float x, float y) {return false;}
	@Override
	public void onLongPress(MotionEvent arg) {
		//looks suspicious....no anymore ;)
		this.tempRectPoint = this.hitTest(arg);
		if(this.tempRectPoint.isCell()){
			this.getContextMenuInfo();
			this.showContextMenu();
		}
	}
	
	@Override
	public ContextMenu.ContextMenuInfo getContextMenuInfo(){
		return super.getContextMenuInfo();
	}
	
	
	//rotation for the grid after saving the file
	@Override
	public void onAnimationEnd(Animation animation) {
		this.setVisibility(View.INVISIBLE);
		Rotation3D rotation = new Rotation3D(
					-90, 0, this.getWidth() / 2.0f, this.getHeight() / 2.0f, 310.0f, false, this);
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
	public void onAnimationStart(Animation arg0) {}
	
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
	
	
	
}

