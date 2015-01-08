package com.example.homer_speech;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;


import android.os.AsyncTask;

public class SpeechRecognizedTask extends AsyncTask<String, Integer, Boolean> {


	@Override
	protected Boolean doInBackground(String... params) {
        try {
            Socket s = new Socket(params[0],Integer.parseInt(params[1]));

            OutputStream out = s.getOutputStream();
            String str = params[2]+"\0";
            PrintWriter output = new PrintWriter(out);
            output.println(str);
            output.flush();


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

