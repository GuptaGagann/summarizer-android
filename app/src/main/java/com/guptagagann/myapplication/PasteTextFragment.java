package com.guptagagann.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;


///**
// * A simple {@link Fragment} subclass.
// * Use the {@link PasteTextFragment#newInstance} factory method to
// * create an instance of this fragment.
// */
public class PasteTextFragment extends BottomSheetDialogFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    TextView pasteDialogText;
    Button saveText;
    EditText editText;
    EditText filename;

    Editable text;
    Editable fileName;


    public PasteTextFragment() {
        // Required empty public constructor
    }

//    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
//     * @return A new instance of fragment PasteTextFragment.
//     */
    // TODO: Rename and change types and number of parameters
//    public static PasteTextFragment newInstance(String param1, String param2) {
//        PasteTextFragment fragment = new PasteTextFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setCancelable(false);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_paste_text, container, false);
        pasteDialogText = v.findViewById(R.id.editDialogText);
        pasteDialogText.setClickable(true);
        pasteDialogText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        editText = v.findViewById(R.id.fileEditText);
        editText.setOnTouchListener(new View.OnTouchListener() {
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
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        filename = v.findViewById(R.id.fileName);
        saveText = v.findViewById(R.id.saveText);
        saveText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text = editText.getText();
                fileName = filename.getText();
                handleUserExit(text,fileName);
            }
        });
        return v;
    }

    public void onCancel(DialogInterface dialog)
    {
        super.onCancel(dialog);
    }

    private void handleUserExit(Editable text, Editable fileName) {
        if (TextUtils.isEmpty(fileName.toString())){
            filename.setError("Please provide a filename!");
        }else {
            //do something
            Toast.makeText(getContext(),"Saving text...",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getActivity().getBaseContext(),
                    MainActivity.class);
            intent.putExtra("fileText",text.toString().replaceAll("\\n", "<br />"));
            intent.putExtra("filename",fileName.toString());
            getActivity().startActivity(intent);
            dismiss();
        }
    }
}
