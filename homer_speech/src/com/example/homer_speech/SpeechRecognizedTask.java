package com.example.homer_speech;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;


import android.os.AsyncTask;
import android.widget.Toast;

public class SpeechRecognizedTask extends AsyncTask<String, Integer, Boolean> {


	@Override
	protected Boolean doInBackground(String... params) {
		try {
			Socket s = new Socket(params[0],Integer.parseInt(params[1]));



			//outgoing stream redirect to socket
			OutputStream out = s.getOutputStream();
			String str = params[2]+"\0";
			PrintWriter output = new PrintWriter(out);
			//            output.println(params[1]);
			output.println(str);
			output.flush();
			//BufferedReader input = new BufferedReader(new InputStreamReader(s.getInputStream()));

			//read line(s)
			//String st = input.readLine();
			//System.out.println(st);
			//Toast.makeText(this, "asd", Toast.LENGTH_SHORT).show();

			//Close connection
			s.close();
		} 
		catch (UnknownHostException e) {
			e.printStackTrace();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}


}	

