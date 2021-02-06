package com.guptagagann.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.guptagagann.myapplication.entity.FlaskApiResponseBody;
import com.guptagagann.myapplication.entity.SourceFileRequestBody;
import com.guptagagann.myapplication.retrofit.RetrofitApis;
import com.guptagagann.myapplication.retrofit.RetrofitInstanceGetter;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Pattern;

/**
 * <p>A fragment that shows a list of items as a modal bottom sheet.</p>
 * <p>You can show this modal bottom sheet from your activity like this:</p>
 * <pre>
 *     RatingBottomSheetFragment.newInstance(30).show(getSupportFragmentManager(), "dialog");
 * </pre>
 */
public class RatingBottomSheetFragment extends BottomSheetDialogFragment {
    String documentName;
    Float rating;
    TextView ratingText;
    TextView summaryTitle;
    ScrollView scrollView;
    RatingBar ratingBar;
    EditText email;
    EditText review;
    TextView wordCount;
    TextView questionText1, questionText2, questionText3, questionText4;
    Button clearRatings1, clearRatings2, clearRatings3, clearRatings4;
    CardView cardView1, cardView2, cardView3, cardView4;
    RatingBar ratingBar1, ratingBar2, ratingBar3, ratingBar4;
    Button submit;
    public RatingBottomSheetFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = getArguments();
        documentName = b.getString("documentName");
        rating = b.getFloat("rating");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_rating_bottom_sheet_list_dialog, container, false);
        ratingText = v.findViewById(R.id.ratingText);
        ratingText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleUserExit();
            }
        });
        summaryTitle = v.findViewById(R.id.summaryTitle);
        summaryTitle.setText("Summary for : "+documentName);
        email = v.findViewById(R.id.email);
        review = v.findViewById(R.id.review);
        wordCount = v.findViewById(R.id.wordCount);
        scrollView = v.findViewById(R.id.scrollView);
        /*scrollView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                switch (event.getAction() & MotionEvent.ACTION_MASK){
                    case MotionEvent.ACTION_UP:
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }
                return false;
            }
        });*/
        review.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                switch (event.getAction() & MotionEvent.ACTION_MASK){
                    case MotionEvent.ACTION_UP:
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }
                return false;
            }
        });
        review.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                wordCount.setText(s.length()+"/500");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        ratingBar = v.findViewById(R.id.rating);
        ratingBar.setRating(rating);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                Toast.makeText(v.getContext(),"You rated the summary "+String.valueOf(rating)+"/5.0 !",Toast.LENGTH_SHORT).show();
            }
        });
        questionText1 = v.findViewById(R.id.questionTextView1);
        clearRatings1 = v.findViewById(R.id.clearRatings1);
        cardView1 = v.findViewById(R.id.cardView1);
        ratingBar1 = v.findViewById(R.id.ratingBar1);
        clearRatings1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ratingBar1.setRating(0);
            }
        });

        questionText2 = v.findViewById(R.id.questionTextView2);
        clearRatings2 = v.findViewById(R.id.clearRatings2);
        cardView2 = v.findViewById(R.id.cardView2);
        ratingBar2 = v.findViewById(R.id.ratingBar2);
        clearRatings2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ratingBar2.setRating(0);
            }
        });

        questionText3 = v.findViewById(R.id.questionTextView3);
        clearRatings3 = v.findViewById(R.id.clearRatings3);
        cardView3 = v.findViewById(R.id.cardView3);
        ratingBar3 = v.findViewById(R.id.ratingBar3);
        clearRatings3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ratingBar3.setRating(0);
            }
        });

        questionText4 = v.findViewById(R.id.questionTextView4);
        clearRatings4 = v.findViewById(R.id.clearRatings4);
        cardView4 = v.findViewById(R.id.cardView4);
        ratingBar4 = v.findViewById(R.id.ratingBar4);
        clearRatings4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ratingBar4.setRating(0);
            }
        });

        submit = v.findViewById(R.id.submitReview);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (!isValidEmail(email.getText().toString())) {
                    email.setError("Please provide the valid email!");
                }else {
                    RetrofitApis retrofitApis = RetrofitInstanceGetter
                            .getRetrofitInstance("http://guptagagann1.pythonanywhere.com/")
                            .create(RetrofitApis.class);

                    SourceFileRequestBody sourceFileRequestBody = new SourceFileRequestBody();
                    sourceFileRequestBody.setSourceFile("WewReview " +
                            ratingBar.getRating() + "\n" +
                            documentName + "\n" +
                            email.getText() + "\n" +
                            review.getText() + "\n" +
                            ratingBar1.getRating() + " " +
                            ratingBar2.getRating() + " " +
                            ratingBar3.getRating() + " " +
                            ratingBar4.getRating() + " ");
                    retrofitApis.sendSourceFileToFlask(sourceFileRequestBody).enqueue(new Callback<FlaskApiResponseBody>() {

                        @Override
                        public void onResponse(Call<FlaskApiResponseBody> call, Response<FlaskApiResponseBody> response) {
                            if (response.body() != null && response.errorBody() == null) {
                                Toast.makeText(v.getContext(),
                                        "Review recorded successfully!", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<FlaskApiResponseBody> call, Throwable t) {
                            Toast.makeText(v.getContext(),
                                    "Flask api call failed for main file!", Toast.LENGTH_SHORT).show();
                        }
                    });
                    Toast.makeText(v.getContext(), "Submitting review...", Toast.LENGTH_SHORT).show();
                    handleUserExit();
                }
            }
        });
        return v;
    }

    public static boolean isValidEmail(String email)
    {
        String emailRegex = "\\A(?=[a-z0-9@.!#$%&'*+/=?^_‘{|}~-]{6,254}\\z)(?=[a-z0-9.!#$%&'*+/=?^_‘{|}~-]{1,64}@)[a-z0-9!#$%&'*+/=?^_‘{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_‘{|}~-]+)*@(?:(?=[a-z0-9-]{1,63}\\.)[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+(?=[a-z0-9-]{1,63}\\z)[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\z";

        Pattern pat = Pattern.compile(emailRegex,Pattern.CASE_INSENSITIVE);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }

    public void onCancel(DialogInterface dialog)
    {
        super.onCancel(dialog);
        handleUserExit();
    }

    private void handleUserExit() {
        Intent intent = new Intent(getActivity().getBaseContext(),
                ResultPage.class);
        intent.putExtra("rating", rating);
        getActivity().startActivity(intent);
        dismiss();
    }

}
