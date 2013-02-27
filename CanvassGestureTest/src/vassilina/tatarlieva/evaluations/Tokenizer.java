package vassilina.tatarlieva.evaluations;

import java.text.DecimalFormatSymbols;

import vassilina.tatarlieva.evaluations.Operand.OperandType;


public class Tokenizer {
	private static final char START_CHAR       = '=';
    private static final char NEW_LINE_CHAR    = '\n';
    private static final char EOL_CHAR         = '\n';
    private static final char SPACE_CHAR       = ' ';
    private static final char TAB_CHAR         = '\t';
    private static final char QUOTES 		   = '\"';
    
    
    
    private String expression;
    private DecimalFormatSymbols decimalFormatSymbols;
    private char currentChar;
    private char nextChar;
    private int nextPosition;
    private double lastNumber;
    private String lastString;
	private String currentLiteral;
    
    
    
    public Tokenizer(String expression){
    	this.expression = expression.trim();
    	this.decimalFormatSymbols = new DecimalFormatSymbols();
    	this.nextPosition = -1;
    	
    	
    	this.getNextCharacter();
    	
    	if(this.nextChar == START_CHAR){
    		this.getNextCharacter();
    	}
    }
    
    public double getLastNumber(){
    	return this.lastNumber;
    }
    
    public String getLastString(){
    	return this.lastString;
    }
    
    public String getLastLiteral(){
    	return this.currentLiteral;
    }
    
    
    public boolean hasReachedEnd(){
    	return this.nextChar == EOL_CHAR;
    }

    private void getNextCharacter(){
    	this.currentChar = this.nextChar;
    	this.nextPosition++;
    	if(this.nextPosition < this.expression.length()){
    		this.nextChar = this.expression.charAt(this.nextPosition);
    	}else{
    		this.nextChar = EOL_CHAR;
    	}
    }
    
    private void removeSpaces(){
    	if(this.nextChar == EOL_CHAR || this.nextChar == NEW_LINE_CHAR){
    		if(this.nextPosition >= this.expression.length() - 1){
    			return;
    		}
    	}
    	
    	while(this.nextChar == NEW_LINE_CHAR || this.nextChar == TAB_CHAR || this.nextChar == SPACE_CHAR){
    		this.getNextCharacter();
    		if(this.nextPosition >= this.expression.length() - 1){
    			break;
    		}
    	}
    }

    private boolean isDecimalSeparator(char separator){
    	return separator == this.decimalFormatSymbols.getDecimalSeparator();
    }
    
    public OperandType lookForToken(){         
        int position = this.nextPosition;
        char current = this.currentChar;
        char next = this.nextChar;			

        OperandType result = this.getToken();

        this.nextPosition = position;
        this.currentChar = current;
        this.nextChar = next;
        
        return result;
    }
    
    public OperandType getToken(){
            if ( this.expression.length() < 1 )
            { 
                return OperandType.EMPTY;
            }

            this.removeSpaces();

            while ( this.nextChar != EOL_CHAR )
            {
               
            	if(Character.isLetter(this.nextChar)){
                	return this.searchForLiteral();
                }else if ( Character.isDigit( this.nextChar ) || 
                     this.isDecimalSeparator( this.nextChar ) )
                {
                    return this.getNumber();
                }
                else if(this.nextChar == QUOTES){
                	return this.getText();
                }
                else
                {
                    this.removeSpaces();
                    this.getNextCharacter();
                    char[] ch = {this.currentChar};
                    String operand = new String(ch);
                    OperandType operandType = Operand.getOperandType( operand );
                    if ( operandType != OperandType.EMPTY )                    
                    {
                        return operandType;
                    }
                    return OperandType.ERROR;
                }
            }

            return OperandType.END_OF_LINE;        
        }
    
    	private OperandType searchForLiteral(){
    		StringBuilder sb = new StringBuilder();
    		this.getNextCharacter();
    		sb.append(this.currentChar);
    		do{
    			sb.append(this.nextChar);
    			this.getNextCharacter();
    		}while(Character.isLetter(this.nextChar) || Character.isDigit(this.nextChar));
    			this.currentLiteral = sb.toString();
    			return OperandType.LITERAL;
    	}

       private OperandType getText(){
    	   if ( this.nextChar != '\"')
           {
				return OperandType.EMPTY;
           }
    	   this.getNextCharacter();
    	   StringBuilder sb = new StringBuilder();
    	   do{
    		   sb.append(this.nextChar);
    		   this.getNextCharacter();
    	   }while(this.nextChar != '\"' && this.nextChar != EOL_CHAR);
    	   this.lastString= sb.toString();    	   
    	   this.getNextCharacter();
    	   return OperandType.TEXT;
       }

        private OperandType getNumber(){		
            if ( !( Character.isDigit( this.nextChar ) || 
				    this.isDecimalSeparator( this.nextChar ) ||
                    this.nextChar == '-' ) )
            {
				return OperandType.EMPTY;
            }

            boolean decimalSeparatorFound	= false;

            StringBuilder stringBuilder = new StringBuilder();
            if ( this.nextChar == '-' ){
                stringBuilder.append( this.nextChar );
                this.getNextCharacter();
            }

            do{
                stringBuilder.append( this.nextChar );
                this.getNextCharacter();

                if ( decimalSeparatorFound && this.isDecimalSeparator( this.nextChar ) )
                {
                    break;
                }
                else
                {
                    decimalSeparatorFound = this.isDecimalSeparator( this.nextChar );
                }
            }
            while ( Character.isDigit( this.nextChar ) ||
                    this.isDecimalSeparator( this.nextChar ) );
            
            String stringValue = stringBuilder.toString();
            if ( !Double.isNaN(Double.parseDouble(stringValue)) ){
                this.lastNumber = Double.parseDouble(stringValue);
                return OperandType.NUMBER;
            }else{
            return OperandType.EMPTY;
            }
        }

}
