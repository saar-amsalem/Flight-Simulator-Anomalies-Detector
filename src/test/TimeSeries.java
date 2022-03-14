package test;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;  
import java.io.FileReader;
import java.io.IOException;


public class TimeSeries {
	public StatLib calc2 = null;
	public String Line = "";
	public String[] values;
	public List<String[]> saved = new ArrayList<>();
	
	public TimeSeries(String csvFileName) {  
		try   
		{  
		try (//parsing a CSV file into BufferedReader class constructor  
		BufferedReader br = new BufferedReader(new FileReader(csvFileName))) {
			while ((this.Line = br.readLine()) != null)   //returns a Boolean value  
			{  
			this.values = Line.split(",");    // use comma as separator  
			this.saved.add(this.values);
			}
		}
		
		}
		 
		catch (IOException e)   
		{  
		e.printStackTrace();  
				
		}
		
			
	}
	
	public float ConvertStringToFloat (String tocon)
	{
		float val = 0 ;
		try {
            
            val = Float.parseFloat(tocon);
        }
  
        catch (Exception e) {
            System.out.println("Exception: " + e);
        }
		return val;
	}
	public String ReturnIndexContent (int i,int j)
	{
		String buffer[] = this.saved.get(i);
		String word = buffer[j];
		return word;
	}
	public float[] ReturnCol (int j)
	{
		float[] res = new float[this.saved.size()-1];
		for (int i=1,c=0;i<this.saved.size();i++,c++)
		{
			res[c]=ConvertStringToFloat(ReturnIndexContent(i, j));
		} 
		return res;
	}
	public int getcolsize (int i)
	{
		return this.saved.size();
	}

	public Point[] ColtoPoints (int x,int y)
	{
		Point [] temp = new Point[this.getcolsize(x)-1];
		float [] x_x = this.ReturnCol(x);
		float [] y_y = this.ReturnCol(y);
		for (int i=0;i<this.getcolsize(x)-1;i++)
		{
			temp[i] = new Point(x_x[i],y_y[i]);
		}
		return temp;
	}
	public Point coltoPoints (String x,String y,int index)
	{
		int index_x = 0,index_y = 0;
		for (int i=0;i<this.values.length;i++)
		{
			if (x.equals(this.ReturnIndexContent(0, i)))
			{
				index_x = i;
				break;
			}
		}
		for (int i=0;i<this.values.length;i++)
		{
			if (y.equals(this.ReturnIndexContent(0, i)))
			{
				index_y = i;
				break;
			}
		}
		Point temp = new Point(this.ConvertStringToFloat(this.ReturnIndexContent(index, index_x)),this.ConvertStringToFloat(this.ReturnIndexContent(index, index_y)));
		return temp;
	}
	public float findmaxdevPoint (Point[] points,Line l)
	{
		float max =0;
		for (int i=0;i<points.length;i++)
		{
			if (StatLib.dev(points[i], l)>max)
			{
				max = StatLib.dev(points[i],l);
			}
		}
		return max;
	}
}
