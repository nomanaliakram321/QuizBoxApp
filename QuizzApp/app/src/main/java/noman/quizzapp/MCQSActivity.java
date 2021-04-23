package noman.quizzapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.ArrayMap;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.security.AccessController.getContext;
import static noman.quizzapp.SetsActivity.category_id;
import static noman.quizzapp.SetsActivity.setList;
import static noman.quizzapp.SplashActivity.catList;
import static noman.quizzapp.SplashActivity.selected_cat_index;

public class MCQSActivity extends AppCompatActivity implements View.OnClickListener {
ProgressBar progressBar;

private List<Questions> questionsList;
TextView optionA,optionB,optionC,optionD;
TextView count,questions;
TextView timer;
private int questionNumber;
private  int score;
private int setNo;
private FirebaseFirestore firestore;
private Dialog isLoading;

private CountDownTimer countDown;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_m_c_q_s);
        timer=findViewById(R.id.seconds);
        progressBar=findViewById(R.id.progress);
        questions=findViewById(R.id.question);
        count=findViewById(R.id.count);
        optionA=findViewById(R.id.optionA);
        optionB=findViewById(R.id.optionB);
        optionC=findViewById(R.id.optionC);
        optionD=findViewById(R.id.optionD);

        optionA.setOnClickListener(this);
        optionB.setOnClickListener(this);
        optionC.setOnClickListener(this);
        optionD.setOnClickListener(this);

        isLoading=new Dialog(MCQSActivity.this);
        isLoading.setContentView(R.layout.loading_screen);
        isLoading.setCancelable(false);
        isLoading.getWindow().setBackgroundDrawableResource(R.drawable.edittext);
        isLoading.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        isLoading.show();

        questionsList=new ArrayList<>();
        firestore=FirebaseFirestore.getInstance();
        setNo=getIntent().getIntExtra("SETNO",1);
        getQuestionsList();
        score=0;

    }

    private void getQuestionsList()
    {
        firestore.collection("QUIZ").document(catList.get(selected_cat_index).getName())
                .collection(setList.get(setNo)).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        Map<String, QueryDocumentSnapshot> docList = new ArrayMap<>();

                        for(QueryDocumentSnapshot doc : queryDocumentSnapshots)
                        {
                            docList.put(doc.getId(),doc);
                        }

                        QueryDocumentSnapshot quesListDoc  = docList.get("QUESTIONS_LIST");

                        String count = quesListDoc.getString("COUNT");

                        for(int i=0; i < Integer.valueOf(count); i++)
                        {
                            String quesID = quesListDoc.getString("Q" + String.valueOf(i+1) + "_ID");

                            QueryDocumentSnapshot quesDoc = docList.get(quesID);

                           questionsList.add(new Questions(

                                    quesDoc.getString("QUESTION"),
                                    quesDoc.getString("A"),
                                    quesDoc.getString("B"),
                                    quesDoc.getString("C"),
                                    quesDoc.getString("D"),
                                    Integer.valueOf(quesDoc.getString("ANSWER"))
                            ));

                        }

                        setQuestionsList();
                        isLoading.dismiss();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MCQSActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                        isLoading.dismiss();
                    }
                });


    }

    private void setQuestionsList()
    {
        timer.setText(String.valueOf(10));
        questions.setText(questionsList.get(0).getQuestion());
        optionA.setText(questionsList.get(0).getOptionA());
        optionB.setText(questionsList.get(0).getOptionB());
        optionC.setText(questionsList.get(0).getOptionC());
        optionD.setText(questionsList.get(0).getOptionD());
        count.setText(String.valueOf(1)+"/"+String.valueOf(questionsList.size()));
        startTimer();
        questionNumber=0;
    }
    private void startTimer()
    {
         countDown=new CountDownTimer(10000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
               timer.setText(String.valueOf(millisUntilFinished/1000));
                int progress=(int)(millisUntilFinished/1000);
                progressBar.setProgress(progress);
            }


            @Override
            public void onFinish() {
                progressBar.setProgress(0);
                changeQuestion();

            }
        };countDown.start();
    }
    @Override
    public void onClick(View v) {
        int selectedOption=0;
        switch (v.getId())
        {
            case R.id.optionA:
                selectedOption=1;
                break;
             case R.id.optionB:
                 selectedOption=2;
                break;
           case R.id.optionC:
               selectedOption=3;
                break;
        case R.id.optionD:
            selectedOption=4;
                break;
            default:
        }
        countDown.cancel();
        checkAnswer(selectedOption,v);
    }

    private void checkAnswer(int selectedOption,View view) {
        if(selectedOption==questionsList.get(questionNumber).getCorrectAns())
        {
            //Right answer
            ((TextView)view).setTextColor(ColorStateList.valueOf(Color.GREEN));
            ((TextView)view).setBackgroundResource(R.drawable.checkboxeditcorrect);

            ((TextView)view).setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_check_circle_24, 0);

            score++;
        }
        else
        {
            ((TextView)view).setTextColor(ColorStateList.valueOf(Color.RED));
            ((TextView)view).setBackgroundResource(R.drawable.checkboxeditwrong);
            ((TextView)view).setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_cancel_24, 0);

            switch (questionsList.get(questionNumber).getCorrectAns())
            {
                case 1:
                    optionA.setTextColor(ColorStateList.valueOf(Color.GREEN));
                    optionA.setBackgroundResource(R.drawable.checkboxeditcorrect);
                    optionA.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_check_circle_24, 0);

                    break;
                case 2:
                    optionB.setTextColor(ColorStateList.valueOf(Color.GREEN));
                    optionB.setBackgroundResource(R.drawable.checkboxeditcorrect);
                    optionB.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_check_circle_24, 0);

                    break;
                case 3:
                    optionC.setTextColor(ColorStateList.valueOf(Color.GREEN));
                    optionC.setBackgroundResource(R.drawable.checkboxeditcorrect);
                    optionC.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_check_circle_24, 0);

                    break;
                case 4:
                    optionD.setTextColor(ColorStateList.valueOf(Color.GREEN));
                    optionD.setBackgroundResource(R.drawable.checkboxeditcorrect);
                    optionD.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_check_circle_24, 0);

                    break;
            }

            //wrong answer
        }
        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                changeQuestion();
            }
        },2000);

    }

    private void changeQuestion() {
        if(questionNumber<questionsList.size()-1)
        {
            questionNumber++;
            animationOccurance(questions,0,0);
            animationOccurance(optionA,0,1);
            animationOccurance(optionB,0,2);
            animationOccurance(optionC,0,3);
            animationOccurance(optionD,0,4);

            count.setText(String.valueOf(questionNumber+1) + "/" + String.valueOf(questionsList.size()));

            timer.setText(String.valueOf(10));
            startTimer();
        }
        else {
            //got to score activity
             Intent intent=new Intent(MCQSActivity.this,ResultActivity.class);
            intent.putExtra("Score",String.valueOf(score) + "/" + String.valueOf(questionsList.size()));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
           //  MCQSActivity.this.finish();
        }
    }

    private void animationOccurance(View view,int value,int viewNum) {
        view.animate().alpha(value).scaleX(value).scaleY(value).setDuration(500).setStartDelay(100).setInterpolator(new DecelerateInterpolator( )).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                        if(value==0)
                        {
                            switch (viewNum)
                            {
                                case 0:
                                    ( (TextView)view).setText(questionsList.get(questionNumber).getQuestion());
                                    break;
                                    case 1:
                                    ( (TextView)view).setText(questionsList.get(questionNumber).getOptionA());

                                    break;
                                    case 2:
                                    ( (TextView)view).setText(questionsList.get(questionNumber).getOptionB());

                                    break;
                                    case 3:
                                    ( (TextView)view).setText(questionsList.get(questionNumber).getOptionC());
                                    break;

                                    case 4:

                                        ( (TextView)view).setText(questionsList.get(questionNumber).getOptionD());
                                    break;
                            }
                            if (viewNum!=0)
                             ((TextView)view).setTextColor(ColorStateList.valueOf(Color.parseColor("#989898")));
                            ((TextView)view).setBackgroundResource(R.drawable.checkboxedit);
                            ((TextView)view).setCompoundDrawablesWithIntrinsicBounds(0, 0, (R.drawable.ic_baseline_panorama_fish_eye_24), 0);
                            questions.setBackgroundResource(0);
                            questions.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);


                            animationOccurance(view,1,viewNum);
                        }
            }


            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }

        });
    }

    @Override
    public void onBackPressed() {
        countDown.cancel();
        super.onBackPressed();
    }
}