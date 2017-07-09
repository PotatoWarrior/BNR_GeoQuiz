package com.dmko.geoquiz;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Button;
import android.widget.TextView;

public class CheatActivity extends AppCompatActivity {
    private static final String EXTRA_ANSWER_IS_TRUE = "com.dmko.geoquiz.answer_is_true";
    private static final String EXTRA_ANSWER_SHOWN = "com.dmko.geoquiz.answer_shown";
    private static final String KEY_IS_ANSWER_SHOWN = "answer";
    private boolean mAnswerIsTrue;
    private Button mShowAnswerButton;
    private TextView mAnswerTextView, mApiLeveltextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cheat);

        mAnswerIsTrue = getIntent().getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false);
        mAnswerTextView = (TextView) findViewById(R.id.answer_text_view);
        mShowAnswerButton = (Button) findViewById(R.id.show_answer_button);
        mApiLeveltextView = (TextView) findViewById(R.id.api_text_view);

        if (savedInstanceState != null) {
            if (savedInstanceState.getBoolean(KEY_IS_ANSWER_SHOWN)) updateAnswer();
        }
        mApiLeveltextView.setText(getResources().getString(R.string.api_level_text, Build.VERSION.SDK_INT));

        mShowAnswerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateAnswer();
                if (Build.VERSION.SDK_INT >= 21) {
                    int cx = mShowAnswerButton.getWidth() / 2;
                    int cy = mShowAnswerButton.getHeight() / 2;
                    float radius = mShowAnswerButton.getWidth();
                    Animator anim = ViewAnimationUtils.createCircularReveal(mShowAnswerButton, cx, cy, radius, 0);
                    anim.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            mShowAnswerButton.setVisibility(View.INVISIBLE);
                        }
                    });
                    anim.start();
                } else {
                    mShowAnswerButton.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_IS_ANSWER_SHOWN, !mAnswerTextView.getText().toString().isEmpty());
    }

    private void updateAnswer() {
        if (mAnswerIsTrue) mAnswerTextView.setText(R.string.true_button);
        else mAnswerTextView.setText(R.string.false_button);
        setAnswerShownResult(true);
    }

    private void setAnswerShownResult(boolean answer_shown) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_ANSWER_SHOWN, answer_shown);
        setResult(RESULT_OK, intent);
    }

    public static boolean wasAnswerShown(Intent intent) {
        return intent.getBooleanExtra(EXTRA_ANSWER_SHOWN, false);
    }

    public static Intent newIntent(Context context, boolean answerIsTrue) {
        return new Intent(context, CheatActivity.class).putExtra(EXTRA_ANSWER_IS_TRUE, answerIsTrue);
    }
}
