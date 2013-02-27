package vassilina.tatarlieva.canvassgesturetest;

public class DeleteVisitor implements ISelectionVisitor {
	private DataProvider dataProvider;
	public DeleteVisitor(DataProvider dataProvider){
		this.dataProvider = dataProvider;
	}
	
	
	public void ifSelected(int row, int col) {
		if((new GridPoint(row, col)).isCell()){
		this.dataProvider.deleteCellValue(new GridPoint(row, col));
		}
	}
	
	
	
	
}
