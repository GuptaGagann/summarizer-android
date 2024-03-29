package com.guptagagann.myapplication;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;

public class HighlightArrayAdapter extends ArrayAdapter<String> {
    private LayoutInflater mInflater;
    private Context mContext;
    private int mResource;
    private List<String> mObjects;
    private int mFieldId = 0;
    private ArrayList<String> mOriginalValues;
    private ArrayFilter mFilter;
    private final Object mLock = new Object();

    private String mSearchText; // this var for highlight
    List<String> sentenceList;

    public HighlightArrayAdapter(Context context, int resource, int textViewResourceId, String[] objects, List<String> sortedSentenceList) {
        super(context, resource, textViewResourceId, objects);
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mResource = resource;
        mObjects = Arrays.asList(objects);
        mFieldId = textViewResourceId;
        sentenceList = sortedSentenceList;
    }

    @Override
    public Context getContext() {
        return mContext;
    }

    @Override
    public int getCount() {
        return mObjects.size();
    }

    @Override
    public String getItem(int position) {
        return mObjects.get(position);
    }

    @Override
    public int getPosition(String item) {
        return mObjects.indexOf(item);
    }

    @Override
    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new ArrayFilter();
        }
        return mFilter;
    }

    private class ArrayFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();

            if (mOriginalValues == null) {
                synchronized (mLock) {
                    mOriginalValues = new ArrayList<>(mObjects);
                }
            }

            if (prefix == null) {
                ArrayList<String> list;
                synchronized (mLock) {
                    list = new ArrayList<>(mOriginalValues);
                }
                results.values = list;
                results.count = list.size();
            } else {
                String prefixString = prefix.toString().toLowerCase();
                mSearchText = prefixString;
                ArrayList<String> values;
                synchronized (mLock) {
                    values = new ArrayList<>(mOriginalValues);
                }

                final int count = values.size();
                final ArrayList<String> newValues = new ArrayList<>();

                for (int i = 0; i < count; i++) {
                    final String value = values.get(i);
                    final String valueText = value.toLowerCase();

                    // First match against the whole, non-splitted value
                    if (valueText.startsWith(prefixString)) {
                        newValues.add(value);
                    } else {
                        final String[] words = valueText.split(" ");
                        final int wordCount = words.length;

                        // Start at index 0, in case valueText starts with space(s)
                        for (int k = 0; k < wordCount; k++) {
                            if (words[k].startsWith(prefixString)) {
                                newValues.add(value);
                                break;
                            }
                        }
                    }
                }

                results.values = newValues;
                results.count = newValues.size();
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            //noinspection unchecked
            mObjects = (List<String>) results.values;
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View view;
        TextView text;

        if (convertView == null) {
            view = mInflater.inflate(mResource, parent, false);
        } else {
            view = convertView;
        }

        try {
            if (mFieldId == 0) {
                //  If no custom field is assigned, assume the whole resource is a TextView
                text = (TextView) view;
            } else {
                //  Otherwise, find the TextView field within the layout
                text = (TextView) view.findViewById(mFieldId);
            }
        } catch (ClassCastException e) {
            Log.e("ArrayAdapter", "You must supply a resource ID for a TextView");
            throw new IllegalStateException(
                    "ArrayAdapter requires the resource ID to be a TextView", e);
        }

        String item = getItem(position);
        text.setText(item);
        int Sentence_no = sentenceList.indexOf(item)+1;
        TextView numbering = view.findViewById(R.id.textViewNumbering);
        numbering.setText(String.valueOf(Sentence_no)+".");

        String fullText = getItem(position);
        // highlight search text
        if (mSearchText != null && !mSearchText.isEmpty()) {
            int startPos = fullText.toLowerCase(Locale.US).indexOf(mSearchText.toLowerCase(Locale.US));
            int endPos = startPos + mSearchText.length();

            if (startPos != -1) {
                Spannable spannable = new SpannableString(fullText);
                spannable.setSpan(new BackgroundColorSpan(Color.LTGRAY), startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannable.setSpan(new ClickableSpan() {
                    @Override
                    public void onClick(@NonNull View widget) {
                        Snackbar snackbar = Snackbar.make(view,"shuuu", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                    public void updateDrawState(@NonNull TextPaint ds) {
                        super.updateDrawState(ds);
                        ds.setUnderlineText(false);
                    }
                }, startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                text.setText(spannable);
                //text.setMovementMethod(LinkMovementMethod.getInstance());
            } else {
                text.setText(fullText);
            }
        } else {
            text.setText(fullText);
        }

        return view;
    }
}

