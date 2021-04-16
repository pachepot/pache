package com.example.pache;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    protected static int number;
    protected static String[][] info = new String[200][2]; // first index : number, second index : { [0]: name, [1]: price }

    public ToggleButton call;

    Tool tool = new Tool();

    Intent intent;
    SpeechRecognizer recognizer;

    private void hideNavigationBar() {
        int newUiOptions = getWindow().getDecorView().getSystemUiVisibility();
        newUiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        newUiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
        newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        getWindow().getDecorView().setSystemUiVisibility(newUiOptions);
    }

    private RecognitionListener listener = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle params) { }

        @Override
        public void onBeginningOfSpeech() { }

        @Override
        public void onRmsChanged(float rmsdB) { }

        @Override
        public void onBufferReceived(byte[] buffer) { }

        @Override
        public void onEndOfSpeech() { }

        @Override
        public void onError(int error) { recognizer.startListening(intent); } // Repeat

        @Override
        public void onPartialResults(Bundle partialResults) { }

        @Override
        public void onEvent(int eventType, Bundle params) { }

        @Override
        public void onResults(Bundle results) {

            String key;
            key = SpeechRecognizer.RESULTS_RECOGNITION;
            ArrayList<String> mResult = results.getStringArrayList(key);
            assert mResult != null;
            String[] rs = new String[mResult.size()];
            mResult.toArray(rs);

            TextView t = findViewById(R.id.test2);
            t.setText(rs[0]);

            tts.speak(tool.string_Parsing(rs[0], call.isChecked()), TextToSpeech.QUEUE_FLUSH, null);

            recognizer.startListening(intent); // Repeat

        }
    };

    TextToSpeech tts;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 5);

        hideNavigationBar();

        call = findViewById(R.id.call_pache);

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!call.isChecked())
                    call.setBackgroundDrawable(getDrawable(R.drawable.pache_ic_off));

                else
                    call.setBackgroundDrawable(getDrawable(R.drawable.pache_ic_on));

            }
        }); // toggle button of call pache

        tool.bithumb();

        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");

        recognizer = SpeechRecognizer.createSpeechRecognizer(this);
        recognizer.setRecognitionListener(listener);


        tts = new TextToSpeech(getBaseContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status!=android.speech.tts.TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.KOREAN);
                }
            }
        });

        tts.setPitch((float) 1.0);
        tts.setSpeechRate((float) 1.0);


        recognizer.startListening(intent);

    }

    @Override
    protected void onDestroy () {
        super.onDestroy();

        number = 0;
        tts.stop();
        recognizer.cancel();

    }

}






