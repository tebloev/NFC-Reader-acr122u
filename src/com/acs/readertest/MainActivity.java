package com.acs.readertest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.acs.smartcard.Reader;
import com.acs.smartcard.Reader.OnStateChangeListener;





public class MainActivity extends Activity {
	int count = 0;
	ArrayList<Item> data = new ArrayList<Item>();
	boolean mRead = false;
	SharedPreferences mShared;
	String name, date, marsh;
	public static final String APP_PREFERENCES_ALL = "All";
	public static final String APP_PREFERENCES_DB = "Database";
	public static final String APP_PREFERENCES_NUM = "Number";
	Reader mReader =  ReaderTestActivity.mReader;
	public static String s;
	private UsbManager mManager;
	private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
	private PendingIntent mPermissionIntent;
	private Spinner mReaderSpinner;
	private ArrayAdapter<String> mReaderAdapter;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.read_layout);
        textViewInit();
        LoadDataToList();
       // open();
        
        //new Task().execute("0FC53B009000", "marsh", "545555");
        //new Task().execute("0FC53B009000", "marsh", "545555");
        mShared = getSharedPreferences(APP_PREFERENCES_NUM, Context.MODE_PRIVATE);
		count = mShared.getInt("Curr", 0);   
		TextView tv1 = (TextView)findViewById(R.id.textView1);
		tv1.setText(getIntent().getStringExtra("route"));
        mReader.setOnStateChangeListener(new OnStateChangeListener() {

            @Override
            public void onStateChange(int slotNum, int prevState, int currState) {

                if (prevState < Reader.CARD_UNKNOWN
                        || prevState > Reader.CARD_SPECIFIC) {
                    prevState = Reader.CARD_UNKNOWN;
                }

                if (currState < Reader.CARD_UNKNOWN
                        || currState > Reader.CARD_SPECIFIC) {
                    currState = Reader.CARD_UNKNOWN;
                }

                // Create output string
               
                
                if (currState == Reader.CARD_PRESENT || currState == Reader.CARD_SPECIFIC)
                {

                    // Set power
                	PowerParams params = new PowerParams();
                    params.slotNum = slotNum;
                    params.action = Reader.CARD_WARM_RESET;
                    new PowerTask().execute(params);
                    
                    //Set protocol

                    int preferredProtocols = Reader.PROTOCOL_UNDEFINED;  
                    preferredProtocols |= Reader.PROTOCOL_T0;
                    preferredProtocols |= Reader.PROTOCOL_T1;
                    SetProtocolParams params1 = new SetProtocolParams();
                    params1.slotNum = slotNum;
                    params1.preferredProtocols = preferredProtocols;
                    new SetProtocolTask().execute(params1);
                    
                    if (slotNum != Spinner.INVALID_POSITION) {

                        // Set parameters
                        TransmitParams params2 = new TransmitParams();
                        params2.slotNum = slotNum;
                        params2.controlCode = -1;

                        // Transmit APDU
                        //logMsg("Slot " + slotNum + ": Transmitting APDU...");
                        new TransmitTask().execute(params2);
                    }
                    
                }
                
            }
        });

    }

    private void open()
    {
    	String deviceName = null;
        // Disable open button
       // mOpenButton.setEnabled(false);
    	try
    	{
    		deviceName = (String) mReaderAdapter.getItem(0);
    		Toast.makeText(MainActivity.this, deviceName, Toast.LENGTH_LONG).show();
    		}
    	catch(Exception e)
    	{
    		Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_LONG).show();
    	}
        if (deviceName != null) {

            // For each device
            for (UsbDevice device : mManager.getDeviceList().values()) {

                // If device name is found
                if (deviceName.equals(device.getDeviceName())) {

                    // Request permission
                    mManager.requestPermission(device,
                            mPermissionIntent);
                   

                    
                    
                    break;
                }
            }
            //Intent intent = new Intent(ReaderTestActivity.this, MainActivity.class);
			//intent.putExtra("route", getIntent().getStringExtra("route"));
            //startActivity(intent);
        }
    

    }
    
    public  class Task extends AsyncTask<String, Void, String>{
//		private ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
		private JSONObject json;
		
		@Override 
		protected void onPreExecute()
		{
//
//				progressDialog.setMessage("Список загружается...");
//		        progressDialog.show();
//		        progressDialog.setCanceledOnTouchOutside(true);	       
//		        progressDialog.setOnCancelListener(new OnCancelListener() {
//		            public void onCancel(DialogInterface arg0) {
//		                Task.this.cancel(true);
//		            }
//		        
//		        });
			
		}
		@Override
		protected String doInBackground(String... params) {
				
		        String result = new String("");
		        try{
		        	HttpParams httpParams = new BasicHttpParams();
	                HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);
	                HttpProtocolParams.setContentCharset(httpParams, "UTF-8");
	                HttpProtocolParams.setHttpElementCharset(httpParams, "UTF-8");

		          HttpClient client = 
		              new DefaultHttpClient(httpParams);
		          HttpPost post = 
		             new HttpPost("http://lgota15.tk/verify.php");
		          json = new JSONObject();          	            

		          
		          int portOfProxy = android.net.Proxy.getDefaultPort();
		          if( portOfProxy > 0 ){
		             HttpHost proxy = new HttpHost(android.net.Proxy.getDefaultHost(), portOfProxy );
		             client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);      
		          }
		          
		          json.put("cardID", params[0]);
		          json.put("routeID", params[1]);
		          json.put("currentDate", params[2]);
		          StringEntity ent = new StringEntity(json.toString(), "UTF-8");
		          post.setEntity(ent);
		          post.setHeader("Accept", "application/json");
		          post.setHeader("Content-type", "application/json");                  
		          HttpResponse response = client.execute(post);	          
		          BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(),"UTF-8"));
				  StringBuilder sb = new StringBuilder();
				  String line = null;
				  while ((line = reader.readLine()) != null) {			   
					   sb.append(line + System.getProperty("line.separator"));
				   }    
		          result = sb.toString();
		          //s = result;
		          
		          } catch (org.apache.http.client.ClientProtocolException e) {
		                  
		              result = "ClientProtocolException: " + e.getMessage();
		              
		          } catch (IOException e) {
		                 
		        	  result = "IOException: " + e.getMessage();
		          
		          } catch (Exception e) {
		                  
		              result = "Exception: " + e.getMessage();
		          }          
		        return result;    
		    
	}
		  @Override
		  protected void onProgressUpdate(Void... values) {
		   super.onProgressUpdate(values);
		  }
		  
		  @Override
		  protected void onPostExecute(String result) {
		   super.onPostExecute(result);	
	   
			try {
					//Toast.makeText(MainActivity.this, s, Toast.LENGTH_LONG).show();
					//Toast.makeText(MainActivity.this, s.length(), Toast.LENGTH_LONG).show();
					
					//Toast.makeText(MainActivity.this, result, Toast.LENGTH_LONG).show();
					JSONObject js = new JSONObject(result);
					String fio = js.getString("userFio");
					boolean valid = js.getBoolean("valid");
					String status = js.getString("status");
					TextView tv5 = (TextView)findViewById(R.id.textView5);
					if (valid)
					{
						ImageView iv = (ImageView)findViewById(R.id.imageView1);
			    		iv.setImageResource(R.drawable.green);
			    		tv5.setTextColor(Color.GREEN);
					}
					else
					{
						ImageView iv = (ImageView)findViewById(R.id.imageView1);
			    		iv.setImageResource(R.drawable.red);
			    		tv5.setTextColor(Color.RED);
					}
					TextView tv = (TextView)findViewById(R.id.textView2);
	        		TextView tv1 = (TextView)findViewById(R.id.textView1);
	        		tv1.setText(getIntent().getStringExtra("route"));;
	        		TextView tv3 = (TextView)findViewById(R.id.textView3);
	        		
	        		
	        		tv5.setText("Статус: " + status);
					tv.setText(fio);
	        		data.add(new Item(fio, tv1.getText().toString(), tv3.getText().toString()));
	        		LoadDataToList();
					//Intent intent = new Intent(Login.this, ReaderTestActivity.class);
					//startActivity(intent);
				}

			 catch (JSONException e) {				 		
				 try
				 {
					 JSONObject js = new JSONObject(result);
					 String msg = js.getString("message");
					 
					 //Toast.makeText(Login.this, msg, Toast.LENGTH_LONG).show();
				 }
					 catch (JSONException e1)
					 {
						 e1.printStackTrace();
						                     
					 }
			 	}	

		}	
	}
    
    public class PowerParams {

        public int slotNum;
        public int action;
    }
    
    private class PowerResult {

        public byte[] atr;
        public Exception e;
    }

    public class PowerTask extends AsyncTask<PowerParams, Void, PowerResult> {

        @Override
        protected PowerResult doInBackground(PowerParams... params) {

            PowerResult result = new PowerResult();

            try {

                result.atr = mReader.power(params[0].slotNum, params[0].action);

            } catch (Exception e) {

                result.e = e;
            }

            return result;
        }

        @Override
        protected void onPostExecute(PowerResult result) {

        }
    }

    public class SetProtocolParams {

        public int slotNum;
        public int preferredProtocols;
    }

    private class SetProtocolResult {

        public int activeProtocol;
        public Exception e;
    }

    public class SetProtocolTask extends
            AsyncTask<SetProtocolParams, Void, SetProtocolResult> {

        @Override
        protected SetProtocolResult doInBackground(SetProtocolParams... params) {

            SetProtocolResult result = new SetProtocolResult();

            try {

                result.activeProtocol = mReader.setProtocol(params[0].slotNum,
                        params[0].preferredProtocols);

            } catch (Exception e) {

                result.e = e;
            }

            return result;
        }

        @Override
        protected void onPostExecute(SetProtocolResult result) {

            if (result.e != null) {                

            } else {

                String activeProtocolString = "Active Protocol: ";

                switch (result.activeProtocol) {

                case Reader.PROTOCOL_T0:
                    activeProtocolString += "T=0";
                    break;

                case Reader.PROTOCOL_T1:
                    activeProtocolString += "T=1";
                    break;

                default:
                    activeProtocolString += "Unknown";
                    break;
                }

                // Show active protocol
                
            }
        }
    }

    public class TransmitParams {

        public int slotNum;
        public int controlCode;
        public String commandString;
    }

    private class TransmitProgress {

        public int controlCode;
        public byte[] command;
        public int commandLength;
        public byte[] response;
        public int responseLength;
        public Exception e;
    }

    public class TransmitTask extends
            AsyncTask<TransmitParams, TransmitProgress, Void> {

        @Override
        protected Void doInBackground(TransmitParams... params) {

            TransmitProgress progress = new TransmitProgress();

           // byte[] command = { 0x00, (byte) 0x84, 0x00, 0x00, 0x08 };
            byte[] response = new byte[300];
            int responseLength;
            int foundIndex;
            int startIndex = 0;    
            
            params[0].commandString = "";
            try
            {
            	
	            do {
	                foundIndex = params[0].commandString.indexOf('\n', startIndex);
	                byte[] command = {(byte) 0xff, (byte) 0xca, 0x00, 0x00, 0x00 };
	                
	                // Set next start index
	                startIndex = foundIndex + 1;
	
	                progress.controlCode = params[0].controlCode;
	                try {
	 
	                    if (params[0].controlCode < 0) {
	
	                        // Transmit APDU
	                        responseLength = mReader.transmit(params[0].slotNum,
	                                command, command.length, response,
	                                response.length);
	                      
	            
	                    } else {
	
	                        // Transmit control command
	                        responseLength = mReader.control(params[0].slotNum,
	                                params[0].controlCode, command, command.length,
	                                response, response.length);
	                    }
						logBuffer(response, responseLength);
	                    progress.command = command;
	                    progress.commandLength = command.length;
	                    progress.response = response;
	                    progress.responseLength = responseLength;
	                    progress.e = null;
	                    mRead = true;
	                   
					
	                } catch (Exception e) {
	                    progress.command = null;
	                    progress.commandLength = 0;
	                    progress.response = null;
	                    progress.responseLength = 0;
	                    progress.e = e;
	                    s = e.toString(); 
	                    mRead = false;
	                    
	                }
	
	               
	
	            } while (foundIndex >= 0);
	            }
            catch(Exception e1)
            {
            	s = e1.toString(); 
            	mRead = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
        	if (mRead)
        	{
        		/*
        		TextView tv1 = (TextView)findViewById(R.id.textView1);
        		TextView tv2 = (TextView)findViewById(R.id.textView2);
        		     		
        		mShared = getSharedPreferences(APP_PREFERENCES_DB, Context.MODE_PRIVATE);
        		Editor edit = mShared.edit();*/
        		long msTime = System.currentTimeMillis();
        		//edit.putString(s, "Кулиев Алан Алексеевич|Маршрут №5");
        		//edit.apply();
        		
        		/*if (ReadClientFromBase(s))
        		{
	        		
	        		//tv1.setText("Маршрут №5");
	        		tv1.setText(marsh);
	        		
	        		//tv2.setText("Кулиев Алан Алексеевич");
	        		tv2.setText(name);
        		}*/
        		TextView tv = (TextView)findViewById(R.id.textView4);
        		tv.setText("Card ID = "+  s);   
        		TextView tv3 = (TextView)findViewById(R.id.textView3);        		
        		Date date = new Date(msTime);
        		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yy HH:mm");
        		String dateFormatted = formatter.format(date);
        		tv3.setText(dateFormatted);
        		//mShared = getSharedPreferences(APP_PREFERENCES_NUM, Context.MODE_PRIVATE);
        		//count = mShared.getInt("Curr", 0);      		
        		//edit = mShared.edit();
        		//edit.putInt("Curr", count + 1);
        		//edit.apply();  		        		
        		//mShared = getSharedPreferences(APP_PREFERENCES_ALL, Context.MODE_PRIVATE);
        		//edit.apply();        		
        		//edit.putString(String.valueOf(count) , tv2.getText().toString() + "|" + tv1.getText().toString() + "$" + String.valueOf(msTime));    	        		
        		//clientInBase(s);
        		
        		new Task().execute(s, "route2", String.valueOf(msTime));
                // Show active protocol
        	}
        	else
        	{
        		Toast.makeText(MainActivity.this, "Не удалось считать данные, приложите карточку повторно.", Toast.LENGTH_LONG).show();
        		textViewInit();        		
        	}
                
            }
  
   }

    private void ParseRecords(String input)
    {
		try
		{
	    	String s1 = "";
			for (int i = 0; i < input.length(); i++)
			{
				if(input.charAt(i) != '$')
				{
	    			if(input.charAt(i) != '|')
	    			{    				
	    				s1 = s1 +  input.charAt(i);    				
	    			}
	    			else
	    			{
	    				name = s1;
	    				s1 = "";		
	    			}
	    		}
	    		marsh = s1;   
	    		s1 = "";	    		
			}
			date = s1;
			data.add(new Item(name, marsh, date));
	    	}
    	catch(Exception e)
    	{
    		//Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_LONG).show();
    		Toast.makeText(MainActivity.this, input , Toast.LENGTH_LONG).show();
    		//Toast.makeText(MainActivity.this, name + "  " + marsh + "  " + date , Toast.LENGTH_LONG).show();
    	}
    }
    
    private void LoadDataToList()
    {
    	try{
	    	mShared = getSharedPreferences(APP_PREFERENCES_NUM, Context.MODE_PRIVATE);
	    	int x = mShared.getInt("Curr", 0);
	    	mShared = getSharedPreferences(APP_PREFERENCES_ALL, Context.MODE_PRIVATE);
	    	if (count != 0)
	    	{
	    		/*for (int i = 1; i<=x; i++)
	    		{
	    			String s = mShared.getString(String.valueOf(i), null);
	    			ParseRecords(s);
	    		}*/
	    		ListView lv = (ListView)findViewById(R.id.listView1);
	    		lv.setAdapter(new Adapter(MainActivity.this, data));
	    	}
    	}
    	catch(Exception e)
    	{
    		//Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_LONG).show();
    	}
    }

    private boolean ReadClientFromBase(String input)
    {
		mShared = getSharedPreferences(APP_PREFERENCES_DB, Context.MODE_PRIVATE);
		String s = mShared.getString(input, null);
		if (s != null)
		{
			ParseFromPref(s);
			return true;
		}
		return false;
    }
    
    private void ParseFromPref(String input)
    {
    	//Toast.makeText(MainActivity.this, input, Toast.LENGTH_SHORT).show();
    		String s1 = "";
    		for (int i = 0; i < input.length(); i++)
    		{
    			if(input.charAt(i) != '|')
    			{    				
    				s1 = s1 +  input.charAt(i);
    				
    			}
    			else
    			{
    				name = s1;
    				s1 = "";		
    			}
    		}
    		marsh = s1;    	
    }
    
    private void textViewInit()
    {
    	TextView tv = (TextView)findViewById(R.id.textView1);
    	tv.setText("Номер маршрута");	        		
    	TextView tv1 = (TextView)findViewById(R.id.textView1);
		tv1.setText(getIntent().getStringExtra("route"));;
    	tv = (TextView)findViewById(R.id.textView2);
    	tv.setText("ФИО");
    	tv = (TextView)findViewById(R.id.textView3);
    	tv.setText("Дата");
    	tv = (TextView)findViewById(R.id.textView4);
    	tv.setText("Card ID");
    }
    
    private void clientInBase(String input)
    {
		mShared = getSharedPreferences(APP_PREFERENCES_DB, Context.MODE_PRIVATE);
		String s = mShared.getString(input, null);
		if (s != null)
		{
			ImageView iv = (ImageView)findViewById(R.id.imageView1);
    		iv.setImageResource(R.drawable.green);
		}
    }
    
    private void logBuffer(byte[] buffer, int bufferLength) {

        String bufferString = "";

        for (int i = 0; i < bufferLength; i++) {

            String hexChar = Integer.toHexString(buffer[i] & 0xFF);
            if (hexChar.length() == 1) {
                hexChar = "0" + hexChar;
            }

            if (i % 16 == 0) {

                if (bufferString != "") {

                    s = bufferString.replace(" ", "");
                    bufferString = "";
                }
            }

            bufferString += hexChar.toUpperCase() + " ";
        }

        if (bufferString != "") {
        	s = bufferString.replace(" ", "");;
        }
    }

}
