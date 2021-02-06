package com.guptagagann.myapplication;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RatingBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.snackbar.Snackbar;
import com.guptagagann.myapplication.entity.FlaskApiResponseBody;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class ResultPage extends AppCompatActivity {
    TextView headingTextView;
    TextView ratingTextView;
    RatingBar ratingBar;

    List<String> paras_list;
    List<String> sortedSentenceList;
    List<String> sortedSentimentList;
    Map<String, Integer> tncs_dt;
    Map<String, Integer> rankedDictionary;

    String[] tncs;
    String[] paras;

    int para_no;

    ListView listView;
    TextView textView;
    String[] tncsListed;
    String[] numbering_array;
    String[] listItem;
    HighlightArrayAdapter adapter;
    ArrayAdapter adapter_no;

    int reduction_percent_selected;
    PdfDocument document;
    String filename;
    String filePath;
    String documentName;

    MenuItem search;
    MenuItem export;
    MenuItem pdfProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_page);

        headingTextView = findViewById(R.id.heading);
        ratingTextView = findViewById(R.id.ratingText);
        ratingBar = findViewById(R.id.rating);

        Bundle b1 = getIntent().getExtras();
        final Serializable responseBody = getIntent().getSerializableExtra("data");
        reduction_percent_selected=b1.getInt("pr");
        documentName=b1.getString("documentName");
        documentName=documentName.split("\\.",-2)[0];

        Log.d("", "onCreate: ");

        FlaskApiResponseBody flaskApiResponseBody= (FlaskApiResponseBody) getIntent().getSerializableExtra("data");
        if(null!=flaskApiResponseBody){
            sortedSentenceList=flaskApiResponseBody.getSortedSentenceList();
            sortedSentimentList=flaskApiResponseBody.getSortedSentimentList();
            tncs_dt = flaskApiResponseBody.getTncs_dt();
            paras_list = flaskApiResponseBody.getParas_list();
            rankedDictionary = flaskApiResponseBody.getRankedDictionary();
        }else {
            Toast.makeText(getApplicationContext(),
                    "Null object! Phone fek de!",Toast.LENGTH_SHORT).show();
        }

        tncs = new String[sortedSentenceList.size()];
        tncs = sortedSentenceList.toArray(tncs);

        int sortedSentenceListSzAfterReduction=sortedSentenceList.size()-(sortedSentenceList.size()*reduction_percent_selected/100);
        int numbering=0;

        tncsListed = new String[sortedSentenceListSzAfterReduction];
        numbering_array = new String[sortedSentenceListSzAfterReduction];
        for (int itr=0; itr<sortedSentenceListSzAfterReduction; itr++){
            String trimmed=tncs[itr].trim();
            String tnc_numbered=trimmed.substring(0,1).toUpperCase()+trimmed.substring(1);
            //tnc="-> "+tnc;
            tncsListed[numbering]=tnc_numbered;
            numbering++;
        }

        paras = new String[paras_list.size()];
        paras = paras_list.toArray(paras);

        listView=findViewById(R.id.listView);
        listItem = sortedSentenceList.toArray(new String[0]);
        adapter = new HighlightArrayAdapter(this,
                R.layout.list_result_page, R.id.textView, tncsListed, Arrays.asList(tncsListed));
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // TODO Auto-generated method stub
                final ProgressDialog progressDialog = new ProgressDialog(ResultPage.this,R.style.Theme_AppCompat_Light_Dialog);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Redirecting to paragraph...");
                progressDialog.show();
                String value=(String)adapterView.getItemAtPosition(position);
                Integer index=Arrays.asList(tncsListed).indexOf(value);
                value=tncs[index];
                para_no=tncs_dt.get(value);

                Intent i=new Intent(ResultPage.this,ParaPage.class);
                i.putExtra("data",responseBody);
                i.putExtra("currPara",para_no);
                startActivity(i);

                new android.os.Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                    }
                },1500);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> parent, final View view, final int position, long id) {
                PopupMenu popupMenu=new PopupMenu(ResultPage.this, view);
                try {
                    Field[] fields = popupMenu.getClass().getDeclaredFields();
                    for (Field field : fields) {
                        if ("mPopup".equals(field.getName())) {
                            field.setAccessible(true);
                            Object menuPopupHelper = field.get(popupMenu);
                            Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                            Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                            setForceIcons.invoke(menuPopupHelper, true);
                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                popupMenu.getMenuInflater().inflate(R.menu.popup_menu_result,popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        String value=(String)parent.getItemAtPosition(position);
                        Integer index=Arrays.asList(tncsListed).indexOf(value);
                        value=tncs[index];
                        para_no=tncs_dt.get(value);

                        switch (item.getItemId()){
                            case R.id.copy:
                                final android.content.ClipboardManager clipboardManager = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
                                ClipData clipData = ClipData.newPlainText("Source Text", tncs[position] );
                                clipboardManager.setPrimaryClip(clipData);
                                Toast.makeText(ResultPage.this, "TnC copied to clipboard!", Toast.LENGTH_SHORT).show();
                                return true;

                            case R.id.refer:
                                final ProgressDialog progressDialog = new ProgressDialog(ResultPage.this,R.style.Theme_AppCompat_Light_Dialog);
                                progressDialog.setIndeterminate(true);
                                progressDialog.setMessage("Redirecting to paragraph...");
                                progressDialog.show();
                                //Toast.makeText(ResultPage.this,"Redirecting to paragraph...", Toast.LENGTH_SHORT).show();
                                Intent i=new Intent(ResultPage.this,ParaPage.class);
                                i.putExtra("data",responseBody);
                                i.putExtra("currPara",para_no);
                                startActivity(i);

                                new android.os.Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog.dismiss();
                                    }
                                },1500);
                                return true;

                            case R.id.sentiment:
                                Snackbar snackbar = Snackbar.make(view,"Sentiment : "+sortedSentimentList.get(index), Snackbar.LENGTH_LONG);
                                View snackbarLayout = snackbar.getView();
                                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT
                                );
                                // Layout must match parent layout type
                                lp.setMargins(0, ratingTextView.getBottom()-150, 0, 0);
                                // Margins relative to the parent view.
                                snackbarLayout.setLayoutParams(lp);
                                snackbar.show();
                                return true;
                        }
                        return false;
                    }
                });
                popupMenu.show();
                return true;
            }
        });
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

                Bundle args = new Bundle();
                args.putFloat("rating",rating);
                args.putString("documentName",documentName);
                Toast.makeText(ResultPage.this,"You rated the summary "+String.valueOf(rating)+"/5.0 !",Toast.LENGTH_SHORT).show();
                ratingBar.setIsIndicator(true);
                // using BottomSheetDialogFragment
                BottomSheetDialogFragment bottomSheetFragment = new RatingBottomSheetFragment();
                bottomSheetFragment.setArguments(args);
                bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());

//                View dialogView = getLayoutInflater().inflate(R.layout.fragment_rating_bottom_sheet_list_dialog, null);
//                BottomSheetDialog dialog = new BottomSheetDialog(ResultPage.this);
//                dialog.setContentView(dialogView);
//                dialog.show();

            }
        });

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        ratingBar.setIsIndicator(false);

        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Toast.makeText(getApplicationContext(),
                    query,Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_bar, menu);
        search = menu.findItem(R.id.search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(search);
        searchView.setMaxWidth(Integer.MAX_VALUE);
        SearchManager searchManager = (SearchManager)getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (adapter!=null){
                    adapter.getFilter().filter(newText);
                }
                return false;
            }
        });

        export = menu.findItem(R.id.export);
        export.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
//                pdfProgress.setVisible(true);
//                export.setVisible(false);
                getWholeListViewItemsToBitmap(listView);
                return false;
            }
        });

        pdfProgress = menu.findItem(R.id.progressBar);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Take a screenshot of a whole listview even if the whole listview elements
     * aren't fully visible in the screen
     *
     * @param p_ListView
     *      -ListView instance
     * @return-Bitmap of List
     */
    public void getWholeListViewItemsToBitmap(ListView p_ListView) {
        final ProgressDialog progressDialog = new ProgressDialog(ResultPage.this,R.style.Theme_AppCompat_Light_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Exporting to PNG and PDF...");
        progressDialog.show();
        ListView listview = p_ListView;
        ListAdapter adapter = listview.getAdapter();
        int itemscount = adapter.getCount();
        int allitemsheight = 0;
        List<Bitmap> bmps = new ArrayList<Bitmap>();
        for (int i = 0; i < itemscount; i++) {
            View childView = adapter.getView(i, null, listview);
            childView.measure(
                    View.MeasureSpec.makeMeasureSpec(listview.getWidth(), View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            childView.layout(0, 0, childView.getMeasuredWidth(), childView.getMeasuredHeight());
            childView.setDrawingCacheEnabled(true);
            childView.buildDrawingCache();
            bmps.add(childView.getDrawingCache());
            allitemsheight += childView.getMeasuredHeight();
        }
        Bitmap bigbitmap = Bitmap.createBitmap(listview.getMeasuredWidth(), allitemsheight,
                Bitmap.Config.ARGB_8888);
        Canvas bigcanvas = new Canvas(bigbitmap);
        Paint paint = new Paint();
        int iHeight = 0;
        for (int i = 0; i < bmps.size(); i++) {
            Bitmap bmp = bmps.get(i);
            bigcanvas.drawBitmap(bmp, 0, iHeight, paint);
            iHeight += bmp.getHeight();
            bmp.recycle();
            bmp = null;
        }

        filename = documentName+".pdf";
        createPdf(bigbitmap);
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
            }
        },1500);
    }

    private void createPdf(Bitmap bitmap){
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        //  Display display = wm.getDefaultDisplay();
        DisplayMetrics displaymetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        float height = bitmap.getHeight() ;
        float width = bitmap.getWidth() ;

        int convertHeight = (int) height, convertWidth = (int) width;

//        Resources mResources = getResources();
//        Bitmap bitmap = BitmapFactory.decodeResource(mResources, R.drawable.screenshot);

        document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(convertWidth, convertHeight, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);

        Canvas canvas = page.getCanvas();

        Paint paint = new Paint();
        canvas.drawPaint(paint);

        bitmap = Bitmap.createScaledBitmap(bitmap, convertWidth, convertHeight, true);

        paint.setColor(Color.BLUE);
        canvas.drawBitmap(bitmap, 0, 0 , null);
        document.finishPage(page);

        // write the document content

        if (!checkPermission()) {
            openActivity();
        } else {
            if (checkPermission()) {
                requestPermissionAndContinue();
            } else {
                openActivity();
            }
        }
        //openGeneratedPDF(filePath);
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
                        +getString(R.string.app_name)+". Please allow the same to use this feature.");
                alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(ResultPage.this, new String[]{WRITE_EXTERNAL_STORAGE
                                , READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
                    }
                });
                AlertDialog alert = alertBuilder.create();
                alert.show();
                Log.e("", "permission denied, show dialog");
            } else {
                ActivityCompat.requestPermissions(ResultPage.this, new String[]{WRITE_EXTERNAL_STORAGE,
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
            Toast.makeText(getApplicationContext(), "You are required to allow storage permission to use this feature!", Toast.LENGTH_LONG).show();
        }
    }

    private void openActivity() {
        File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
                .getAbsolutePath() +"/"+ getString(R.string.app_name));
        if(!directory.mkdirs()){
//            Toast.makeText(this, "Directory not created!", Toast.LENGTH_LONG).show();
        }
        File file = new File(directory,filename);
        filePath = directory.toString() + File.separator + filename;
        FileOutputStream fileOutputStream;
        try {
            fileOutputStream = new FileOutputStream(file);
            document.writeTo(fileOutputStream);
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Something wrong: " + e.toString(), Toast.LENGTH_LONG).show();
        }
        // close the document
        document.close();
        Toast.makeText(ResultPage.this, "Summary exported to "+Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
                .getAbsolutePath() +"/"+ getString(R.string.app_name), Toast.LENGTH_LONG).show();
    }

    private void openGeneratedPDF(String filePath){
        File file = new File(filePath);
        if (file.exists())
        {
            Intent intent=new Intent(Intent.ACTION_VIEW);
            Uri uri = Uri.fromFile(file);
            intent.setDataAndType(uri, "application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            try
            {
                startActivity(intent);
            }
            catch(ActivityNotFoundException e)
            {
                Toast.makeText(ResultPage.this, "No Application available to view pdf", Toast.LENGTH_LONG).show();
            }
        }
    }
}


