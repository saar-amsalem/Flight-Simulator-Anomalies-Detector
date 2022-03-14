package test;

public class StatLib {

	

	// simple average
	public static float avg(float[] x){
		float sum =0;
		for (int i=0;i<x.length;i++)
		{
			sum = sum + x[i];
		}
		float avg = sum/x.length;
		return avg;
	}

	// returns the variance of X and Y
	public static float var(float[] x){
		float avg = avg(x);
		float sum=0;
		for (int i = 0;i<x.length;i++)
		{
			sum += ((x[i]-avg)*(x[i]-avg));
		}
		return (sum/x.length);
	}

	// returns the covariance of X and Y
	public static float cov(float[] x, float[] y){
		float sum =0;
		for (int i=0;i<x.length;i++)
		{
			sum+= (x[i]-avg(x))*(y[i]-avg(y));
		}
		
		return sum/x.length;
	}


	// returns the Pearson correlation coefficient of X and Y
	public static float pearson(float[] x, float[] y){
		return cov(x,y)/((float)Math.sqrt(var(x))*(float)(Math.sqrt(var(y))));
	}

	// performs a linear regression and returns the line equation
	public static Line linear_reg(Point[] points){
		float x[] = new float[points.length]; 
		float y []=new float[points.length];
		for (int i=0;i<points.length;i++)
		{
			x[i]=points[i].x;
			y[i]=points[i].y;
		}
		float a = cov(x,y)/var(x);
		float b = avg(y)-a*avg(x);
		Line ret = new Line(a,b);
		return ret;
		
	}

	// returns the deviation between point p and the line equation of the points
	public static float dev(Point p,Point[] points){
		Line kav = linear_reg(points);
		float res = kav.f(p.x)-p.y;
		if (res < 0 )
		{
		return -res;
		}
		return res;
	}

	// returns the deviation between point p and the line
	public static float dev(Point p,Line l){
		float res = l.f(p.x)-p.y;
		if (res < 0)
		{
			return -res;
		}
		return res;
	}
	
}
