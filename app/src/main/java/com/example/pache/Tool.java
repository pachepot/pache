package com.example.pache;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

public class Tool {

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
