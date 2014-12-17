package com.example.homer_speech;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;



public class MainActivity extends Activity implements OnInitListener, android.view.View.OnClickListener{
	public static final String TAG = "homer_speech";
	static TextToSpeech tts;
	static SpeechRecognizer speech_recognizer;
	static SocketServerThread socket_server_thread;
	static Intent intent;
	//static ServerSocket server_socket;

	void speak(String text)
	{
		if (!tts.isSpeaking()) {
			TextView tv = (TextView) findViewById(R.id.textView2);
			tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
			Log.d(TAG, "Speaking "+text);	

			tv.setText("Speaking "+text);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		tts = new TextToSpeech(this, (OnInitListener) this);
		speech_recognizer = SpeechRecognizer.createSpeechRecognizer(this);
		//        socket_server_thread = new SocketServerThread();
		//        socket_server_thread.run();


		/// speech recognition
		intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		//intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
		// intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,"voice.recognition.test");

		Thread fst = new Thread(new SocketServerThread());  
		fst.start();  

		WifiManager wifiMgr = (WifiManager) getSystemService(WIFI_SERVICE);
		WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
		int ip = wifiInfo.getIpAddress();
		String ipAddress = Formatter.formatIpAddress(ip);
		((TextView) findViewById(R.id.textView2)).setText(ipAddress);

		final Button button_speak = (Button) findViewById(R.id.button1);
		button_speak.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// Perform action on click
				speak(((TextView) findViewById(R.id.textView2)).getText().toString());

			}
		});


		final Button button_speak1 = (Button) findViewById(R.id.button2);
		button_speak1.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {



				speech_recognizer.setRecognitionListener(new listener());

				speech_recognizer.startListening(intent);
				Log.d(TAG, "start recognition ");
				TextView tv = (TextView) findViewById(R.id.textView2);
				tv.setText("Start xxx");

			}
		});


		//        final Button button_start_recognition = (Button) findViewById(R.id.button2);
		//
		//        button_start_recognition.setOnClickListener(new ClickListener());
		//});


		final Button button_stop_recognition = (Button) findViewById(R.id.button3);

		button_stop_recognition.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				speech_recognizer.stopListening();
				TextView tv = (TextView) findViewById(R.id.textView2);
				tv.append("Stopping Recognition");

			}
		});

		final Button button_german_tts = (Button) findViewById(R.id.Button4);

		button_german_tts.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				tts.setLanguage(new Locale("GERMANY"));
				intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"de-DE");
			}
		});

		final Button button_english_tts = (Button) findViewById(R.id.Button02);

		button_english_tts.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				tts.setLanguage(new Locale("US"));
				intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"en-US");

			}
		});


		//findViewById(R.id.button2).setOnClickListener( this);
}


@Override
public boolean onCreateOptionsMenu(Menu menu) {
	// Inflate the menu; this adds items to the action bar if it is present.
	getMenuInflater().inflate(R.menu.main, menu);
	return true;
}

@Override
public boolean onOptionsItemSelected(MenuItem item) {
	// Handle action bar item clicks here. The action bar will
	// automatically handle clicks on the Home/Up button, so long
	// as you specify a parent activity in AndroidManifest.xml.
	int id = item.getItemId();
	if (id == R.id.action_settings) {
		return true;
	}
	return super.onOptionsItemSelected(item);
}




@Override
public void onInit(int code) {
	if (code==TextToSpeech.SUCCESS) {

		tts.setLanguage(Locale.getDefault());

	} else {
		tts = null;
		Toast.makeText(this, "Failed to initialize TTS engine.",

				Toast.LENGTH_SHORT).show();
	}

}


private class SocketServerThread extends Thread {

	static final int SocketServerPORT = 8051;
	ServerSocket serverSocket;
	int count = 0;
	String message;

	@Override
	public void run() {
		try {
			serverSocket = new ServerSocket(SocketServerPORT);
			MainActivity.this.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					Toast.makeText(MainActivity.this, "I'm waiting here: "  + serverSocket.getLocalPort(), Toast.LENGTH_SHORT).show();

					//info.setText();
				}
			});

			message = "";
			while (true) {

				Socket socket = serverSocket.accept();
				count++;
				//message += "#" + count + " from " + socket.getInetAddress() + ":" + socket.getPort() + "\n";
				//		     //message += socket.getInputStream();

				BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				message += in.readLine();

				MainActivity.this.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						//msg.setText(message);
						speak(message);
						message = "";
					}
				});


			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}


//	@Override
//	public void onClick(View arg0) {
//		// TODO Auto-generated method stub
//
//	}
//	
//	class ClickListener implements View.OnClickListener
//	{
//
//		@Override
//		public void onClick(View arg0) {
//
//			
//		}
//		 
//
//        		
//            
//		
//	}
class listener implements RecognitionListener          
{
	public void onReadyForSpeech(Bundle params)
	{
		Log.d(TAG, "onReadyForSpeech");
	}
	public void onBeginningOfSpeech()
	{
		Log.d(TAG, "onBeginningOfSpeech");
	}
	public void onRmsChanged(float rmsdB)
	{
		Log.d(TAG, "onRmsChanged");
	}
	public void onBufferReceived(byte[] buffer)
	{
		Log.d(TAG, "onBufferReceived");
	}
	public void onEndOfSpeech()
	{
		Log.d(TAG, "onEndofSpeech");
	}
	public void onError(int error)
	{
		Log.d(TAG,  "error " +  error);
		// mText.setText("error " + error);
	}
	public void onResults(Bundle results)                   
	{
		String str = new String();
		Log.d(TAG, "onResults " + results);
		ArrayList data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
		for (int i = 0; i < data.size(); i++)
		{
			Log.d(TAG, "result " + data.get(i));
			str += data.get(i);
		}
		TextView tv = (TextView) findViewById(R.id.textView2);
		tv.setText(data.toString());
		//								new SpeechRecognizedTask().execute("192.168.1.100", 8050, str);
		SpeechRecognizedTask task = new SpeechRecognizedTask();

		task.execute("192.168.1.100", "8050", data.get(0).toString().toUpperCase());
		//task.execute("141.26.95.176", "8050", data.get(0).toString().toUpperCase());
	}
	public void onPartialResults(Bundle partialResults)
	{
		Log.d(TAG, "onPartialResults");
	}
	public void onEvent(int eventType, Bundle params)
	{
		Log.d(TAG, "onEvent " + eventType);
	}
}


@Override
public void onClick(View arg0) {
	// TODO Auto-generated method stub

}
}
