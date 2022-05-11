package com.example.calculator;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    /*
    TODO: сделать вывод сообщений об ошибках на экран
    TODO: запретить стирание результата
    TODO: ограничить количество знаков в некоторых ситуациях
    TODO: доделать дизайн
    TODO: добавить функций
     */

    private final String TAG = "RPNCalcMainActivity";

    private final int NUM_OF_ROWS = 6;
    private final int NUM_OF_COLUMNS = 4;

    private TextView tvCalculate;
    private EditText etForInputNum;
    private LinearLayout linLayoutVertical;
    private LinearLayout[] linLayoutsHorizontal;

    private String calcString = "";
    private boolean isNum = true;
    private boolean textSizeIsChange = false;

    private RPN rpn;

    private Map<Integer, Button> buttons;
    private String[] textForButtons = {"%", "CE", "C", "<=",
            "1/x", "^2", "√", "/",
            "7", "8", "9", "*",
            "4", "5", "6", "-",
            "1", "2", "3", "+",
            "+/-", "0", ",", "="};

    private String separator = ",";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rpn = new RPN();

        tvCalculate = findViewById(R.id.tvCalculate);
        etForInputNum = findViewById(R.id.etForInputNum);
        linLayoutVertical = findViewById(R.id.linLayoutVertical);

        etForInputNum.setShowSoftInputOnFocus(false);

        linLayoutsHorizontal = new LinearLayout[NUM_OF_ROWS];

        buttons = new HashMap<>(NUM_OF_COLUMNS * NUM_OF_ROWS);

        initButtons();
    }

    @SuppressLint("ResourceType")
    private void initButtons() {
        int count = 0;
        for (LinearLayout linLayoutHorizontal : linLayoutsHorizontal) {
            linLayoutHorizontal = new LinearLayout(this);
            linLayoutHorizontal.setOrientation(LinearLayout.HORIZONTAL);
            linLayoutHorizontal.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1));

            linLayoutVertical.addView(linLayoutHorizontal);

            for (int i = 0; i < NUM_OF_COLUMNS; i++) {
                Button btn = new Button(this);
                btn.setText(textForButtons[count++]);
                btn.setOnClickListener(this);
                btn.setId(View.generateViewId());
                btn.setBackgroundResource(R.drawable.mystyle);
                btn.setTextSize(25);
                btn.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                btn.setGravity(Gravity.CENTER_VERTICAL);
                btn.setLayoutParams(new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1));

                linLayoutHorizontal.addView(btn);
                buttons.put(btn.getId(), btn);
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void result() {
        try {
            if (!isNumeric(String.valueOf(calcString.charAt(calcString.length() - 1)))) {
                calcString += etForInputNum.getText().toString();
            }
            String result = String.valueOf(rpn.calculateExpression(calcString));
            tvCalculate.setText(calcString + "=");
            calcString = "";
            clearInputField();
            addSymToInput(round(result));
            isNum = true;
            changeSize();
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
    }

    private String round(String res) {
        DecimalFormat df;

        String[] expStr = res.split("E");
        int exp = 0;
        if (expStr.length > 1) exp = Integer.parseInt(expStr[1]);

        if (exp <= 14) {
            df = new DecimalFormat("#.#############");
            df.setRoundingMode(RoundingMode.CEILING);
        } else {
            df = new DecimalFormat("0.0E0");
            df.setRoundingMode(RoundingMode.HALF_UP);
            df.setMinimumFractionDigits(9);
        }

        return df.format(Double.parseDouble(res));
    }

    private boolean unary;

    private void addSym(String sym) {

        if (sym.equals(separator) && inputIsContains(separator)) return;

        if (isNumeric(sym) || sym.equals(separator)) {
            if (!isNum) clearInputField();
            addSymToInput(sym);
            isNum = true;
            unary = false;
        } else {
            String num = etForInputNum.getText().toString();
            if (isUnary(sym)) {
                calcString += sym + num;
                isNum = true;
                unary = true;
            } else {
                if (!isNum) {
                    calcString = calcString.substring(0, calcString.length() - 1) + sym;
                } else {
                    if (!unary) calcString += num;
                    calcString += sym;
                }
                isNum = false;
                unary = false;
            }

            tvCalculate.setText(calcString);
        }

    }

    private boolean isUnary(String sym) {
        return sym.equals("√");
    }

    private boolean inputIsContains(String sym) {
        return etForInputNum.getText().toString().contains(sym);
    }


    @Override
    public void onClick(View view) {

        if (buttons.containsKey(view.getId())) {
            if (Objects.requireNonNull(buttons.get(view.getId())).getText().equals("CE")) clearInputField();
            else if (Objects.requireNonNull(buttons.get(view.getId())).getText().equals("C")) clearAllFields();
            else if (Objects.requireNonNull(buttons.get(view.getId())).getText().equals("<=")) eraseSym();
            else if (Objects.requireNonNull(buttons.get(view.getId())).getText().equals("+/-")) negative();
            else if (Objects.requireNonNull(buttons.get(view.getId())).getText().equals("^2")) square();
            else if (Objects.requireNonNull(buttons.get(view.getId())).getText().equals("=")) result();
            else if (Objects.requireNonNull(buttons.get(view.getId())).getText().equals("1/x")) recNum();
            else {
                addSym(Objects.requireNonNull(buttons.get(view.getId())).getText().toString());
            }
        }
    }

    private void negative() {
        if (!(inputIsContains("-") || etForInputNum.getText().toString().equals("0")))
            if (isNum) {
                String num = etForInputNum.getText().toString();
                clearInputField();
                addSymToInput("-" + num);
            }
    }

    private void square() {
        addSym("^");
        addSym("2");
    }

    private void recNum() {
        calcString += "1/";
        calcString += etForInputNum.getText().toString();
        tvCalculate.setText(calcString);
        isNum = true;
        unary = true;
    }


    private void clearAllFields() {
        clearInputField();
        if (!tvCalculate.getText().toString().isEmpty()) tvCalculate.setText("");
        if (!calcString.isEmpty()) calcString = "";
    }

    private void clearInputField() {
        if (!etForInputNum.getText().toString().equals("0")) {
            etForInputNum.getText().clear();
        }
        addSymToInput("0");
        changeSize();
    }

    private void eraseSym() {
        if (isNum) {
            etForInputNum.getText().delete(etForInputNum.length() - 1, etForInputNum.length());
            if (etForInputNum.getText().toString().isEmpty()) addSymToInput("0");

            if (!calcString.equals(""))
                calcString = calcString.substring(0, calcString.length() - 1);
            changeSize();
        }
    }

    private void addSymToInput(String sym) {
        if (!sym.equals(separator)) {
            if (etForInputNum.getText().toString().equals("0")) etForInputNum.setText("");
        }

        if (etForInputNum.length() <= 14) {
            etForInputNum.append(sym);
            changeSize();
        } else
            Toast.makeText(this, "Невозможно ввести болеее 15 символов", Toast.LENGTH_SHORT).show();
    }

    private boolean isNumeric(String sym) {
        try {
            Double.parseDouble(sym);
            return true;
        } catch (Exception e){
            return false;
        }
    }

    private final int stepOfChangeSizeText = 25;


    private void changeSize() {
        if (etForInputNum.length() > 9 && !textSizeIsChange) {
            etForInputNum.setTextSize(TypedValue.COMPLEX_UNIT_SP,
                    (etForInputNum.getTextSize()/getResources().getDisplayMetrics().scaledDensity) - stepOfChangeSizeText);
            textSizeIsChange = true;
        }
        if (textSizeIsChange && etForInputNum.length() <= 9) {
            etForInputNum.setTextSize(TypedValue.COMPLEX_UNIT_SP,
                    (etForInputNum.getTextSize()/getResources().getDisplayMetrics().scaledDensity) + stepOfChangeSizeText);
            textSizeIsChange = false;
        }
    }
}