package com.example.texttospeechexample;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 * Несколько вариантов перевода текста в речь, но не понятно как остановить и возобновить с опред поизиции
 */
public class SpeekBaseActivity extends AppCompatActivity {
	private TextToSpeech mTTS;
	private EditText speachText;
	private Locale ruLocal;
	private String TAG = "MainActivity TTS";
	private int currPosition = 0;
	private String[] arSentences;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_speek_base);
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		final FloatingActionButton fabStart = findViewById(R.id.fabStart);
		final FloatingActionButton fabStop = findViewById(R.id.fabStop);
		final FloatingActionButton fabClear = findViewById(R.id.fabClear);
		speachText = findViewById(R.id.speachText);

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
						fabStart.setEnabled(true);
					}
				}
				else {
					Log.e(TAG, "onInit: Initialization failed");
				}
			}
		});


		//
		fabStart.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				String charSequence = speachText.getText().toString();
				//speak(charSequence,0);
				speakSpeech(charSequence);
				Snackbar.make(view, "Воспроизведение", Snackbar.LENGTH_LONG)
						.setAction("Action", null).show();

			}
		});

		fabStop.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				stop();
				Snackbar.make(view, "Остановка", Snackbar.LENGTH_LONG)
						.setAction("Action", null).show();

			}
		});

		fabClear.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				stop();
				speachText.setText("");
			}
		});
	}

	/*
	Разбить на предложения и вопроизвести 750мс - остановка
	 */
	public void speakSpeech(String speech) {

		HashMap<String, String> myHash = new HashMap<String, String>();

		myHash.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "done");

		String[] splitspeech = speech.split("\\.");

		for (int i = 0; i < splitspeech.length; i++) {

			if (i == 0) { // Use for the first splited text to flush on audio stream

				mTTS.speak(splitspeech[i].toString().trim(),TextToSpeech.QUEUE_FLUSH, myHash);

			} else { // add the new test on previous then play the TTS

				mTTS.speak(splitspeech[i].toString().trim(), TextToSpeech.QUEUE_ADD,myHash);
			}

			mTTS.playSilence(750, TextToSpeech.QUEUE_ADD, null);
			Toast.makeText(this, "Itterate - "+ i, Toast.LENGTH_SHORT).show();
		}
	}



	/**
	 * воспроизвести текст начиная с определенной позиции
	 * @param charSequence текст для воспроизведения
	 * @param position c какого символа начать
	 */
	private  void speak(String charSequence, Integer position){
		//String charSequence = speachText.getText().toString();

		//int position ;
		// колич символов
		int sizeOfChar= charSequence.length();
		// 	текст обрезанный начиная с определенной позиции
		String testStri= charSequence.substring(position,sizeOfChar);

		int next = 20;
		int pos =0;

		// разбивка по 20 символов
		while(true) {
			String temp="";
			Log.e("in loop", "" + pos);

			try {

				temp = testStri.substring(pos, next);

				HashMap<String, String> params = new HashMap<String, String>();
				params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, temp);
				mTTS.speak(temp, TextToSpeech.QUEUE_ADD, params);

				pos = pos + 20;
				next = next + 20;

			} catch (Exception e) {
				temp = testStri.substring(pos, testStri.length());
				mTTS.speak(temp, TextToSpeech.QUEUE_ADD, null);

				break;

			}
		}
	}


	/**
	 * Простое воспроизведение
	 */
	private void speak0() {
		if ( mTTS.isSpeaking() )
			return;

		String text = speachText.getText().toString();
		getSentences( text);
		//float pitch = (float)mSeekBarPitch.getProgress() / 50;
		//if ( pitch < 0.1 ) pitch = 0.1f;

		//float speed = (float)mSeekBarSpeed.getProgress() / 50;
		//if ( speed < 0.1 ) speed = 0.1f;
		Date t1 = new Date();
		long t11 =  t1.getTime();

		Log.d(TAG, "speak: start Time - " + t11);

		mTTS.setPitch(1f);
		mTTS.setSpeechRate(0.5f);//speed

		//mTTS.speak(text, TextToSpeech.QUEUE_ADD, null);

		for ( int i=currPosition; i<arSentences.length; i++ ){

			Log.d(TAG, "speak: step begin - " +i);
			mTTS.speak(arSentences[i], TextToSpeech.QUEUE_ADD, null);

			if(!mTTS.isSpeaking())
				break;
			Log.d(TAG, "speak: step end - " +i);
			currPosition = i;

			//mTTS.
		}/**/


		// надо разбить на предложения и воспроизводить последовательно,
		// если остановились, то воспроизводить с места остановки

		 t1 = new Date();
		 t11 =  t1.getTime();
		Log.d(TAG, "speak: end Time - " + t11);
	}

	private void getSentences(String txt){
		arSentences = txt.split("\\.");
	}

	private void stop() {
		if ( mTTS.isSpeaking() )
			mTTS.stop();

		// запомнить на каком месте остановились

		// если нажимать повторно, то стерать текст и начинать заново
	}

	private void clear() {
		currPosition=0;
		if ( mTTS.isSpeaking() )
			mTTS.stop();
		if ( mTTS != null ) {
			mTTS.stop();
		}
	}

	@Override
	protected void onDestroy() {
		if ( mTTS != null ) {
			mTTS.stop();
			mTTS.shutdown();
		}

		super.onDestroy();

	}
}
