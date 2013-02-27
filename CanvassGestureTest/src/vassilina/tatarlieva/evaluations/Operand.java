package vassilina.tatarlieva.evaluations;

import java.util.HashMap;



public class Operand  {
	
	public static final String SPACE = " ";
	public static final String EQUAL = "=";
	public static final String MULTIPLY = "*";
	public static final String DIVIDE = "/";
	public static final String PLUS = "+";
	public static final String MINUS = "-";        
	public static final String RIGHT_PARENTHESIS = ")";
	public static final String LEFT_PARENTHESIS = "("; 
	
	private OperandType operandType;
	private double numberValue;
	private int parametersCount = 0;
	private String stringValue = SPACE;
	
	public int getParametersCount(){
		return this.parametersCount;
	}
	public static Operand empty = new Operand(OperandType.EMPTY , 0);
	public static Operand error = new Operand(OperandType.ERROR , 0);
	private static  HashMap<OperandType, String> typeNameOperandMap;
    private static  HashMap<String, OperandType> nameTypeOperandMap;
	
    static{
    	typeNameOperandMap  = createNamesMap();
    	nameTypeOperandMap  = createTypeMap( typeNameOperandMap.size() );
    }
    
    
    public enum OperandType {
    						TEXT , 
    						ERROR,
    						ERROR_MATCHING,
    						EMPTY , 
    						NUMBER ,
    						LITERAL,
    						MULTIPLY, 
    						DIVIDE , 
    						PLUS ,
    						MINUS, 
    						LEFT_PARENTHESES, 
    						RIGHT_PARENTHESES,
    						END_OF_LINE;
    						};
	
	
	public Operand(OperandType operandType,  double numberValue) {
		this.operandType = operandType;
		 if ( this.operandType != OperandType.EMPTY )
         {
             this.numberValue = numberValue;
         }
         else 
         {
             this.numberValue = 0;
             this.stringValue = SPACE;
         }
	}
	
	public Operand(){
		this.stringValue = SPACE;
		this.numberValue = 0;
	}
	
	public Operand(double numberValue) {
		this.operandType = OperandType.NUMBER;
		this.numberValue = numberValue;
	}
	
	public Operand(String text, boolean isLiteral){
		if(isLiteral){
			this.operandType = OperandType.LITERAL;
			this.stringValue = text;
		}else{
			this.operandType = OperandType.TEXT;
			this.stringValue = text;
		}
	}
	
	public Operand(OperandType operandType, int parametersCount) {
		this.operandType = operandType;
		this.numberValue = 0;
		this.parametersCount = parametersCount;
	}
	
	public double getNumber() {
		return this.numberValue;
	}
	public OperandType getOperandType() {
		return this.operandType;
	}
	
	public String getString(){
		return this.stringValue;
	}
	
	
	public static OperandType getOperandType( String operandString )
    {
        if ( nameTypeOperandMap.containsKey( operandString ) )
        {
            return nameTypeOperandMap.get(operandString);
        }

        return OperandType.EMPTY;
    }

	   private static HashMap<OperandType, String> createNamesMap()
       {
		   HashMap<OperandType, String> typeNameOperandMap = new HashMap<OperandType, String>();

           typeNameOperandMap.put( OperandType.EMPTY,"");            
           typeNameOperandMap.put( OperandType.MULTIPLY,MULTIPLY );
           typeNameOperandMap.put( OperandType.DIVIDE,DIVIDE );            
           typeNameOperandMap.put( OperandType.PLUS, PLUS );
           typeNameOperandMap.put( OperandType.MINUS,MINUS );
           typeNameOperandMap.put( OperandType.LEFT_PARENTHESES,LEFT_PARENTHESIS );
           typeNameOperandMap.put( OperandType.RIGHT_PARENTHESES,RIGHT_PARENTHESIS );
			typeNameOperandMap.put( OperandType.END_OF_LINE,"");
           return typeNameOperandMap;
       }
       private static HashMap<String, OperandType> createTypeMap( int count ){
    	   HashMap<String, OperandType> typeOperandMap = new HashMap<String, OperandType>();

           for( OperandType key : typeNameOperandMap.keySet() ){
               String value = typeNameOperandMap.get(key);
               if ( value != null && value.length() > 0 ){
                   typeOperandMap.put( value, key );
               }
           }

           return typeOperandMap;
       }
           
    @Override
    public String toString() {
    	return this.toString(true);
    }
    
    public String toString( boolean showDecimalZeros ) {
    	if(this.operandType == OperandType.NUMBER){
    		boolean isInt = Math.abs( this.numberValue - (int)this.numberValue) < 0.0000001;
    		
    		if ( showDecimalZeros || !isInt )
    		{
    			return Double.toString(this.numberValue);
    		}
    		else
    		{
    			return Integer.toString((int)this.numberValue);    			
    		}
    	}else{
    		return this.stringValue;
    	}
    }
    
    
    @Override
    public boolean equals(Object o) {
    	if(!this.getClass().equals(o.getClass())){
			return false;
		}else{
			Operand other = (Operand)o; 
			return other.operandType == this.operandType&& other.numberValue == this.numberValue && this.numberValue == 0;
		}
    }
    
    
}



