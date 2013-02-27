package vassilina.tatarlieva.canvassgesturetest;

import java.util.ArrayList;
import java.util.regex.PatternSyntaxException;

public class GridPoint implements Comparable<GridPoint> {
	private int row = -1;
	private int column = -1;
	
	public GridPoint()
	{
	}
	
	public GridPoint( int row, int column )
	{
		this.row = row;
		this.column = column;
	}
	
	public GridPoint(String string){
		ArrayList<Character> row = new ArrayList<Character>();
		ArrayList<Character> column = new ArrayList<Character>();
		string = string.toUpperCase();
		for(int i = 0; i<string.length(); i++){
			if(Character.isDigit(string.charAt(i))){
				row.add(string.charAt(i));
			}else{
				column.add(string.charAt(i));
			}
		}
		int colNum = 0;
		for(int i = column.size()-1, j = 0; i>=0; i--, j++){
			colNum += (Character.getNumericValue(column.get(i))-10)*Math.pow(26,j);
		}
		this.column = colNum;
		String row1 = "";
		for(int i= row.size() - 1; i>=0; i--){
			row1 += row.get(i);
		}
		this.row = Integer.parseInt(row1)-1;
	}
	
	public int row()
	{
		return this.row;
	}
	
	public void setRow(int row)
	{
		this.row = row;
	}
	
	public int column()
	{
		return this.column;
	}

	public void setColumn(int col)
	{
		this.column = col;
	}
	
	public void setLocation( int row, int col )
	{
		this.row = row;
		this.column = col;
	}
	
	public boolean isCell()
	{
		return this.column >= 0 && this.row >= 0;
	}
	
	public boolean isCorner()
	{
		return this.column < 0 && this.row < 0 ;
	}
	

	public boolean isRow() {
		return this.column < 0 && this.row >= 0 ;
	}
	
	public boolean isColumn() {
		return this.column >= 0 && this.row < 0 ;
	}
	
	@Override
	public boolean equals(Object o) {
		if(!this.getClass().equals(o.getClass())){
			return false;
		}else{
			GridPoint other = (GridPoint)o; 
			return other.row == this.row && other.column == this.column;
		}
	}
	
	@Override
	public int hashCode() {
		int multiplier = 17;
		int code = 41;
		code = multiplier * code + this.row;
		code = multiplier * code + this.column;
		return code;
	}
	
	@Override
	public String toString() {
		return this.row + "x" + this.column;
	}
	
	public static GridPoint toGridPoint(String s){
		try{
		String[] a = s.split("x");
		return new GridPoint(Integer.parseInt(a[0]), Integer.parseInt(a[1]));
		}catch(PatternSyntaxException pse){
			System.out.println(pse.getDescription());
		}
		return new GridPoint();
	}

	
	@Override
	public int compareTo(GridPoint another) {
		if(this.row == another.row){
			return this.column - another.column;
		}else{
			return this.row - another.row;
		}
		
	}
	
}
