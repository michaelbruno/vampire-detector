package com.apollonarius.vampiredetector;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

/**
 *
 * @author Michael Bruno
 *
 */

public class MainActivity extends Activity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

	}

    @Override
    public void onResume() {
        super.onResume();
        ApplicationControl.getInstance();
    }

    public void startButton(View view){
		Bundle b = new Bundle();
    	Intent intent = new Intent(this,VisionActivity.class);
    	intent.putExtras(b);

    	startActivity(intent);
    }

	public void rateButton(View view){
		Uri uri = Uri.parse("market://details?id=" + getPackageName());
		Intent market = new Intent(Intent.ACTION_VIEW, uri);
		market.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
		try {
			startActivity(market);
		} catch (ActivityNotFoundException e) {
			startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
		}
	}
}