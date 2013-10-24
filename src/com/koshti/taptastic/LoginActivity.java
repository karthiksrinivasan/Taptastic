package com.koshti.taptastic;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity 
{
	Button btnSignIn,btnSignUp;
	LoginDataBaseAdapter loginDataBaseAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
	     super.onCreate(savedInstanceState);
	     setContentView(R.layout.main);
	     
	     // create a instance of SQLite Database
	     loginDataBaseAdapter=new LoginDataBaseAdapter(this);
	     loginDataBaseAdapter=loginDataBaseAdapter.open();
	     
	     // Get The Reference Of Buttons
	     btnSignIn=(Button)findViewById(R.id.buttonSignIN);
	     btnSignUp=(Button)findViewById(R.id.buttonSignUP);
			
	    // Set OnClick Listener on SignUp button 
	    btnSignUp.setOnClickListener(new View.OnClickListener() {
		public void onClick(View v) {			
			// Create Intent for SignUpActivity  and Start The Activity
			Intent intentSignUP=new Intent(getApplicationContext(),SignUPActivity.class);
			startActivity(intentSignUP);
			}
		});
	}
	// Methods to handleClick Event of Sign In Button
	public void signIn(View V)
	   {
			final Dialog dialog = new Dialog(LoginActivity.this);
			dialog.setContentView(R.layout.login);
		    dialog.setTitle("Login");
	
		    // get the References of views
		    final  EditText editTextUserName=(EditText)dialog.findViewById(R.id.editTextUserNameToLogin);
		    final  EditText editTextPassword=(EditText)dialog.findViewById(R.id.editTextPasswordToLogin);
		    
			Button btnSignIn=(Button)dialog.findViewById(R.id.buttonSignIn);
				
			// Set On ClickListener
			btnSignIn.setOnClickListener(new View.OnClickListener() {
				
				public void onClick(View v) {
					// get The User name and Password
					String userName=editTextUserName.getText().toString();
					String password=editTextPassword.getText().toString();
					
					// Check in web service if the credentials are valid
					String strUrl = "http://cs-server.usc.edu:39547/examples/servlet/Taptastic?Username=" + userName + "&Password=" + password;
					validateCredentials checkCredentails = new validateCredentials(); 
					checkCredentails.execute(strUrl);					
					
					// Check in database if the credentials are valid
					// fetch the Password form database for respective user name
					String storedPassword=loginDataBaseAdapter.getSingleEntry(userName);
					
					// check if the Stored password matches with  Password entered by user
					if(password.equals(storedPassword))
					{
						Toast.makeText(LoginActivity.this, "Database :Congrats: Login Successfull", Toast.LENGTH_LONG).show();
						dialog.dismiss();
					}
					else
					{
						Toast.makeText(LoginActivity.this, "Database :User Name or Password does not match", Toast.LENGTH_LONG).show();
					}
				}
			});
			
			dialog.show();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	    // Close The Database
		loginDataBaseAdapter.close();
	}
	
	private class validateCredentials extends AsyncTask<String, Integer, String> {
		String replyFromServlet = null;

		@Override
		protected String doInBackground(String... url) {
			InputStream iStream = null;
			try {
				String strUrl = url[0];
				URL urlServlet = new URL(strUrl);

				HttpURLConnection urlConnection = (HttpURLConnection) urlServlet.openConnection();
				urlConnection.connect();
				iStream = urlConnection.getInputStream();

				BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
				StringBuffer sb = new StringBuffer();

				String line = "";
				while ((line = br.readLine()) != null) {
					sb.append(line);
				}

				replyFromServlet = sb.toString();
			} catch (Exception e) {
				Log.d("Background Task", e.toString());
			}
			return replyFromServlet;
		}

		@Override
		protected void onPostExecute(String authentication) {
			try {
				if(authentication == "passed"){
					Toast.makeText(LoginActivity.this, "Servlet: Congrats: Login Successfull", Toast.LENGTH_LONG).show();
				}else{
					Toast.makeText(LoginActivity.this, "Servlet: User Name or Password does not match", Toast.LENGTH_LONG).show();
				}
			} catch (Exception e) {
				Log.d("Background Task", e.toString());
			}
		}
	}
	
}
