package com.guptagagann.myapplication;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.provider.SyncStateContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.guptagagann.myapplication.entity.FlaskApiResponseBody;
import com.guptagagann.myapplication.entity.SourceFileRequestBody;
import com.guptagagann.myapplication.retrofit.RetrofitApis;
import com.guptagagann.myapplication.retrofit.RetrofitInstanceGetter;
import com.itextpdf.text.List;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import lombok.SneakyThrows;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.guptagagann.myapplication.Constants.IS_WELCOME_ACTIVITY_SHOWN;
import static com.guptagagann.myapplication.Constants.LAUNCH_COUNT;
import static com.guptagagann.myapplication.Constants.VERSION_NAME;

public class MainActivity extends AppCompatActivity {
    private SharedPreferences mSharedPreferences;
    private boolean mDoubleBackToExitPressedOnce = false;

    Button saveButton;
    private String filename = "SampleFile.txt";
    private String filepath = "MyFileStorage";
    File myExternalFile;
    ImageButton _fileOpener;
    ImageButton _pasteDialogOpener;
    ImageButton _resetButton;
    ImageView importImageView;
    TextView importedFilenameText;
    Button _flask_call;
    SeekBar seekBar;
    TextView percent_seeked;

    int reduction_percent_selected;
    String fileToOpen;

    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
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
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }
        });
//
//        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
//        int count = mSharedPreferences.getInt(LAUNCH_COUNT, 0);
//        if (count > 0 && count % 1 == 0)
//            // mFeedbackUtils.rateUs();
//            mSharedPreferences.edit().putInt(LAUNCH_COUNT, count + 1).apply();
//
//        String versionName = mSharedPreferences.getString(VERSION_NAME, "");
//        if (!versionName.equals(BuildConfig.VERSION_NAME)) {
//            // WhatsNewUtils.displayDialog(this);
//            mSharedPreferences.edit().putString(VERSION_NAME, BuildConfig.VERSION_NAME).apply();
//        }
//
//        //check for welcome activity
//        openWelcomeActivity();
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                String mailto = "mailto:guptagagann@gmail.com" +
                        "?cc=" + "" +
                        "&subject=" + Uri.encode("Suggestion/Bug Report!") +
                        "&body=" + Uri.encode("");

                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse(mailto));

                try {
                    startActivity(emailIntent);
                } catch (ActivityNotFoundException e) {
                    //TODO: Handle case where no email app is available

                }
            }
        });

        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            saveButton.setEnabled(false);
        }
        else {
            myExternalFile = new File(getExternalFilesDir(filepath), filename);
        }

        seekBar=findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                //Toast.makeText(getApplicationContext(),"seekbar progress: "+progress, Toast.LENGTH_SHORT).show();
                percent_seeked=findViewById(R.id.percent_seeked);
                percent_seeked.setText(Integer.toString(progress)+"%");
                reduction_percent_selected=progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //Toast.makeText(getApplicationContext(),"seekbar touch started!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //Toast.makeText(getApplicationContext(),"seekbar touch stopped!", Toast.LENGTH_SHORT).show();
            }
        });

        _fileOpener=findViewById(R.id.fileOpener);

        _fileOpener.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!checkPermission()) {
                    openActivity();
                } else {
                    if (checkPermission()) {
                        requestPermissionAndContinue();
                    } else {
                        openActivity();
                    }
                }
            }
        });

        _pasteDialogOpener = findViewById(R.id.pasteDialogOpener);
        _pasteDialogOpener.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetDialogFragment bottomSheetFragment = new PasteTextFragment();
                //bottomSheetFragment.setArguments(args);
                bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
            }
        });

        _resetButton = findViewById(R.id.reset);
        _resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                importedFilenameText.setText(getString(R.string.filepath));
                fileToOpen = null;
                _resetButton.setVisibility(View.INVISIBLE);
                importImageView.setVisibility(View.INVISIBLE);
            }
        });

        importImageView = findViewById(R.id.importImage);
        importedFilenameText = findViewById(R.id.importedFilename);

        _flask_call=findViewById(R.id.flaskCall_btn);
        _flask_call.setOnClickListener(new View.OnClickListener() {
            @SneakyThrows
            @Override
            public void onClick(View v) {
                if(fileToOpen!=""&&fileToOpen!=null){
                    final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this,R.style.Theme_AppCompat_Light_Dialog);
                    progressDialog.setIndeterminate(true);
                    progressDialog.setMessage("Generating Summary...");
                    progressDialog.show();
                    File file=new File(fileToOpen);
                    String[] fileToOpenExt;
                    fileToOpenExt = fileToOpen.split("\\.");
                    int len = fileToOpenExt.length;
                    String fileContents="";
                    if(fileToOpenExt[len-1].equals("txt")) {
                        StringBuilder stringBuilder = new StringBuilder();
                        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
                        String line;
                        while ((line = bufferedReader.readLine()) != null) {
                            stringBuilder.append(line);
                            stringBuilder.append("\n");
                        }
                        bufferedReader.close();
                        fileContents = stringBuilder.toString();
                    }
                    //System.out.println(fileContents);

                    else if (fileToOpenExt[len-1].equals("pdf")) {
                        try {
                            String parsedText = "";
                            PdfReader reader = new PdfReader(fileToOpen);
                            int n = reader.getNumberOfPages();
                            for (int i = 0; i < n; i++) {
                                parsedText = parsedText + PdfTextExtractor.getTextFromPage(reader, i + 1).trim() + " ";
                                //Extracting the content from the different pages
                            }
                            //System.out.println(parsedText);
                            fileContents = parsedText;
                            reader.close();
                        } catch (Exception e) {
                            //System.out.println(e);
                        }
                    }

                    RetrofitApis retrofitApis= RetrofitInstanceGetter
                            .getRetrofitInstance("https://guptagagann1.pythonanywhere.com")
                            .create(RetrofitApis.class);

                    SourceFileRequestBody sourceFileRequestBody=new SourceFileRequestBody();
                    sourceFileRequestBody.setSourceFile(fileContents);
                    retrofitApis.sendSourceFileToFlask(sourceFileRequestBody).enqueue(new Callback<FlaskApiResponseBody>() {

                        @Override
                        public void onResponse(Call<FlaskApiResponseBody> call, Response<FlaskApiResponseBody> response) {
                            if(response.body()!=null && response.errorBody()==null){
                                startActivity(new Intent(MainActivity.this,ResultPage.class)
                                        .putExtra("documentName",documentName)
                                        .putExtra("data",response.body())
                                        .putExtra("pr",reduction_percent_selected));

                            }
                            else {
                                Toast.makeText(getApplicationContext(),"Summary generation failed! File too big or contains unsupported characters.",Toast.LENGTH_LONG).show();
                            }
                            new android.os.Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.dismiss();
                                }
                            },2000);
                        }

                        @Override
                        public void onFailure(Call<FlaskApiResponseBody> call, Throwable t) {
                            Toast.makeText(getApplicationContext(),
                                    "Flask api call failed for main file",Toast.LENGTH_SHORT).show();

                            new android.os.Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.dismiss();
                                }
                            },2000);
                        }
                    });
                }else{
                    Toast.makeText(MainActivity.this, "Select file!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private static boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    private static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    String documentName;
    @SneakyThrows
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this,R.style.Theme_AppCompat_Light_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Importing file...");
        progressDialog.show();
        if(resultCode != RESULT_CANCELED) {
            String filepath = "";
            Uri uri = data.getData();
            if (isExternalStorageDocument(uri)) {
                String tempId = DocumentsContract.getDocumentId(data.getData());
                String splitArray[] = tempId.split(":");
                String type = splitArray[0];
                String id = splitArray[1];
                documentName = id;
                if (id.contains("/")) {
                    documentName = id.split("\\/", -2)[id.split("\\/", -2).length - 1];
                }
                if (type.equals("primary")) {
                    filepath = Environment.getExternalStorageDirectory() + "/" + id;

                } else {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "File doesn't have primary type!",
                            Toast.LENGTH_LONG);
                    toast.show();
                }
            } else if (isDownloadsDocument(uri)) {
                Toast.makeText(getApplicationContext(),
                        "Sorry! :( We're not supporting importing from downloads and drive as of now.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "Sorry! :( We're not supporting importing from downloads and drive as of now.", Toast.LENGTH_LONG).show();
            }
            fileToOpen = filepath;
            if (fileToOpen != "" && fileToOpen != null) {
                importedFilenameText.setText(fileToOpen);
                _resetButton.setVisibility(View.VISIBLE);
                importImageView.setVisibility(View.VISIBLE);
                importImageView.setImageDrawable(getDrawable(R.drawable.ic_import));
            }
        }
        else {
            importedFilenameText.setText("File import cancelled/failed! Try again!");
        }
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
            }
        }, 500);
    }

    private boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    private boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    private static final int PERMISSION_REQUEST_CODE = 200;

    private boolean checkPermission() {
        return ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                ;
    }

    private void requestPermissionAndContinue() {
        if (ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, WRITE_EXTERNAL_STORAGE)
                    && ActivityCompat.shouldShowRequestPermissionRationale(this, READ_EXTERNAL_STORAGE)) {
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
                alertBuilder.setCancelable(true);
                alertBuilder.setTitle(getString(R.string.permission_necessary));
                alertBuilder.setMessage("It looks like you've denied storage permission to "
                        +getString(R.string.app_name)+". Please allow the same to use this feature!");
                alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{WRITE_EXTERNAL_STORAGE
                                , READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
                    }
                });
                AlertDialog alert = alertBuilder.create();
                alert.show();
                Log.e("", "permission denied, show dialog");
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{WRITE_EXTERNAL_STORAGE,
                        READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            }
        } else {
            openActivity();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED) {
            if (requestCode == PERMISSION_REQUEST_CODE) {
                if (permissions.length > 0 && grantResults.length > 0) {

                    boolean flag = true;
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            flag = false;
                        }
                    }
                    if (flag) {
                        openActivity();
                    } else {
                        finish();
                    }

                } else {
                    finish();
                }
            } else {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
        else {
            Toast.makeText(getApplicationContext(),
                    "You are required to allow storage access to use this feature!", Toast.LENGTH_LONG).show();
        }
    }

    private void openActivity() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        String [] mimeTypes = {"text/plain", "application/pdf"};
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this,R.style.Theme_AppCompat_Light_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Opening explorer...");
        progressDialog.show();

        startActivityForResult(intent,2);

        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
            }
        },500);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {
            startActivity(new Intent(MainActivity.this,About.class));
            return true;
        }
        if (id == R.id.action_help) {
            startActivity(new Intent(MainActivity.this,WelcomeActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        //Toast.makeText(getApplicationContext(),""+intent.getStringExtra("fileText"), Toast.LENGTH_SHORT).show();
        String text = intent.getStringExtra("fileText");
        String filename = intent.getStringExtra("filename");
        importedFilenameText.setText(filename);
        _resetButton.setVisibility(View.VISIBLE);
        importImageView.setVisibility(View.VISIBLE);
        importImageView.setImageDrawable(getDrawable(R.drawable.ic_paste));

        fileToOpen = myExternalFile.toString();
        documentName = filename;

        try {
            FileOutputStream fos = new FileOutputStream(myExternalFile);
            fos.write(text.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openWelcomeActivity() {
        if (!mSharedPreferences.getBoolean(IS_WELCOME_ACTIVITY_SHOWN, false)) {
            Intent intent = new Intent( MainActivity.this, WelcomeActivity.class);
            mSharedPreferences.edit().putBoolean(IS_WELCOME_ACTIVITY_SHOWN, true).apply();
            startActivity(intent);
        }
    }

    private static long back_pressed;

    @Override
    public void onBackPressed() {
        if (back_pressed + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
        } else {
            Toast.makeText(getBaseContext(), "Back once again to exit the app!",
                    Toast.LENGTH_SHORT).show();
            back_pressed = System.currentTimeMillis();
        }
    }


}
