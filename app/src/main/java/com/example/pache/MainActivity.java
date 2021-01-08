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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.StringTokenizer;


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

class Tool {

    private ArrayList<String> nickNameList;
    private HashMap<String, String> nickNameMatch;

    Tool () {

        nickNameList = new ArrayList<>();
        nickNameMatch = new HashMap<>();

        nickNameList.add("비트코인");
        nickNameList.add("이더리움");
        nickNameList.add("리플");
        nickNameList.add("이오스");
        nickNameList.add("체인링크");
        nickNameList.add("트론");
        nickNameList.add("퀀텀");
        nickNameList.add("에이다");
        nickNameList.add("더마이다스터치골드"); nickNameList.add("맘터"); nickNameList.add("맘스터치");
        nickNameList.add("젠서");
        nickNameList.add("플레타");
        nickNameList.add("엘리시아");

        nickNameMatch.put("비트코인", "BTC");
        nickNameMatch.put("이더리움", "ETH");
        nickNameMatch.put("리플", "XRP");
        nickNameMatch.put("이오스", "EOS");
        nickNameMatch.put("체인링크", "LINK");
        nickNameMatch.put("트론", "TRX");
        nickNameMatch.put("퀀텀", "QTUM");
        nickNameMatch.put("더마이다스터치골드", "TMTG"); nickNameMatch.put("맘터", "TMTG"); nickNameMatch.put("맘스터치", "TMTG");
        nickNameMatch.put("젠서", "XSR");
        nickNameMatch.put("플레타", "FLETA");
        nickNameMatch.put("엘리시아", "EL");

    }

    public void bithumb () {

        final StringBuilder sb = new StringBuilder();
        final String apiUrl = "https://api.bithumb.com/public/ticker/all";

        Thread crawl = new Thread(new Runnable() {

            @Override
            public void run() {
                try {

                    Document document = Jsoup.connect(apiUrl).ignoreContentType(true).get();
                    sb.append(document.toString());

                    StringTokenizer st = new StringTokenizer(sb.toString(), " []{},:\"");
                    ArrayList<String> word = new ArrayList<>();
                    while (st.hasMoreTokens()) word.add(st.nextToken()); // Split

                    bithumb_Parsing(word); // Parsing

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        });

        crawl.start();

    }

    public void coinOne () {

        final StringBuilder sb = new StringBuilder();
        final String apiUrl = "https://api.coinone.co.kr/ticker/?currency=all";

//        final Handler handler = new Handler(){
//            @SuppressLint("HandlerLeak")
//            public void handleMessage(Message msg){
//                coinAction = findViewById(R.id.BTC);
//                for (int i=0; i<number; i++) if (info[i][0].equals("btc")) coinAction.setText("btc : "+info[i][1]);
//
//                coinAction = findViewById(R.id.TMTG);
//                for (int i=0; i<number; i++) if (info[i][0].equals("tmtg")) coinAction.setText("tmtg : "+info[i][1]);
//
//                coinAction = findViewById(R.id.LBXC);
//                for (int i=0; i<number; i++) if (info[i][0].equals("lbxc")) coinAction.setText("lbxc : "+info[i][1]);
//            }
//        };

        Thread crawl = new Thread(new Runnable() {

            @Override
            public void run() {
                try {

                    Document document = Jsoup.connect(apiUrl).ignoreContentType(true).get();
                    sb.append(document.toString());

                    StringTokenizer st = new StringTokenizer(sb.toString(), " []{},:\"");
                    ArrayList<String> word = new ArrayList<>();
                    while (st.hasMoreTokens()) word.add(st.nextToken()); // Split

                    coinOne_Parsing(word); // Parsing

//                    Message msg = handler.obtainMessage();
//                    handler.sendMessage(msg);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        });

        crawl.start();



    }

    public void bithumb_Parsing (ArrayList<String> word) {

        int i=0, size=word.size();

        while (i<size) {

            switch (word.get(i)) {

                case "opening_price" :
                    MainActivity.info[MainActivity.number][0] = word.get(i-1);
                    i++;
                    break;

                case "closing_price" :
                    MainActivity.info[MainActivity.number][1] = word.get(++i);
                    i++;
                    MainActivity.number++;
                    break;

                default :
                    i++;

            }

        }

        for (int j=0; j<MainActivity.number; j++) {
            nickNameList.add(MainActivity.info[j][0].toLowerCase());
            nickNameMatch.put(MainActivity.info[j][0].toLowerCase(), MainActivity.info[j][0]);
        }

    }

    public void coinOne_Parsing (ArrayList<String> word) {

        int i=0, size=word.size();

        while (i<size) {

            switch (word.get(i)) {

                case "currency" :
                    MainActivity.info[MainActivity.number][0] = word.get(++i);
                    i++;
                    break;

                case "last" :
                    MainActivity.info[MainActivity.number][1] = word.get(++i);
                    i++;
                    MainActivity.number++;
                    break;

                default :
                    i++;

            }

        }

    }

    public String string_Parsing (String s, boolean check) {

        String return_String = "";

        if (s.contains("파체") || s.contains("파채") || check) ;
        else return "";



        for (int i = 0; i < nickNameList.size(); i++) {

            if (s.contains(nickNameList.get(i))) {

                String coinName = nickNameMatch.get(nickNameList.get(i));

                if (s.contains("시세") || s.contains("가격")) {

                    String price = "-1";

                    for (int j = 0; j < MainActivity.number; j++) {

                        if (MainActivity.info[j][0].equals(coinName)) {

                            price = MainActivity.info[j][1];
                            break;

                        }

                    }

                    return_String = coinName + "의 가격은 " + price + "원 이에요.";
                    break;

                }

                else if (s.contains("매수")) ;

                else if (s.contains("매도")) ;

            }

        }

        return return_String;


    }

}