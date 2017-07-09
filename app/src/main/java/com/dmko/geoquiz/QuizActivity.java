package com.dmko.geoquiz;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class QuizActivity extends AppCompatActivity {
    private Button mTrueButton, mFalseButton, mCheatButton;
    private ImageButton mNextButton, mPrevButton;
    private TextView mQuestionTextView;
    private Question[] mQuestionBank = new Question[]{
            new Question(R.string.question_australia, true),
            new Question(R.string.question_mideast, false),
            new Question(R.string.question_oceans, true)
    };
    private static final String KEY_INDEX = "index";
    private static final String KEY_CHEATED_QUESTIONS = "cheated_questions";
    private int mCurrentIndex = 0;
    private int questionsAnsweredCorrect = 0, questionsAnsweredIncorrect = 0;
    private static final String TAG = "QuizActivity";
    private static final int REQUEST_CODE_CHEAT = 0;
    private ArrayList<Integer> mCheatedQuestions;
    private static final int MAX_CHEAT_COUNT = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        if (savedInstanceState != null) {
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0);
            mCheatedQuestions = savedInstanceState.getIntegerArrayList(KEY_CHEATED_QUESTIONS);
        } else {
            mCheatedQuestions = new ArrayList<>(mQuestionBank.length);
        }

        mTrueButton = (Button) findViewById(R.id.true_button);
        mFalseButton = (Button) findViewById(R.id.false_button);
        mNextButton = (ImageButton) findViewById(R.id.next_button);
        mQuestionTextView = (TextView) findViewById(R.id.question_text_view);
        mPrevButton = (ImageButton) findViewById(R.id.prev_button);
        mCheatButton = (Button) findViewById(R.id.cheat_button);

        updateQuestion();

        mTrueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(true);
                updateQuestion();
            }
        });
        mFalseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(false);
                updateQuestion();
            }
        });
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                updateQuestion();
            }
        });
        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex = mCurrentIndex == 0 ? mQuestionBank.length - 1 : (mCurrentIndex - 1) % mQuestionBank.length;
                updateQuestion();
            }
        });
        mQuestionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                updateQuestion();
            }
        });
        mCheatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = CheatActivity.newIntent(QuizActivity.this, mQuestionBank[mCurrentIndex].isAnswerTrue());
                startActivityForResult(intent, REQUEST_CODE_CHEAT);
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_INDEX, mCurrentIndex);
        outState.putIntegerArrayList(KEY_CHEATED_QUESTIONS, mCheatedQuestions);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) return;
        if (requestCode == REQUEST_CODE_CHEAT) {
            if (data == null) return;
            if (CheatActivity.wasAnswerShown(data) && !mCheatedQuestions.contains(mCurrentIndex)) {
                mCheatedQuestions.add(mCurrentIndex);
            }
        }
    }

    private void updateQuestion() {
        Question question = mQuestionBank[mCurrentIndex];
        mQuestionTextView.setText(question.getTextResId());
        mTrueButton.setEnabled(question.isEnabled());
        mFalseButton.setEnabled(question.isEnabled());
        mCheatButton.setEnabled(mCheatedQuestions.size() < MAX_CHEAT_COUNT);
        mCheatButton.setText(getResources().getString(R.string.cheat_button, MAX_CHEAT_COUNT - mCheatedQuestions.size()));
    }

    private void checkAnswer(boolean userAnswer) {
        Question question = mQuestionBank[mCurrentIndex];
        boolean answerIsTrue = question.isAnswerTrue();
        int messageResId;
        if (mCheatedQuestions.contains(mCurrentIndex)) {
            messageResId = R.string.judgment_toast;
            questionsAnsweredIncorrect++;
        } else if (userAnswer == answerIsTrue) {
            messageResId = R.string.correct_toast;
            questionsAnsweredCorrect++;
        } else {
            messageResId = R.string.incorrect_toast;
            questionsAnsweredIncorrect++;
        }
        question.setEnabled(false);
        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show();
        checkWinCondition();
    }

    private void checkWinCondition() {
        if (questionsAnsweredIncorrect + questionsAnsweredCorrect == mQuestionBank.length) {
            double percentage = (questionsAnsweredCorrect * 1.0 / mQuestionBank.length) * 100;
            Toast toast = Toast.makeText(this, String.format(Locale.US, "Your final score is %d / 100", (int) percentage), Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP, 0, 0);
            toast.show();
        }
    }
}
