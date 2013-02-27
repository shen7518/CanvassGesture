package vassilina.tatarlieva.evaluations;

import java.util.ArrayList;
import java.util.List;

import vassilina.tatarlieva.evaluations.Operand.OperandType;

	
public class Interpreter {
	private ArrayList<Operand> operandsList ;
	public List<Operand> getOperandList() {
		return this.operandsList;
	}
	public boolean interpret(String expression) {
		this.operandsList = new ArrayList<Operand>();
		Tokenizer tokenizer = new Tokenizer(expression);
		
		boolean result = this.interpretExpression( tokenizer );
        /*if ( !tokenizer.hasReachedEnd() ){
            result = false;
        }*/
		return result;
	}
	
	
	private boolean interpretExpression(Tokenizer tokenizer){
		OperandType operandType = tokenizer.lookForToken();
		if(operandType == OperandType.ERROR){
			tokenizer.getToken();
        	this.push(operandType, 0);
        	return true;
		}else{
			return this.interpretAdditiveExpression(tokenizer);
		}
	}

	private boolean interpretAdditiveExpression(Tokenizer tokenizer){
		boolean result = this.interpretMultiplicativeExpression(tokenizer);
		
		 if ( !result )
         { 
             return result;
         }
		 
		 result &= this.interpretAdditiveExpression1(tokenizer);
		 
		 return result;
	}

	
	private boolean interpretAdditiveExpression1(Tokenizer tokenizer){
		 boolean result = true;
         OperandType operandType = tokenizer.lookForToken();
         if ( operandType == OperandType.PLUS ||
              operandType == OperandType.MINUS )
         { 
             tokenizer.getToken();
             result = this.interpretMultiplicativeExpression( tokenizer );
             if ( result )
             { 
                 this.push( operandType, 2 );
                 result &= this.interpretAdditiveExpression1( tokenizer );
             }
         }
         if(operandType == OperandType.ERROR){
 			tokenizer.getToken();
         	this.push(operandType, 0);
         	return true;
         }else{
        	 return result;
         }
	}
	
        private boolean interpretMultiplicativeExpression(Tokenizer tokenizer) {
        	boolean result = this.interpretUnaryExpression( tokenizer );
            if ( !result )
            { 
                return result;
            }
            result &= this.interpretMultiplicativeExpression1( tokenizer );
            return result;
	}
       
        private boolean interpretMultiplicativeExpression1( Tokenizer tokenizer ){ 
            boolean result = true;
            OperandType operandType = tokenizer.lookForToken();
            if ( operandType == OperandType.MULTIPLY ||
                 operandType == OperandType.DIVIDE )
            { 
                tokenizer.getToken();
                result = this.interpretUnaryExpression( tokenizer );
                if ( result )
                { 
                    this.push( operandType , 2 );
                    result &= this.interpretMultiplicativeExpression1( tokenizer );
                }
            }
            if(operandType == OperandType.ERROR){
     			tokenizer.getToken();
             	this.push(operandType, 0);
             	return true;
             }else{
            	 return result;
             }
        }
        
        //the mighty unary expression
        private boolean interpretUnaryExpression(Tokenizer tokenizer){
        	boolean result = true;
        	OperandType operandType = tokenizer.lookForToken();
        	if(operandType == OperandType.MINUS || operandType == OperandType.PLUS){
        		tokenizer.getToken();
                result = this.interpretUnaryExpression( tokenizer );
                if(result){
                	this.push(operandType, 1);
                }
        	}
        	else
        	{        	
        		result &= this.interpretPrimaryExpression(tokenizer);
        	}
        	
			return result;
        }
        private boolean interpretPrimaryExpression( Tokenizer tokenizer ){ 
            OperandType operandType = tokenizer.lookForToken();
            
            if ( operandType == OperandType.LEFT_PARENTHESES)
            {   
                tokenizer.getToken();
                
                boolean result = this.interpretExpression( tokenizer );
                if ( !result )
                {
                    return false;
                }

                operandType = tokenizer.getToken();
                if ( operandType != OperandType.RIGHT_PARENTHESES)
                {                    
                    return false;
                }

                return true;
            }
            else if ( operandType == OperandType.NUMBER )
            {				
                tokenizer.getToken();
                this.push( tokenizer.getLastNumber() );
                return true;
            }else if(operandType == OperandType.TEXT){
            	tokenizer.getToken();
            	this.push(tokenizer.getLastString(), false);
            	return true;
            }else if(operandType == OperandType.LITERAL){
            	tokenizer.getToken();
            	String literal = tokenizer.getLastLiteral();
            	if(literal.matches("[a-zA-Z]+[1-9]+")){
            		this.push(literal, true);
            		return true;
            	}else{
            		return false;
            	}
            }else if(operandType == OperandType.ERROR){
            	tokenizer.getToken();
            	this.push(operandType, 0);
            	return true;
            }
            return false;
        }

       
        private void push( OperandType operandType, int parametersCount ){ 
            this.operandsList.add( 0, new Operand( operandType, parametersCount ) );
        }

        private void push( double number ){ 
            this.operandsList.add( 0, new Operand( number ) );
        }
        
        private void push(String text, boolean isLiteral){
        	this.operandsList.add(0, new Operand(text, isLiteral));
        }
        
        
    
}

