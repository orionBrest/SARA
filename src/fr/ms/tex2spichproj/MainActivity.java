package fr.ms.tex2spichproj;

import java.util.ArrayList;
import java.util.Date;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnSeekBarChangeListener{

	// variables declarations
	
	protected static final int RESULT_SPEECH = 1;
	
	private TextView textViewSpeed = null;
	private TextView textViewBearing = null;
	private TextView textViewAuto = null;	
	private Button buttonSpeed = null;
	private Button buttonBearing = null;
	private ImageButton buttonReco = null;
	
	private CheckBox speedAutoCheckBox = null;
	@SuppressWarnings("unused")
	private CheckBox bearingAutoCheckBox = null;
	
	private SeekBar speedBar = null;
	@SuppressWarnings("unused")
	private SeekBar bearingBar = null;
	private SeekBar timeBar = null; 
	
	private TextToSpeech tts = null;
	
	private LocationManager lm = null;
	private LocationListener ll = null;
	private String bearing = "pas de satellite";
	private String speed = "pas de satellite";
	private double speedAuto = 0;
	private double speedLastAuto = 0;
	private double speedTreshold = 0.1; 
	private long speedTimeTreshold = 5;
	private Date speedNow = null;
	private Date speedBefore = null;
	
	//positions : 
	@SuppressWarnings("unused")
	private String latitude = "pas de satellite";
	@SuppressWarnings("unused")
	private String longitude = "pas de satellite";
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
    //TextView creation
    textViewSpeed = new TextView(this);
    textViewSpeed = (TextView) findViewById(R.id.speedView);
    textViewBearing = new TextView(this);
    textViewBearing = (TextView) findViewById(R.id.bearing);
    textViewAuto = new TextView(this);
    textViewAuto = (TextView) findViewById(R.id.speak);
    textViewAuto.setText("mode auto par défault");
    
    //CheckBox
    speedAutoCheckBox = (CheckBox) findViewById(R.id.speedAutoCheckBox);
    bearingAutoCheckBox = (CheckBox) findViewById(R.id.bearingAutoCheckBox);
        
    //SpeedBar
    speedBar = (SeekBar) findViewById(R.id.seekBarSpeed);
    speedBar.setOnSeekBarChangeListener(this);
    speedBar.setContentDescription("Réglage seuil vitesse auto");
    
    //TimeBar 
    timeBar = (SeekBar) findViewById(R.id.seekBarTime);
    timeBar.setOnSeekBarChangeListener(this);
    timeBar.setContentDescription("Réglage seuil temps vitesse auto");
    
    // edit text creation
    //editText = new EditText(this);
    //editText = (EditText) findViewById(R.id.editTextInvitation);
	  
	//location manager creation
	lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	ll = new MyLocationListener();		
	//loc = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
	lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, ll);
   
	//dates creation
//	now = new Date();
//	Log.i("new Date() : now.getTime", " = " + now.getTime());
	speedBefore = new Date();
	
	//2point creation
	/**
	point1 = lm.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
	point2 = lm.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);	
	point1.setLatitude(48.1);
    point1.setLongitude(-4.1);
    point2.setLatitude(48.9);
    point2.setLongitude(-4.9);
    int dist1to2 = (int)(point1.distanceTo(point2))/1000  ;
    test = (String.valueOf(dist1to2));
    textView5.setText(test);
    **/
	
	//OnInitListener Creation
	OnInitListener onInitListener = new OnInitListener() {
		@Override
		public void onInit(int status) {
			//Toast.makeText(getApplicationContext(), (Integer.valueOf(status)).toString(), Toast.LENGTH_SHORT).show();
		}
	};
	
    // tts creation
	tts = new TextToSpeech(this, onInitListener);
	
	// button creation
    buttonSpeed= new Button(this);
    buttonSpeed = (Button) findViewById(R.id.buttonSpeed);
	buttonBearing = new Button(this);
    buttonBearing = (Button) findViewById(R.id.buttonBearing);
	buttonReco= new ImageButton(this);
    buttonReco = (ImageButton) findViewById(R.id.buttonSpeak);

    // OnClickListener creation
    View.OnClickListener onclickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v== buttonSpeed){
				tts.speak("vitesse : " + speed, TextToSpeech.QUEUE_FLUSH, null);
			}
			if (v== buttonBearing){
				tts.speak("cap : " + bearing, TextToSpeech.QUEUE_FLUSH, null);
			}
			if (v== buttonReco){
				 Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
	             intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "fr-FR");	 
	                try {
	                    startActivityForResult(intent, RESULT_SPEECH);
	                } catch (ActivityNotFoundException a) {
	                    Toast.makeText(getApplicationContext(),"Pas de reconnaissance",Toast.LENGTH_SHORT).show();
	                }//end of catch
	            }// end of if button5
	        }// end of onclick		
		}; //end of new View.LocationListener	
	
	// button activation
	buttonSpeed.setOnClickListener(onclickListener);
	buttonBearing.setOnClickListener(onclickListener);
	buttonReco.setOnClickListener(onclickListener);
	
    }//end of oncreate

	

	
  @Override
  protected void onResume() {
    super.onResume();
    lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, ll);
    //tts.speak("resume", tts.QUEUE_FLUSH, null);
  }

  /* Remove the locationlistener updates when Activity is paused */
  @Override
  protected void onPause() {
    super.onPause();
    lm.removeUpdates(ll);
  }
  
  /* Remove the locationlistener updates when Activity is stopped */
  @Override
  protected void onStop() {
    super.onStop();
	tts.shutdown();
  }
  
	@Override
	protected void onDestroy() {
		super.onDestroy();
		lm.removeUpdates(ll);
		tts.shutdown();
	}
	
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        switch (requestCode) {
        	case RESULT_SPEECH: {
        		if (resultCode == RESULT_OK && null != data) {
                ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                	if ( (text.get(0).equals("vitesse"))){
                		tts.speak("vitesse : " + speed + "noeuds", TextToSpeech.QUEUE_FLUSH, null);
                	}
                	else if ( (text.get(0).equals("cap"))){
                		tts.speak("cap : " + bearing, TextToSpeech.QUEUE_FLUSH, null);
                	}
                	else {
                		Toast.makeText(getApplicationContext(),text.get(0),Toast.LENGTH_SHORT).show();
                	}
        		}
            break;
        	}// end of case
        }//end of switch 
    }//end of on Activity result 
	
	//method to round 1 decimal
	//public double arrondiLat(double val) {return (Math.floor(val*1000))/1000;}
	//public double arrondiLong(double val) {return (Math.floor(val*100))/100;}
	public double arrondiSpeed(double val) {return (Math.floor(val*10))/10;}
	//public double arrondiBearing(double val) {return (Math.floor(val*100))/100;}

    public class MyLocationListener implements LocationListener {

		@Override
		public void onLocationChanged(Location loc) {
			
			latitude = String.valueOf(loc.getLatitude());
			longitude = String.valueOf(loc.getLongitude());			
			speed = String.valueOf(arrondiSpeed(loc.getSpeed()*(1.94)));
			bearing = String.valueOf((int)loc.getBearing());
			
			if (speedAutoCheckBox.isChecked()){
				speedAuto = arrondiSpeed(loc.getSpeed()*(1.94));
				speedNow = new Date();
				if 	((( speedAuto < speedLastAuto - speedTreshold )|| ( speedAuto > speedLastAuto + speedTreshold ))
				 &&	((speedNow.getTime() - speedBefore.getTime()) > speedTimeTreshold*1000)){
				tts.speak("vitesse : " + speed + "noeuds", TextToSpeech.QUEUE_FLUSH, null);
				speedLastAuto = speedAuto;
				speedBefore = new Date();
				}
			}//end of if speedAutoCheck...
			
			//displaying value
			textViewSpeed.setText(speed);
			textViewBearing.setText(bearing);    
		}

		@Override
		public void onProviderDisabled(String provider) {
			Toast.makeText( getApplicationContext(),"Gps Disabled",Toast.LENGTH_SHORT).show();	
		}

		@Override
		public void onProviderEnabled(String provider) {
			Toast.makeText( getApplicationContext(),"Gps Enabled",Toast.LENGTH_SHORT).show();	
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			//Log.i("LocationListener","onStatusChanged");
		}
    	
    } //end of MyLocationListener

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		if (seekBar.equals(speedBar)){
			speedTreshold = (double) progress/10;
			textViewAuto.setText("Seuil vitesse auto : " + Double.valueOf(speedTreshold).toString());
			seekBar.setContentDescription(Double.valueOf(speedTreshold).toString() + "noeuds");
		}
		else if (seekBar.equals(timeBar)){
			speedTimeTreshold = progress;
			textViewAuto.setText("Seuil de temps : " + Double.valueOf(speedTimeTreshold).toString());
			seekBar.setContentDescription(Double.valueOf((int)speedTimeTreshold).toString() + "secondes");
		}
		
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		//seekBar.setContentDescription("Seuil vitesse auto : " + Double.valueOf(speedTreshold).toString());	
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		//textView5.setContentDescription("On stop Seuil vitesse auto : " + Double.valueOf(speedTreshold).toString());
		//tts.speak(" Le Seuil de la vitesse auto est réglé à : " + Double.valueOf(speedTreshold).toString(), TextToSpeech.QUEUE_ADD, null);
	}
 
}//end of Activity
