package vassilina.tatarlieva.evaluations;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;

import vassilina.tatarlieva.canvassgesturetest.DataProvider;
import vassilina.tatarlieva.canvassgesturetest.GridPoint;
import vassilina.tatarlieva.evaluations.Operand.OperandType;

public class Evaluator {
	private static int MAX_STACK_SIZE = 10;
	private DataProvider dataProvider;
	private HashSet<GridPoint> tree = new HashSet<GridPoint>();
	
	public Operand evaluate(GridPoint gridPoint, DataProvider dataProvider){
		return this.evaluate(gridPoint, dataProvider, true);
	}
	
	private Operand evaluate(GridPoint gridPoint, DataProvider dataProvider, boolean clearTree){
		if(clearTree){
			this.tree.clear();
		}
		
		
		if(this.tree.contains(gridPoint)){
			return Operand.error;
		}
		
		this.dataProvider = dataProvider;
		CellContent cellContent = this.dataProvider.getCellContent(gridPoint);
		
		this.tree.add(gridPoint);
		
		
		if(cellContent.getResultType() == CellDataType.ERROR){
			return Operand.error;
		}
		
		Interpreter interpreter = new Interpreter();
		boolean isParsed = interpreter.interpret(cellContent.getEntryText());
		
		
		if(!isParsed){
			return Operand.error;
		}
		List<Operand> operandsList = interpreter.getOperandList(); 
		Stack<Operand> resultStack = new Stack<Operand>();
		
		for(int index = operandsList.size() - 1 ; index >= 0; index-- ){
			Operand current = operandsList.get(index);
			if(current.getOperandType() == OperandType.NUMBER){
				resultStack.push(current);
			}
			else if(current.getOperandType() == OperandType.PLUS){
				this.processPlus(resultStack , current.getParametersCount());
			}
			else if(current.getOperandType() == OperandType.MINUS){
				this.processMinus(resultStack , current.getParametersCount());
			}
			else if(current.getOperandType() == OperandType.MULTIPLY){
				this.processMultiply(resultStack);
			}
			else if(current.getOperandType() == OperandType.DIVIDE){
				this.processDivision(resultStack);
			}else if(current.getOperandType() == OperandType.TEXT){
				resultStack.push(current);
			}else if(current.getOperandType() == OperandType.LITERAL){
				this.processLiteral(current, resultStack);
			}else if(current.getOperandType() == OperandType.ERROR){
				resultStack = new Stack<Operand>();
				resultStack.push(Operand.error);
			}
		}
		
		Operand result = resultStack.pop();
		
		if(resultStack.size() > 0 ){
			return Operand.empty;
		}
		
		return result;
	}


	private void processLiteral(Operand current,Stack<Operand> resultStack){
		String cellAddress = current.getString();
		GridPoint p = new GridPoint(cellAddress);
		
		if(this.tree.size() > MAX_STACK_SIZE){
			resultStack.push(Operand.error);
			return;
		}
		Operand result;
		CellContent cellContent = this.dataProvider.getCellContent(p);
		if(cellContent == null || cellContent.getResultType() == CellDataType.EMPTY ){
			resultStack.push(Operand.empty);
		}else{
			if(cellContent.getEntryType() == CellDataType.FORMULA ){
				
				
				result = this.evaluate(p, dataProvider, false);
				this.tree.add(p); 
				 if( result == null )
				 {
					 resultStack.push(Operand.empty);
				 }
				 else if(result == Operand.error){
					cellContent.setResultType(CellDataType.ERROR);
					resultStack.push(Operand.error);
				}else if(result == Operand.empty){
					cellContent.setResultType(CellDataType.EMPTY);
					resultStack.push(Operand.empty);
				}else{
					if(result.getOperandType() == OperandType.NUMBER){
						cellContent.setResultType(CellDataType.NUMBER);
						cellContent.setDisplayNumber(result.getNumber());
					}else{
						cellContent.setResultType(CellDataType.TEXT);
						cellContent.setDisplayText(result.getString());
					}
					resultStack.push(result);
				}
			}else{
				if(cellContent.getResultType() == CellDataType.EMPTY){
					resultStack.push(Operand.empty);
				}else if(cellContent.getResultType() == CellDataType.ERROR){
					resultStack.push(Operand.error);	
				}else{
					try{
						resultStack.push(new Operand(Double.parseDouble(cellContent.getDisplayContents())));
					}catch(NumberFormatException nfe){
						resultStack.push(new Operand(cellContent.getDisplayContents(), false));
					}
				}		
			}
		}
		//cellContent.setVisited();
	}
	
	
	
	private void processPlus(Stack<Operand> resultStack , int parametersCount){
		Operand result = null;
		if(parametersCount == 1){
			Operand operand1 = resultStack.pop();
			if(operand1 == null){
				operand1 = Operand.empty;
			}
			if(operand1.getOperandType() == OperandType.TEXT){
				result = new Operand(operand1.getString(), false);
			}else if(operand1.getOperandType() == OperandType.EMPTY){
				result = new Operand(operand1.getNumber());
			}else{
				result = new Operand(operand1.getNumber());
			}
			if(operand1.getOperandType() == OperandType.ERROR){
				result = Operand.error;
			}
		}else{
			Operand operand1 = resultStack.pop();
			Operand operand2 = resultStack.pop();
			if(operand1 == null){
				operand1 = Operand.empty;
			}
			if(operand2 == null){
				operand2 =Operand.empty;
			}
		
			
			if(operand1.getOperandType() == OperandType.NUMBER && operand2.getOperandType() == OperandType.NUMBER){
				result = new Operand(operand2.getNumber() + operand1.getNumber());
			}else if(operand1.getOperandType() == OperandType.NUMBER && operand2 == Operand.empty){
				result = new Operand(operand1.getNumber());
			}else if(operand1 == Operand.empty && operand2.getOperandType() == OperandType.NUMBER){
				result = new Operand(operand2.getNumber());
			}else if(operand1.getOperandType() == OperandType.TEXT && operand2 == Operand.empty){
				result = new Operand(operand1.toString(false), false);
			}else if(operand2.getOperandType() == OperandType.TEXT && operand1== Operand.empty){
				result = new Operand(operand2.toString(false), false);
			}else if(operand2 == Operand.empty && operand1 == Operand.empty){
				result = Operand.empty;
			}else{
				result = new Operand((operand2.toString(false) + operand1.toString(false)), false);
			}
			
			if(	operand1.getOperandType() == OperandType.ERROR ||
				operand2.getOperandType() == OperandType.ERROR ){
				result = Operand.error;
			}
			
		}
		
		resultStack.push(result);
	}
	
	
	private void processMinus(Stack<Operand> resultStack , int parametersCount){
		Operand result = null;
		if(parametersCount == 1){
			Operand operand1 = resultStack.pop();
			if(operand1== null){
				operand1 = Operand.empty;
			}
			result = new Operand(-operand1.getNumber());
			if(operand1.getOperandType() == OperandType.TEXT || 
				operand1.getOperandType() == OperandType.ERROR){
				result = Operand.error;
			}
		}else{
			Operand operand1 = resultStack.pop();
			Operand operand2 = resultStack.pop();
			if(operand1 == null){
				operand1 = Operand.empty;
			}
			if(operand2 == null){
				operand2 = Operand.empty;
			}
			if(operand1 == Operand.empty && operand2== Operand.empty){
				result = Operand.empty;
			}else{
				result = new Operand(operand2.getNumber() - operand1.getNumber());
			}
			if(operand1.getOperandType() == OperandType.TEXT || 
					operand1.getOperandType() == OperandType.ERROR ||
					operand2.getOperandType() == OperandType.TEXT || 
					operand2.getOperandType() == OperandType.ERROR
					){
				result = Operand.error;
			}
			
		}
		
		resultStack.push(result);
	}
	
	private void processMultiply(Stack<Operand> resultStack){
		Operand operand1 = resultStack.pop();
		Operand operand2 = resultStack.pop();
		Operand result;
		if(operand1 == null){
			operand1 = Operand.empty;
		}
		if(operand2 == null){
			operand2 = Operand.empty;
		}
		if(operand1 == Operand.empty && operand2== Operand.empty){
			result = Operand.empty;
		}else{
			result = new Operand(operand1.getNumber() * operand2.getNumber());
		}
		if(		operand1.getOperandType() == OperandType.TEXT || 
				operand1.getOperandType() == OperandType.ERROR ||
				operand2.getOperandType() == OperandType.TEXT ||
				operand2.getOperandType() == OperandType.ERROR){
			result = Operand.error;
		}
		
		resultStack.push(result);
	}
	
	
	private void processDivision(Stack<Operand> resultStack){
		Operand operand1 = resultStack.pop();
		Operand operand2 = resultStack.pop();
		Operand result;
		if(operand1 == null){
			operand1 = Operand.empty;
		}
		if(operand2 == null){
			operand2 = Operand.empty;
		}
		if(operand1 == Operand.empty && operand2== Operand.empty){
			result = Operand.error;
		}else{
			result = new Operand(operand2.getNumber() / operand1.getNumber());
		}
		if(		operand1.getOperandType() == OperandType.TEXT || 
				operand1.getOperandType() == OperandType.ERROR ||
				operand2.getOperandType() == OperandType.TEXT || 
				operand2.getOperandType() == OperandType.ERROR||
				operand1.getNumber() == 0){
			result = Operand.error;;
		}
		
		resultStack.push(result);
	}
	
	public boolean validateString(String string){
		return string.matches("=[^=]*");
		
	}
	public boolean wasText(String string){
		if (string.matches("=[0-9*-/+()]*") && !string.matches("=[-/+()]*")){
			return false;
		}else{
			return true;
		}
	}
}
