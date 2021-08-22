package com.guptagagann.myapplication;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.TypefaceSpan;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.guptagagann.myapplication.entity.FlaskApiResponseBody;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;

public class ParaPage extends AppCompatActivity {
    LinearLayout layout;

    String lookUp;
    List<String> sortedSentenceList;
    List<String> sortedSentimentList;
    Map<String, Integer> tncs_dt;
    List<String> paras_list;
    Map<String, Integer> rankedDictionary;

    String[] paras;

    int para_no;
    String para;

    String[] wordsToHL;
    int[] weight;

    TextView toggleTextView;
    TextView paraTextView;
    TextView paraTextView2;
    ToggleButton changeParaView;
    ScrollView scrollView1;
    ScrollView scrollView2;

    Button prev;
    Button next;

    MenuItem search;

    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_para_page);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }
        });

        layout = findViewById(R.id.linearLayout);
        paraTextView=findViewById(R.id.textViewPara1);
        paraTextView2=findViewById(R.id.textViewPara2);
        scrollView1=findViewById(R.id.scrollView1);
        scrollView2=findViewById(R.id.scrollView2);
        changeParaView=findViewById(R.id.toggleButton1);
        toggleTextView=findViewById(R.id.toggleText);
        toggleTextView.setTextColor(Color.LTGRAY);
        FlaskApiResponseBody flaskApiResponseBody= (FlaskApiResponseBody) getIntent().getSerializableExtra("data");
        if(null!=flaskApiResponseBody){
            sortedSentenceList = flaskApiResponseBody.getSortedSentenceList();
            sortedSentimentList = flaskApiResponseBody.getSortedSentimentList();
            tncs_dt = flaskApiResponseBody.getTncs_dt();
            paras_list = flaskApiResponseBody.getParas_list();
            rankedDictionary = flaskApiResponseBody.getRankedDictionary();
        }else {
//            Toast.makeText(getApplicationContext(),
//                    "Null object! Phone fek de!",Toast.LENGTH_SHORT).show();
        }

        paras = new String[paras_list.size()];
        paras = paras_list.toArray(paras);

        final Bundle paraDetails = getIntent().getExtras();
        para_no = paraDetails.getInt("currPara");

        showParagraph(paras, para_no);

        prev=findViewById(R.id.prev);
        next=findViewById(R.id.next);
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(para_no-1>=0){
                    para_no--;
                    showParagraph(paras,para_no);
                    return;
                }
                Toast.makeText(getApplicationContext(), "You've reached the start of the document!",Toast.LENGTH_SHORT).show();
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(para_no+1!=paras.length){
                    para_no++;
                    showParagraph(paras,para_no);
                    return;
                }
                Toast.makeText(getApplicationContext(), "You've reached the end of the document!",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showParagraph(String[] paras, int para_no) {
        //Toast.makeText(getApplicationContext(), para_no+"",Toast.LENGTH_SHORT).show();
        paraTextView.setText("");
        paraTextView2.setText("");
        para=(String) Array.get(paras,para_no);
        String para_splitted[] = para.split(" ");
        final List<String>  para_as_list;
        para_as_list = Arrays.asList(para_splitted);
        wordsToHL = new String[para_as_list.size()*3];
        weight = new int[para_as_list.size()*3];
        int itr=0;
        for(String word : para_as_list){
            if(word.length()>0) {
                if (rankedDictionary.containsKey(word.toLowerCase() + " ")) {
                    wordsToHL[itr] = " " + word + " ";
                    weight[itr] = rankedDictionary.get(word.toLowerCase() + " ");
                    itr++;
                }
            }
        }

        final String para_copy = para;
        //para = para.replaceFirst("^\\d+[.\\d+]+ ","");
        final Spannable paragraph = new SpannableStringBuilder(para);

        changeParaView.setChecked(false);
        scrollView1.setVisibility(View.GONE);
        paraTextView2.setText(para);
        paraTextView2.setJustificationMode(Layout.JUSTIFICATION_MODE_INTER_WORD);

        changeParaView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String test="";
                if(isChecked){
                    for (int itr=0; wordsToHL[itr]!=null ;itr++){
                        test = test+wordsToHL[itr]+" "+weight[itr];
                        if(wordsToHL[itr]!=null) {
                            final int finalItr = itr;
                            ClickableSpan clickableSpan = new ClickableSpan() {
                                @Override
                                public void onClick(@NonNull View paraTextView) {
                                    startActivity(new Intent(ParaPage.this,definitionWebView.class).putExtra("lookUp",wordsToHL[finalItr].toLowerCase().trim()));
                                }

                                @Override
                                public void updateDrawState(@NonNull TextPaint ds) {
                                    super.updateDrawState(ds);
                                    switch (weight[finalItr]){
                                        case 15:
                                            ds.setColor(Color.parseColor("#520000"));
                                            break;
                                        case 14:
                                            ds.setColor(Color.parseColor("#aa0000"));
                                            break;
                                        case 13:
                                            ds.setColor(Color.parseColor("#ff0000"));
                                            break;
                                        case 12:
                                            ds.setColor(Color.parseColor("#ff4500"));
                                            break;
                                        case 11:
                                            ds.setColor(Color.parseColor("#D81B60"));
                                            break;
                                        case 10:
                                            ds.setColor(Color.parseColor("#4B0082"));
                                            break;
                                        case 9:
                                            ds.setColor(Color.parseColor("#800080"));
                                            break;
                                        case 8:
                                            ds.setColor(Color.parseColor("#0000FF"));
                                            break;
                                        case 7:
                                            ds.setColor(Color.parseColor("#1976D2"));
                                            break;
                                        case 6:
                                            ds.setColor(Color.parseColor("#2F4F4F"));
                                            break;
                                        case 5:
                                            ds.setColor(Color.parseColor("#006400"));
                                            break;
                                        case 4:
                                            ds.setColor(Color.parseColor("#388E3C"));
                                            break;
                                        case 3:
                                            ds.setColor(Color.parseColor("#556B2F"));
                                            break;
                                        case 2:
                                            ds.setColor(Color.parseColor("#6B8E23"));
                                            break;
                                        case 1:
                                            ds.setColor(Color.parseColor("#808000"));
                                            break;
                                    }
                                    ds.setUnderlineText(false);
                                }
                            };
                            lookUp = wordsToHL[itr];
                            Pattern pattern=Pattern.compile(lookUp);
                            Matcher matcher=pattern.matcher(para);
                            int startSpan = para.indexOf(lookUp.trim());
                            if(matcher.find()){
                                //Toast.makeText(getApplicationContext(), "*"+lookUp+"*",Toast.LENGTH_SHORT).show();
                                startSpan = matcher.start();
                            }
                            int endSpan = startSpan + lookUp.length();
                            paragraph.setSpan(clickableSpan, startSpan, Integer.min(endSpan,para.length()), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            paragraph.setSpan(new TypefaceSpan("sans-serif-medium"), startSpan, Integer.min(endSpan,para.length()), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                    }
                    scrollView2.setVisibility(View.GONE);
                    scrollView1.setVisibility(View.VISIBLE);
                    paraTextView.setText(paragraph);
                    paraTextView.setHighlightColor(Color.LTGRAY);
                    //paraTextView.setJustificationMode(Layout.JUSTIFICATION_MODE_INTER_WORD);
                    paraTextView.setMovementMethod(new LinkMovementMethod());
//                    paraTextView.setTextIsSelectable(true);
                    paraTextView.setSelected(true);
                }

                else {
                    scrollView1.setVisibility(View.GONE);
                    scrollView2.setVisibility(View.VISIBLE);
                    paraTextView2.setText(para_copy);
                    paraTextView2.setJustificationMode(Layout.JUSTIFICATION_MODE_INTER_WORD);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        // Get the SearchView and set the searchable configuration
//        // Inflate the options menu from XML
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.menu_report, menu);
//        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
//        // Assumes current activity is the searchable activity
//        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
//        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
//
//        return true;
//        getMenuInflater().inflate(R.menu.menu_report, menu);
//        search = menu.findItem(R.id.search_report);
//        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(search);
//        searchView.setMaxWidth(Integer.MAX_VALUE);
//        SearchManager searchManager = (SearchManager)getSystemService(Context.SEARCH_SERVICE);
//        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
////        searchView.setSubmitButtonEnabled(true);
////        searchView.setQueryRefinementEnabled(true);
//
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                searchView.clearFocus();
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                Toast.makeText(getApplicationContext(), "*"+newText+"*",Toast.LENGTH_SHORT).show();
//                return false;
//            }
//        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Toast.makeText(getApplicationContext(),
                    query,Toast.LENGTH_SHORT).show();
        }
    }
}
