package test;

import java.util.ArrayList;

import test.Commands.Command;
import test.Commands.DefaultIO;

public class CLI {

	ArrayList<Command> commands;
	DefaultIO dio;
	Commands c;
	
	public CLI(DefaultIO dio) {
		this.dio=dio;
		c=new Commands(dio); 
		commands=new ArrayList<>();
		commands.add(c. new print_menu()); // index 0
		commands.add(c. new CSV_upload_Client());// index 1
		commands.add(c. new algorithm_set()); // index 2
		commands.add(c. new anomaly_detection()); // index 3
		commands.add(c. new anomaly_report()); // index 4
		commands.add(c. new anomalies_analyze()); // index 5
		commands.add(c. new exit_menu()); // index 6

		// implement
	}
	
	public void start() {
		
		commands.get(0).execute();
		String act = dio.readText();
		int index = Integer.parseInt(act);
		while (index != 6||act.equals("bye"))
		{
			commands.get(index).execute();
			commands.get(0).execute();
			index = (int)dio.readVal();
		}
		commands.get(6).execute();
		
	}
}
