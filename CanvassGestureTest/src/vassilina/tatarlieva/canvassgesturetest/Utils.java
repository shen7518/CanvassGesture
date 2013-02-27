package vassilina.tatarlieva.canvassgesturetest;



public class Utils {
	public static String getColumnHeaderText(int headerIndex) {
		headerIndex++;
		StringBuilder sb = new StringBuilder();
		while (headerIndex > 0) {
			headerIndex--;
			sb.append((char) (headerIndex % 26 + 65));
			headerIndex = headerIndex / 26;
		}

		return sb.reverse().toString();
	}
	
	public static String keyGen(GridPoint p){
		return Integer.toString(p.row()) + "?" + Integer.toString(p.column());
	}
}
