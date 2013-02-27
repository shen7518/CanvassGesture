package vassilina.tatarlieva.welcomeactivity;

import java.util.List;

import vassilina.tatarlieva.canvassgesturetest.Grid;

import vassilina.tatarlieva.canvassgesturetest.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class FileListAdapter extends ArrayAdapter<String> {

	public FileListAdapter(Context context,  int textViewResourceId,
			List<String> objects) {
		super(context, textViewResourceId, objects);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		 View v = convertView;
         if (v == null) {
             LayoutInflater vi = (LayoutInflater)getContext().getSystemService(
             		Context.LAYOUT_INFLATER_SERVICE);
             v = vi.inflate(R.layout.list_layout, null);
         }
         
         TextView fileName = (TextView)v.findViewById(R.id.fileName);
         ImageView fileImage = (ImageView)v.findViewById(R.id.fileImage);
         
         String fileString= this.getItem(position);
         fileName.setTextSize(Grid.TEXT_FONT);
         fileName.setText(fileString);
         fileName.setPadding(Grid.TEXT_FONT, Grid.TEXT_FONT, Grid.TEXT_FONT, Grid.TEXT_FONT);
         fileImage.setImageResource(R.drawable.icon);
         
         return v;
	}

}
