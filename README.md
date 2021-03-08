# NetListener
An android library to implement listener for network and internet connection.

# Demo app photos and video

https://user-images.githubusercontent.com/70692539/110327782-b939a580-801a-11eb-9337-c2fd46b51aef.mov

<img width="331" alt="Screenshot 2021-03-08 at 14 00 35" src="https://user-images.githubusercontent.com/70692539/110327802-be96f000-801a-11eb-8e0e-57defd3cc814.png">
<img width="330" alt="Screenshot 2021-03-08 at 14 00 00" src="https://user-images.githubusercontent.com/70692539/110327813-c22a7700-801a-11eb-9489-39c0a57f41d5.png">
<img width="329" alt="Screenshot 2021-03-08 at 13 59 32" src="https://user-images.githubusercontent.com/70692539/110327821-c48cd100-801a-11eb-8003-8b904cd3795d.png">

# Implementation

Step 1. Add the JitPack repository to your build file

Add it in your root build.gradle at the end of repositories:

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
Step 2. Add the dependency

	dependencies {
	        implementation 'com.github.hansaray:NetListener:0.1.0'
	}

# Usage

Listener usage:

Step1: Ideally initialize observer in onResume or onStart methods.

        NetListener.observer(MainActivity.this)
                .setSnackBarEnabled(true)
                .setCallBack(this)
                .setSensitivity(4)
                .
                .
                .
                .build();
                
Step2: Unregister the NetListener onDestroy method.

        NetListener.unregister(MainActivity.this);
        
Step3: Implement the interface for listener.

       public class MainActivity extends AppCompatActivity implements InternetConnectionListener
       
Step4: Implement override methods.

       @Override
        public void onConnected(int source) {
        
       }

       @Override
       public View onDisconnected() {
           return layout_you_want_to_show_the_snackbar_on;
       }
       
Checking the connection once:

       boolean connected = NetListener.isInternetConnected(getApplicationContext());
       
# Settings and methods
       .setSnackBarEnabled(true) //While instant listening, you can enable/disable snackbar, by default it's enabled.
       .setBackground(snackbar_shape) //You can set snackbar background, by default it's red,rectangle with rounded corners.
       .setTextColor(white) //You can set text color, by default it's white.
       .setIcon(ic_no_internet) //You can set icon for snackbar when there is no internet.
       .setLogsEnabled(false) //You can enable/disable logs for testing, by default it's false.
       .setSnackBarCancelable(false) //You can set snackbar cancelable or not, by default it's false.
       .setSnackBarDuration(Snackbar.LENGTH_INDEFINITE) //You can set snackbar duration, by default it's indefinite.
       .setCallBack(this) //Setting call back activity which implemented the interface.
       .setSensitivity(4) //Setting ping sending sensitivity to determine internet connection.
       .setMessage(R.string.no_internet) //You can set message to show on snackbar when there is no internet.
       
       NetListener.isInternetConnected(context) //Returns internet connection status at the moment
       NetListener.isActive(context) //Returns status of NetListener if it's active or not
       NetListener.getConnectionType(context) //Returns internet connection type at the moment. Wifi,Cellular or Vpn.
