package com.example.texttospeechexample;

import android.nfc.Tag;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

	private TextToSpeech mTTS;
	private EditText mEditText;
	private SeekBar mSeekBarPitch;
	private SeekBar mSeekBarSpeed;
	private Button btnSpeak;

	private Locale ruLocal;
	private String TAG = "MainActivity TTS";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		btnSpeak = findViewById(R.id.btn_speak);
		mTTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
			@Override
			public void onInit(int status) {
				if ( status == TextToSpeech.SUCCESS ) {
					ruLocal = Locale.forLanguageTag("ru");
					int result = mTTS.setLanguage(ruLocal);
					if ( result == TextToSpeech.LANG_MISSING_DATA ||
							result == TextToSpeech.LANG_NOT_SUPPORTED ) {
						Log.e(TAG, "onInit: Language not supported");
					}
					else {
						btnSpeak.setEnabled(true);
					}
				}
				else {
					Log.e(TAG, "onInit: Initialization failed");
				}
			}
		});

		mEditText = findViewById(R.id.text_to_speech);
		mSeekBarPitch = findViewById(R.id.seek_bar_pitch);
		mSeekBarSpeed = findViewById(R.id.seek_bar_speed);

		btnSpeak.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				speak();
			}
		});
	}

	@Override
	protected void onDestroy() {
		if(mTTS!=null){
			mTTS.stop();
			mTTS.shutdown();
		}

		super.onDestroy();

	}

	private void speak() {
		String text = mEditText.getText().toString();
		float pitch = (float)mSeekBarPitch.getProgress() / 50;
		if ( pitch < 0.1 ) pitch = 0.1f;

		float speed = (float)mSeekBarSpeed.getProgress() / 50;
		if ( speed < 0.1 ) speed = 0.1f;

		mTTS.setPitch(pitch);
		mTTS.setSpeechRate(speed);

		mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null);

	}
}
