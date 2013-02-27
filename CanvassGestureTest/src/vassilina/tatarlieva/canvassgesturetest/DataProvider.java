package vassilina.tatarlieva.canvassgesturetest;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map.Entry;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xmlpull.v1.XmlSerializer;

import vassilina.tatarlieva.evaluations.CellContent;
import vassilina.tatarlieva.evaluations.CellDataType;
import vassilina.tatarlieva.evaluations.Evaluator;
import vassilina.tatarlieva.evaluations.Operand;
import vassilina.tatarlieva.evaluations.Operand.OperandType;
import android.content.Context;
import android.graphics.Paint;
import android.util.Xml;

public class DataProvider {
	private String fileName;
	private Evaluator evaluator;
	public String getFileName(){
		return this.fileName;
	}
	
	private static Paint dataPaint = new Paint();
	private Grid grid;
	private HashMap<GridPoint, CellContent> dataHashMap ;
	static{
		dataPaint.setARGB(255, 250, 0, 0);
		dataPaint.setStyle(Paint.Style.FILL);
		dataPaint.setTextSize(Grid.TEXT_FONT);
		dataPaint.setAntiAlias(true);
	}
	
	public DataProvider(Grid grid, String fileName){
		this.grid = grid;
		this.fileName = fileName;
		this.dataHashMap = new HashMap<GridPoint, CellContent>();
		this.evaluator = new Evaluator();
	}
	
	public static Paint dataPaint(){
		return DataProvider.dataPaint;
	}
	
	public String getCellValue(GridPoint p){
		CellContent cellContent = dataHashMap.get(p);
		if(cellContent == null){
			return null;
		}else{ 
			return cellContent.getEntryText();
		}
	}
	
	public CellContent getCellContent(GridPoint p){
		CellContent cellContent = dataHashMap.get(p);
		if(cellContent!= null){
			return cellContent;
		}else{
			return new CellContent(null);
		}
			
		
	}
	
	public boolean hasCellValue(GridPoint p){
		return dataHashMap.containsKey(p);
	}
	
	public void setCellValue(GridPoint p, CellContent cellContent){
		CellContent cellcontent = cellContent;
		dataHashMap.put(p, cellcontent);
		this.recalculate();
	}
	
	public void deleteCellValue(GridPoint p){
		dataHashMap.remove(p);
	}
	
	private void unvisitAll(){
		for(Entry<GridPoint, CellContent> entry: dataHashMap.entrySet()){
			CellContent currentCell = entry.getValue();
			currentCell.unvistit();
		}
	}
	
	public void recalculate(){
		this.unvisitAll();
		for(Entry<GridPoint, CellContent> entry: dataHashMap.entrySet()){
			CellContent currentCell = entry.getValue();
           if(currentCell.getEntryType() == CellDataType.FORMULA ){
        	   Operand result = evaluator.evaluate(entry.getKey(), this);
        	   if(result == Operand.error){
        		   currentCell.setResultType(CellDataType.ERROR);
        	   }else if(result.getOperandType() == OperandType.NUMBER){
        		   currentCell.setResultType(CellDataType.NUMBER);
        		   currentCell.setDisplayNumber(result.getNumber());
        		   currentCell.setDisplayText(null);
        	   }else if(result.equals(Operand.empty)){
        		   currentCell.setResultType(CellDataType.EMPTY);
        		   currentCell.setDisplayText(null);
        		   currentCell.setDisplayNumber(0);
        	   }else{
        		   if(result.getString() != ""){
        			   currentCell.setDisplayText(result.getString());
        			   currentCell.setResultType(CellDataType.TEXT);
        		   }else{
        			   currentCell.setDisplayText(null);
        			   currentCell.setDisplayNumber(0);
        			   currentCell.setResultType(CellDataType.EMPTY); 
        		   }
        		   currentCell.setDisplayNumber(0);
        	   }
        	   currentCell.setVisited();    
           }
        }
		this.grid.invalidate();
	}
	
	
	public void saveFile(String fileName) throws IOException{
		if(fileName != null){
			this.fileName = fileName;
		}
		String data = this.dataSerialization();
		FileOutputStream fos = this.grid.getContext().openFileOutput(this.fileName, Context.MODE_PRIVATE);
		fos.write(data.getBytes());
		fos.close();
	}
	//the data serialization
	private String dataSerialization() throws IOException{
		XmlSerializer serializer = Xml.newSerializer();
	    StringWriter writer = new StringWriter();
	    
	    try {
	        serializer.setOutput(writer);
	        serializer.startDocument("UTF-8", true);
	        serializer.startTag("", "cells");
	        for(Entry<GridPoint, CellContent> entry: dataHashMap.entrySet()){
	            serializer.startTag("", "cell");
	            serializer.attribute("", "id", entry.getKey().toString());
	            serializer.text(entry.getValue().toString());
	            serializer.endTag("", "cell");
	        }
	        serializer.endTag("", "cells");
	        serializer.endDocument();
	        return writer.toString();
	    } catch (Exception e) {
	        throw new RuntimeException(e);
	    } 

	}
		
	
	public void reloadFile() throws FileNotFoundException{
		this.dataHashMap = new HashMap<GridPoint, CellContent>();
		this.dataDeserialization();
		this.recalculate();
	}
	
	private void dataDeserialization() throws FileNotFoundException{
		try {
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser sp = spf.newSAXParser();
			XMLReader xr = sp.getXMLReader();
			XMLHandler myHandler = new XMLHandler(this.dataHashMap);
			xr.setContentHandler(myHandler);
			xr.parse(new InputSource(this.grid.getContext().openFileInput(this.fileName)) );
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
	}
	
	//ready
	public void closeFile() throws IOException{
		this.saveFile(null);
	}

	public void load() throws FileNotFoundException{
		this.dataDeserialization();
		this.recalculate();
	}
	
	//the class that handles the parsing of the XML
	private class XMLHandler extends DefaultHandler{
		
		private HashMap<GridPoint, CellContent> hm;
		private StringBuffer accumulator = new StringBuffer();
		private GridPoint currentKey;
		public XMLHandler(HashMap<GridPoint, CellContent> dataHashMap){
			super();
			this.hm = dataHashMap;
		}
		@Override
		public void characters(char[] buffer, int start, int length)
				throws SAXException {
			super.characters(buffer, start, length);
			this.accumulator.append(buffer, start, length);
		}
		
		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {
			super.startElement(uri, localName, qName, attributes);
			accumulator.setLength(0);
			if(attributes.getValue(0) != null){
				this.currentKey = GridPoint.toGridPoint(attributes.getValue(0));
			}
			
		}
		
		@Override
		public void endElement(String uri, String name, String qName)
				throws SAXException {
			super.endElement(uri, name, qName);
			if (accumulator.length() > 0) {
				this.hm.put(this.currentKey,  new CellContent(accumulator.toString().trim()));
				accumulator.setLength(0);
			}
		}	
	}
}
