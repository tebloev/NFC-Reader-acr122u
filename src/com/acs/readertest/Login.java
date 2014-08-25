package com.acs.readertest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

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
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        Button btn = (Button)findViewById(R.id.button1);
        btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				EditText et1 = (EditText)findViewById(R.id.editText1);
				EditText et2 = (EditText)findViewById(R.id.editText2);
				String login = et1.getText().toString();
				String pass = et2.getText().toString();
				new Task().execute(login, pass);				
			}
		});
    }

    public  class Task extends AsyncTask<String, Void, String>{

		private JSONObject json;
		
		@Override 
		protected void onPreExecute()
		{


			
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
		             new HttpPost("http://lgota15.tk/login.php");
		          json = new JSONObject();          	            

		          
		          int portOfProxy = android.net.Proxy.getDefaultPort();
		          if( portOfProxy > 0 ){
		             HttpHost proxy = new HttpHost(android.net.Proxy.getDefaultHost(), portOfProxy );
		             client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);      
		          }
		          
		          json.put("login", params[0]);
		          json.put("pass", params[1]);
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
					JSONObject js = new JSONObject(result);
					String fio = js.getString("driverFio");
					String route = js.getString("routeNumber");
					//SharedPreferences mShared =  getSharedPreferences(Defa, Context.MODE_PRIVATE);
					Intent intent = new Intent(Login.this, MainActivity.class);
					intent.putExtra("fio", fio);
					intent.putExtra("route", route);
					startActivity(intent);
				}

			 catch (JSONException e) {				 		
				 try
				 {
					 JSONObject js = new JSONObject(result);
					 String msg = js.getString("message");
					 Toast.makeText(Login.this, msg, Toast.LENGTH_LONG).show();
				 }
					 catch (JSONException e1)
					 {
						 e1.printStackTrace();
						                     
					 }
			 	}	
//            this.progressDialog.dismiss();	

		}	
	}
}
