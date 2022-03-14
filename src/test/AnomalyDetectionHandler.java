package test;


import test.Commands.DefaultIO;
import test.Server.ClientHandler;

import java.io.*;
import java.net.Socket;
import java.util.InputMismatchException;
import java.util.Scanner;

public class AnomalyDetectionHandler implements ClientHandler{

	@Override
	public void handle(Socket s) {
		try {
			SocketIO client = new SocketIO(s.getInputStream(), s.getOutputStream());
			CLI cli = new CLI(client);
			cli.start();
			client.write("bye");
			client.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public class SocketIO implements DefaultIO{

		Scanner in;
		PrintWriter out;
		public SocketIO(InputStream inFromClient, OutputStream outToClient) {
			in = new Scanner(new InputStreamReader(inFromClient));
			out = new PrintWriter(new OutputStreamWriter(outToClient),true);
		}

		@Override
		public String readText() {
			try {
				return in.nextLine();
			} catch (InputMismatchException e) {
				e.printStackTrace();
				return null;
			}
		}

		@Override
		public void write(String text) {
			out.println(text);
		}

		@Override
		public float readVal() {
			return in.nextFloat();
		}

		@Override
		public void write(float val) {
			out.print(val);
		}

		public void close() {
			try {
				in.close();
				out.close();
			}catch(InputMismatchException e){e.printStackTrace();}
		}
	}
}
