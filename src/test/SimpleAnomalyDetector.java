package test;

import java.util.ArrayList;
import java.util.List;

public class SimpleAnomalyDetector implements TimeSeriesAnomalyDetector {

		StatLib calc = null;
		public List<AnomalyReport> AR = new ArrayList<>();
		public List<CorrelatedFeatures> CF = new ArrayList<>();
		public float correlationVal = (float) 0.9;
		@Override
		public void learnNormal(TimeSeries ts) {

			CorrelatedFeatures temp = null; 
			float correlation = 0;
			float max = this.correlationVal;
			int length = ts.saved.get(0).length;
			for (int j=0;j<length;j++)
			{
				for (int i=j+1;i<length;i++)
				{
					correlation = Math.abs(StatLib.pearson(ts.ReturnCol(j),ts.ReturnCol(i)));
					if (correlation>= this.correlationVal&& correlation >=max)
					{	max = correlation;
						temp = new CorrelatedFeatures(ts.ReturnIndexContent(0, j), ts.ReturnIndexContent(0, i), correlation, StatLib.linear_reg(ts.ColtoPoints(j, i)), (float) (ts.findmaxdevPoint(ts.ColtoPoints(j, i), StatLib.linear_reg(ts.ColtoPoints(j, i)))*1.1));
						this.CF.add(temp);
					}
				}	
				max = this.correlationVal;
			}
			
			
			

		}
	
	
		@Override
		public List<AnomalyReport> detect(TimeSeries ts) {
			
			for (int t =0 ; t<this.CF.size();t++)
			{
			CorrelatedFeatures todetect = this.CF.get(t);
			AnomalyReport temp = null;
			String i = todetect.feature1;
			String j = todetect.feature2;
			Line l = todetect.lin_reg;
			String deli = "-";
			Point[] tspoint = new Point[ts.getcolsize(0)-1];
			for (int k=0;k<tspoint.length;k++)
			{
				tspoint[k] = ts.coltoPoints(i, j, k+1);
			}
			for (int c=0;c < tspoint.length;c++)
			{
				if (StatLib.dev(tspoint[c],l)>todetect.threshold)
				{
					String des =new String(i+deli+j);
					temp = new AnomalyReport(des,c+1);
					this.AR.add(temp);
				}
			}

			}
			return this.AR;
		}
		
		public List<CorrelatedFeatures> getNormalModel(){
			return this.CF;
		}
	}