package com.fuckolympus.arc.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.fuckolympus.arc.R;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * Created by alex on 27.7.17.
 */
public class CustomSpinner extends LinearLayout {

    private int minValue = 0;

    private int maxValue = 0;

    private int selectedValue = 0;

    public CustomSpinner(Context context) {
        super(context);
        initializeViews(context);
    }

    public CustomSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        obtainAttributes(context, attrs);
        initializeViews(context);
    }

    private void obtainAttributes(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomSpinner);
        minValue = NumberUtils.toInt(typedArray.getString(R.styleable.CustomSpinner_minValue));
        maxValue = NumberUtils.toInt(typedArray.getString(R.styleable.CustomSpinner_maxValue));
        typedArray.recycle();
    }

    public int getSelectedValue() {
        return selectedValue;
    }

    public void setSelectedValue(int selectedValue) {
        if (selectedValue < minValue || selectedValue > maxValue) {
            return;
        }

        this.selectedValue = selectedValue;

        updateTextView(selectedValue);
    }

    private void updateTextView(int selectedValue) {
        TextView valueText = (TextView) findViewById(R.id.valueText);
        valueText.setText(String.valueOf(selectedValue));
    }

    private void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.custom_spinner, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        updateTextView(selectedValue);

        // todo add specific code
        Button addButton = (Button) findViewById(R.id.addBtn);
        addButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int newValue = selectedValue + 1;
                if (newValue <= maxValue) {
                    selectedValue = newValue;
                } else {
                    selectedValue = minValue;
                }
                updateTextView(selectedValue);
            }
        });

        Button subButton = (Button) findViewById(R.id.subBtn);
        subButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int newValue = selectedValue - 1;
                if (newValue >= minValue) {
                    selectedValue = newValue;
                } else {
                    selectedValue = maxValue;
                }
                updateTextView(selectedValue);
            }
        });
    }
}
