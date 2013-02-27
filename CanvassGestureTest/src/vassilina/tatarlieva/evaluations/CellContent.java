package vassilina.tatarlieva.evaluations;

public class CellContent {
	private String entryText;
	private String displayText;
	private double displayNumber;
	private CellDataType entryType;
	private CellDataType resultType;
	private boolean isVisited;
	
	public void setVisited(){
		this.isVisited = true;
	}
	
	public boolean isVisited(){
		return this.isVisited;
	}
	
	public static String error = "ERROR!!!";
	
	//getters for the fields
	public CellDataType getEntryType(){
		return this.entryType;
	}
	
	public CellDataType getResultType(){
		return this.resultType;
	}
	
	public void setResultType(CellDataType cdt){
		this.resultType = cdt;
	}
	
	public double getDisplayNumber(){
		return this.displayNumber;
	}
	
	public void setDisplayNumber(double num){
		this.displayNumber = num;
	}
	
	public String getDisplayText(){
		return this.displayText;
	}
	
	public void setDisplayText(String text){
		this.displayText = text;
	}
	
	public String getEntryText(){
		return this.entryText;
	}
	
	public CellContent(String text){
		if(text == null || text == ""){
			this.entryType= CellDataType.EMPTY;
			this.resultType= CellDataType.EMPTY;
			this.displayText = "";
			this.displayNumber = 0;
		}else if(text.startsWith(Operand.EQUAL)){
			this.entryText = text;
			this.entryType = CellDataType.FORMULA;
			
		}else {
			try{
				this.displayNumber = Double.parseDouble(text);
				this.entryType= CellDataType.NUMBER;
				this.resultType= CellDataType.NUMBER;
			}catch(NumberFormatException nfe){
				this.displayText = text;
				this.entryType= CellDataType.TEXT;
				this.resultType= CellDataType.TEXT;
			}
			this.entryText = text;
		}
	}
	
	
	@Override
	public String toString() {
		if (this.entryType == CellDataType.FORMULA){
			return this.entryText;
		}else if(this.entryType == CellDataType.NUMBER){
			return Double.toString(this.displayNumber);
		}else{
			return this.displayText;
		}
	}
	
	public String getDisplayContents(){
		if(this.resultType == CellDataType.NUMBER){
			return Double.toString(this.displayNumber);
		}else if(this.resultType == CellDataType.ERROR){
			return error;
		}else if(this.entryType == CellDataType.FORMULA && this.resultType == CellDataType.EMPTY){
			return Double.toString(this.displayNumber);
		}else{
			return this.displayText;
		}
		
	}

	public void unvistit() {
		this.isVisited = false;
	}
	
	
}

