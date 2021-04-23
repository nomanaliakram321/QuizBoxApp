package com.example.adminapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.util.ArrayMap;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adminapp.Model.QuestionModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

import static com.example.adminapp.CategoryActivity.catList;
import static com.example.adminapp.CategoryActivity.selectedCatIndex;
import static com.example.adminapp.QuestionsActivity.questionList;
import static com.example.adminapp.SetsActivity.seleted_set_index;
import static com.example.adminapp.SetsActivity.setList;

public class MCQActivity extends AppCompatActivity {
    private EditText optionA,optionB,optionC,optionD,question,answer;
    private TextView addMcq,action_bar_text;
    private FirebaseFirestore firestore;
    private int qID;

    private  String action;
    private Dialog isLoading;
    private String stroptionA,stroptionB,stroptionC,stroptionD,strquestion,stringanswer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_m_c_q);
        optionA=findViewById(R.id.optionA);
        optionB=findViewById(R.id.optionB);
        optionC=findViewById(R.id.optionC);
        optionD=findViewById(R.id.optionD);
        answer=findViewById(R.id.answer);
        action_bar_text=findViewById(R.id.action_bar_text);
        question=findViewById(R.id.questionDescription);
        addMcq=findViewById(R.id.addMCQbutton);
        isLoading=new Dialog(MCQActivity.this);
        isLoading.setContentView(R.layout.loading_screen);
        isLoading.setCancelable(false);
        isLoading.getWindow().setBackgroundDrawableResource(R.drawable.edittext);
        isLoading.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        firestore=FirebaseFirestore.getInstance();


        action = getIntent().getStringExtra("ACTION");

        if(action.compareTo("EDIT") == 0)
        {
            qID = getIntent().getIntExtra("Q_ID",0);
            loadData(qID);
            action_bar_text.setText("Question " + String.valueOf(qID + 1));
            addMcq.setText("UPDATE");
        }
        else
        {
            action_bar_text.setText("Question " + String.valueOf(questionList.size() + 1));
            addMcq.setText("ADD");
        }

        addMcq.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                strquestion=question.getText().toString();
                stroptionA=optionA.getText().toString();
                stroptionB=optionB.getText().toString();
                stroptionC=optionC.getText().toString();
                stroptionD=optionD.getText().toString();
                stringanswer=answer.getText().toString();


                if (strquestion.isEmpty())
                {
                    question.setError("Enter Question Please");
                }
                if (stroptionA.isEmpty())
                {
                    optionA.setError("Enter Option A Please");
                }  if (stroptionB.isEmpty())
                {
                    optionB.setError("Enter Option B Please");
                }  if (stroptionC.isEmpty())
                {
                    optionC.setError("Enter Option C Please");
                }  if (stroptionD.isEmpty())
                {
                    optionD.setError("Enter Option D Please");
                }  if (stringanswer.isEmpty())
                {
                    answer.setError("Enter Answer Please");
                }
                if(action.compareTo("EDIT") == 0)
                {
                    editQuestion();
                }
                else {
                    addnewMcq();
                }


            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void addnewMcq() {
        isLoading.show();
        Map<String,Object> quesData=new ArrayMap<>();
        quesData.put("QUESTION",strquestion);
        quesData.put("A",stroptionA);
        quesData.put("B",stroptionB);
        quesData.put("C",stroptionC);
        quesData.put("D",stroptionD);
        quesData.put("ANSWER",stringanswer);

       final String doc_id=firestore.collection("QUIZ").document(catList.get(selectedCatIndex).getId())
                .collection(setList.get(seleted_set_index)).document().getId();
        firestore.collection("QUIZ").document(catList.get(selectedCatIndex).getId())
                .collection(setList.get(seleted_set_index)).document(doc_id).set(quesData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Map<String,Object> quesDoc=new ArrayMap<>();
                quesDoc.put("Q"+String.valueOf(questionList.size() + 1)+"_ID",doc_id);
                quesDoc.put("COUNT",String.valueOf(questionList.size() +1));
                firestore.collection("QUIZ").document(catList.get(selectedCatIndex).getId())
                        .collection(setList.get(seleted_set_index)).document("QUESTIONS_LIST").update(quesDoc).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(MCQActivity.this,"Question Added Successfully",Toast.LENGTH_SHORT).show();
                        questionList.add(new QuestionModel(
                                doc_id,strquestion,
                                stroptionA,
                                  stroptionB,
                                stroptionC,
                                stroptionD,
                                Integer.valueOf(stringanswer)
                        ));
                        isLoading.dismiss();
                        MCQActivity.this.finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MCQActivity.this,e.getMessage().toString(),Toast.LENGTH_SHORT).show();
                        isLoading.dismiss();

                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MCQActivity.this,e.getMessage().toString(),Toast.LENGTH_SHORT).show();

            }
        });


    }
    private void loadData(int id)
    {
        question.setText(questionList.get(id).getQuetion());
        optionA.setText(questionList.get(id).getOptionA());
        optionB.setText(questionList.get(id).getOptionB());
        optionC.setText(questionList.get(id).getOptionC());
        optionD.setText(questionList.get(id).getOptionD());
        answer.setText(String.valueOf(questionList.get(id).getCorrectAnswer()));
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void editQuestion()
    {
        isLoading.show();

        Map<String,Object> quesData = new ArrayMap<>();
        quesData.put("QUESTION", strquestion);
        quesData.put("A",stroptionA);
        quesData.put("B",stroptionB);
        quesData.put("C",stroptionC);
        quesData.put("D",stroptionD);
        quesData.put("ANSWER",stringanswer);


        firestore.collection("QUIZ").document(catList.get(selectedCatIndex).getId())
                .collection(setList.get(seleted_set_index)).document(questionList.get(qID).getQuetionId())
                .set(quesData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Toast.makeText(MCQActivity.this,"Question updated successfully",Toast.LENGTH_SHORT).show();

                        questionList.get(qID).setQuetion(strquestion);
                        questionList.get(qID).setOptionA(stroptionA);
                        questionList.get(qID).setOptionB(stroptionB);
                        questionList.get(qID).setOptionC(stroptionC);
                        questionList.get(qID).setOptionD(stroptionD);
                        questionList.get(qID).setCorrectAnswer(Integer.valueOf(stringanswer));

                        isLoading.dismiss();
                        MCQActivity.this.finish();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MCQActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                        isLoading.dismiss();
                    }
                });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home)
        {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

}