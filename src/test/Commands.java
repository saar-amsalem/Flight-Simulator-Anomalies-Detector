package test;


import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class Commands {
	
	// Default IO interface
	public interface DefaultIO{
		public String readText();
		public void write(String text);
		public float readVal();
		public void write(float val);

		// you may add default methods here
	}
	
	// the default IO to be used in all commands
	DefaultIO dio;
	public Commands(DefaultIO dio) {
		this.dio=dio;
	}
	
	// the shared state of all commands
	private class SharedState{
		int P;
		int TP;
		int FP;
		int n;
		SimpleAnomalyDetector sad_d;
		List<String[]> anomalies_reported_from_client;
		List<long[]> grouped_anomalies;
		int times;

		public SharedState()
		{
			this.anomalies_reported_from_client = new ArrayList<>();
			this.grouped_anomalies = new ArrayList<>();
			this.sad_d=new SimpleAnomalyDetector();
			this.n=0;
			this.P =0;
			this.TP = 0;
			this.FP = 0;
			this.times = 1;
		}
		
		public void anomaly_detector_builder_detect(String filename)
		{
			sad_d.detect(new TimeSeries(filename));
		}
		public void anomaly_detector_builder_learn(String filename)
		{
			sad_d.learnNormal(new TimeSeries(filename));
		}

		public void grouping()
		{
			boolean flag = false;
			int size = sharedState.sad_d.AR.size();
			this.grouped_anomalies = new ArrayList<>();
			long[] tempStrings = new long[2];
			int startindex = 0;
			int endindex =1;
			for(int i=0,c=i;i<size-1;i++,c=i)
			{
				for(int j=i+1;j<size;j++)
				{
					if(sharedState.sad_d.AR.get(i).timeStep+1 == sharedState.sad_d.AR.get(j).timeStep && sharedState.sad_d.AR.get(i).description.equals(sharedState.sad_d.AR.get(j).description))
					{
						tempStrings[startindex] = sharedState.sad_d.AR.get(c).timeStep;
						flag = true;
						i++;
					} 
					if(!flag)
					{	
						break;
					}
					flag = false;
				}
				tempStrings[endindex] = sharedState.sad_d.AR.get(i).timeStep;
				this.grouped_anomalies.add(tempStrings);
				tempStrings = new long[2];
			}
			this.times++;
		}
	
	}

	public class CSV_generator
	{
		PrintWriter out;
		public CSV_generator(String filename)
		{
			String line;
			try {
				out=new PrintWriter(new FileWriter(filename),true);
				line = dio.readText();
				while (!(line.equals("done")))
				{
					if(line.equals("")||line.isEmpty()||line == "")
					{
						continue;
					}
					out.println(line);
					line = dio.readText();
					sharedState.n++;
				}	
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}

	public class CSV_reader
	{
		Scanner in;
		public CSV_reader(String filename)
		{
			try {
				in=new Scanner(new FileReader(filename));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		public String readtext()
		{
			return this.in.nextLine();
		}
		public long readVal()
		{
			return this.in.nextLong();
		}
	}
	private  SharedState sharedState=new SharedState();

	
	// Command abstract class
	public abstract class Command{
		protected String description;
		
		public Command(String description) {
			this.description=description;
		}
		
		public abstract void execute();
	}
	
	// Command class for example:
	public class algorithm_set extends Command{

		public algorithm_set() {
			super(null);
		}

		@Override
		public void execute() {
			sharedState.anomaly_detector_builder_learn("Train.csv");
			dio.write("the current correlation threshold is : "+ sharedState.sad_d.correlationVal);
			dio.write("Type a new threshold : ");
			float new_corr = dio.readVal();
			while(new_corr<0 || new_corr >1){
			dio.write("please enter a value between 0 and 1 ");
			new_corr = dio.readVal();
			}
			sharedState.sad_d.correlationVal = new_corr;
		}		
	}

	public class CSV_upload_Client extends Command{

		
		public CSV_upload_Client()
		{
			super(null);
		}
		@Override
		public void execute()
		{
			dio.write("Please upload your local train CSV file.");
			CSV_generator train = new CSV_generator("Train.csv");
			dio.write("Upload complete.");
			dio.write("Please upload your local test CSV file.");
			CSV_generator test = new CSV_generator("Test.csv");
			dio.write("Upload complete.");
			
		}
	}
	public class print_menu extends Command{

		public print_menu()
		{
			super(null);
		}
		@Override
		public void execute()
		{
			dio.write("Welcome to the Anomaly Detection Server.\nPlease choose an option:");
			dio.write("1. upload a time series csv file");
			dio.write("2. algorithm settings");
			dio.write("3. detect anomalies");
			dio.write("4. display results");
			dio.write("5. upload anomalies and analyze results");
			dio.write("6. exit");
		}

	}

	public class anomaly_detection extends Command
	{
		
		
		public anomaly_detection()
		{
			super (null);
		}
	
		@Override
		public void execute()
		{
			dio.write("anomaly detection complete.");
			sharedState.anomaly_detector_builder_detect("Test.csv");
			
		}
	}

	public class anomaly_report extends Command{

		public anomaly_report()
		{
			super (null);
		}
		public void print_AR(AnomalyReport ar)
		{
			dio.write(ar.timeStep + "\t" + ar.description);
		}
		@Override
		public void execute()
		{
			int size = sharedState.sad_d.AR.size();
			for (int i = 0;i<size;i++)
			{
				this.print_AR(sharedState.sad_d.AR.get(i));
			}
			dio.write("done");
		}

	}
	
	public class anomalies_analyze extends Command
	{

		public anomalies_analyze()
		{
			super (null);
		}
		
		@Override
		public void execute()
		{
			dio.write("Please upload your local anomalies file.");
			String line;
			String[] temp;
			sharedState.anomalies_reported_from_client = new ArrayList<>();
			boolean flag = false;
			line = dio.readText();	
				while (!(line.equals("done")))
				{	
					if(!(line.equals(""))&& line != "")
					{
						temp = line.split(",");
						sharedState.P++;
						sharedState.anomalies_reported_from_client.add(temp);
						flag = true;
						line = dio.readText();
						
					}
					if (!flag)
					{
						line = dio.readText();
					}
				}

			dio.write("Upload complete.");
			if(sharedState.times ==1)
			{	
				sharedState.grouping();
			}
			
			int t=0;
			float[] dd = new float[(sharedState.P)];
			String[] line_for_analyze;
			boolean[] reported = new boolean[sharedState.grouped_anomalies.size()];
			for (int j = 0;j<sharedState.P;j++)
			{
				line_for_analyze = sharedState.anomalies_reported_from_client.get(j);
				long num1 = Long.parseLong(line_for_analyze[0]);//start of client_report
				long num2 = Long.parseLong(line_for_analyze[1]);//stop of client_report
				dd[j] = (float)num2-(float)num1+1;
				
				while(t<sharedState.grouped_anomalies.size())
				{
					
					long start_of_detector = sharedState.grouped_anomalies.get(t)[0];
					long end_of_detector = sharedState.grouped_anomalies.get(t)[1];
					
					if(start_of_detector > num2 || end_of_detector < num1)
					{
						if (!reported[t])
						{
							sharedState.FP++;
							reported[t] = true;
						}
						t++;
					}
					else
					{
						if(!reported[t])
						{
							sharedState.TP++;
							reported[t] = true;
						}
						else
						{
							sharedState.FP--;
							sharedState.TP++;
						}
						t++;
					}
				}
				t=0;

			}
			float TP = (float)sharedState.TP/(float)sharedState.P;
			DecimalFormat df = new DecimalFormat("#0.0");
			df.setMaximumFractionDigits(3);
			df.setRoundingMode(RoundingMode.DOWN);
			dio.write("True Positive Rate: "+df.format(TP));
			float N = (sharedState.n-2)/2;
			int s = 0;
			while (s<sharedState.P)
			{
				N = N - dd[s];
				s++;
			}
			
			float FP =(float)sharedState.FP/N;
			
			dio.write("False Positive Rate: "+df.format(FP));
			sharedState.P =0;
			sharedState.FP =0;
			sharedState.TP =0;
			

		}
	}
	public class exit_menu extends Command
	{

		public exit_menu()
		{
			super (null);
		}
		
		@Override
		public void execute()
		{
		}

	}

	// implement here all other commands
	
}
