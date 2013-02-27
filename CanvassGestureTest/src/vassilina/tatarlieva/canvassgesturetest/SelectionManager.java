package vassilina.tatarlieva.canvassgesturetest;

import java.util.ArrayList;
import java.util.HashSet;
import android.graphics.Paint;


public class SelectionManager {
	private static Paint selectionPaint = new Paint();
	private static Paint rectPaint = new Paint();
	private int currentRow = -1;
	private int currentCol = -1;
	private HashSet<Integer> selectedRows = new HashSet<Integer>();
	private HashSet<Integer> selectedCols = new HashSet<Integer>();
	private HashSet<GridPoint> hm = new HashSet<GridPoint>();
	
	private boolean rectangularSelectionMode = false;
	private ArrayList<RectangularSelection> rectSel = new ArrayList<RectangularSelection>();
	private GridPoint startRectPoint = null;
	
	
	static {
		selectionPaint.setARGB(255, 0, 0, 75);
		selectionPaint.setStyle(Paint.Style.FILL);
		rectPaint.setARGB(255, 255, 170, 0);
		rectPaint.setStyle(Paint.Style.FILL);
	}
	public static Paint selectionPaint(){
		return selectionPaint;
	}
	public static Paint rectPaint(){
		return rectPaint;
	}
	public void selectRow(int row, boolean appendSelection){
		if(!appendSelection){
			this.clearSelection();
		}
		this.selectedRows.add(row);
		
	}
	
	public void selectCol(int col, boolean appendSelection){
		if(!appendSelection){
			this.clearSelection();
		}
		this.selectedCols.add(col);
	}	
	
	public void selectCell(GridPoint p, boolean append){
		if(append){
			this.hm.add(p);
		}else{
			this.clearSelection();
			this.currentCol = p.column();
			this.currentRow = p.row();
		}
	}
	
	//single is selected
	public boolean isSelected(GridPoint point){
		if (this.selectedRows.contains(point.row()) || this.selectedCols.contains(point.column())) {
			return true;
		}
		if( point.row() == this.currentRow && point.column() == this.currentCol ){
			return true;
		}
		
		for(RectangularSelection rs : this.rectSel){
			if(rs.containsPoint(point)){
				return true;
			}
		}
		return this.hm.contains(point);
	}

	
	public void clearSelection(){
		this.currentCol = -1;
		this.currentRow = -1;
		this.selectedRows.clear();
		this.selectedCols.clear();
		this.hm.clear();
		this.rectSel.clear();
	}
	
	public void selectAll(){
		for(int col= 0; col< Grid.COL_COUNT; col++){
			this.selectedCols.add(col);
		}
	}
	
	//WTF?!!!!!!11
	public void selectArea(int row1, int col1, int row2, int col2){
		
	}
	
	public GridPoint getSelection(){
		return new GridPoint(this.currentRow, this.currentCol);
	}
	
	public void visit( ISelectionVisitor v ){
		v.ifSelected(currentRow, currentCol);
		for(int row: this.selectedRows){
			for(int col= 0; col< Grid.COL_COUNT; col++){
				v.ifSelected(row, col);
			}
		}
		
		for(int col: this.selectedCols){
			for(int row= 0; row< Grid.ROW_COUNT; row++){
				v.ifSelected(row, col);
			}
		}
		
		for(GridPoint p: hm){
			v.ifSelected(p.row(), p.column());
		}
		
		for(RectangularSelection rs : rectSel){
			for(int col = rs.colStart; col<= rs.colEnd; col++){
				for(int row = rs.rowStart; row<= rs.rowEnd; row++){
					v.ifSelected(row, col);
				}
			}
			
		}
	}
	
	//rectangular selection!!!
	public boolean isRectangularSelectionStarted(){
		return this.rectangularSelectionMode;
	}
	public void startRectangularSelection( GridPoint p, boolean appendSelection){
		if(!appendSelection){
			this.clearSelection();
		}
		this.startRectPoint = p;
		this.rectangularSelectionMode = true;
		
	}
	
	public void endRectangularSelection(GridPoint p, boolean appendSelection){
		if(!appendSelection){
			this.clearSelection();
		}
		this.rectangularSelectionMode = false;
		rectSel.add(new RectangularSelection(this.startRectPoint, p));
	}
	
	public void cancelRectangularSelection(boolean appendSelection){
		if(!appendSelection){
			this.clearSelection();
		}
		this.startRectPoint = null;
		this.rectangularSelectionMode = false;
		this.rectSel.clear();
	}
	
	
	private class RectangularSelection{
		private int colStart;
		private int rowStart;
		private int colEnd;
		private int rowEnd;
		public RectangularSelection(GridPoint p0, GridPoint p1){
			this.colStart = Math.min(p0.column(), p1.column());
			this.colEnd = Math.max(p0.column(), p1.column());
			this.rowStart = Math.min(p0.row(), p1.row());
			this.rowEnd = Math.max(p0.row(), p1.row());
		}
		
		public boolean containsPoint(GridPoint p){
			if(p.column()>= this.colStart
					&& p.column()<= this.colEnd
					&& p.row()<= this.rowEnd
					&& p.row()>= this.rowStart){
				return true;
			}else{
				return false;
			}
		
		}
	}
	
		
}


