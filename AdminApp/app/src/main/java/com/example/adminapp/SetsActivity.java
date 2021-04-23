package com.example.adminapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.ArrayMap;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.example.adminapp.CategoryActivity.catList;
import static com.example.adminapp.CategoryActivity.selectedCatIndex;

public class SetsActivity extends AppCompatActivity {
    private RecyclerView setRecyclerView;
    private TextView addSetsBtn,backbtn,optionMenu;
    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;
    private SetsAdapter adapter;
     private Dialog isLoading,addCategoryDialog;
    public static List<String> setList=new ArrayList<String>();
    public  static int seleted_set_index=0;
    private MenuItem item;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sets);
        setRecyclerView=findViewById(R.id.setRecycler);
        addSetsBtn=findViewById(R.id.addSetbutton);
            optionMenu=findViewById(R.id.optionMenuset);
            backbtn=findViewById(R.id.backbtnset);
        // showing the back button in action bar

        firebaseAuth=FirebaseAuth.getInstance();
        isLoading=new Dialog(SetsActivity.this);
        isLoading.setContentView(R.layout.loading_screen);
        isLoading.setCancelable(false);
        isLoading.getWindow().setBackgroundDrawableResource(R.drawable.edittext);
        isLoading.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);

        addSetsBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {

                addNewSet();

            }
        });
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SetsActivity.this, CategoryActivity.class));
                SetsActivity.this.finish();
            }
        });
        optionMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               showMenu(v);
            }
        });

        firestore=FirebaseFirestore.getInstance();

        LinearLayoutManager layoutManager=new GridLayoutManager(this, 3);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        setRecyclerView.setLayoutManager(layoutManager);
        loadSets();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)


    private void loadSets() {
        setList.clear();
            isLoading.show();


            firestore.collection("QUIZ").document(catList.get(selectedCatIndex).
                    getId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        long noOfsets=(long)documentSnapshot.get("SETS");
                        for (int i=1;i<=noOfsets;i++)
                        {
                            setList.add(documentSnapshot.getString("SET"+String.valueOf(i)+"_ID"));
                        }
                    catList.get(selectedCatIndex).setSetCounter(documentSnapshot.getString("COUNTER"));
                    catList.get(selectedCatIndex).setNoOfSets(String.valueOf(noOfsets));
                    adapter=new SetsAdapter(setList);
                    setRecyclerView.setAdapter(adapter);
                    isLoading.dismiss();

                    }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(SetsActivity.this,e.getMessage().toString(),Toast.LENGTH_SHORT).show();

                    isLoading.dismiss();


                }
            });



    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void addNewSet() {
        isLoading.show();
      final String current_catID=catList.get(selectedCatIndex).getId();
      final String current_counter=catList.get(selectedCatIndex).getSetCounter();
        Map<String,Object> questionData =new ArrayMap<>();
        questionData.put("COUNT","0");
        firestore.collection("QUIZ").document(current_catID).collection(current_counter).
                document("QUESTIONS_LIST").set(questionData).
                addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                Map<String,Object> catDoc =new ArrayMap<>();
                catDoc.put("COUNTER",String.valueOf(Integer.valueOf(current_counter)+1));
                catDoc.put("SET"+String.valueOf(setList.size()+1)+ "_ID",current_counter);
                catDoc.put("SETS",setList.size()+1);
                firestore.collection("QUIZ").document(current_catID).update(catDoc).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(SetsActivity.this,"Set Added Successfully",Toast.LENGTH_SHORT).show();

                        setList.add(current_counter);
                        catList.get(selectedCatIndex).setNoOfSets(String.valueOf(setList.size()));
                        catList.get(selectedCatIndex).setSetCounter(String.valueOf(Integer.valueOf(current_counter) +1));
                        adapter.notifyItemInserted(setList.size());
                        isLoading.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SetsActivity.this,e.getMessage().toString(),Toast.LENGTH_SHORT).show();

                        isLoading.dismiss();
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SetsActivity.this,e.getMessage().toString(),Toast.LENGTH_SHORT).show();

                isLoading.dismiss();
            }
        });


    }
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.signout:

                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(SetsActivity.this,SignInActivity.class));
                finishAffinity();
                return true;
            case R.id.help:

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void showMenu(View view)
    {
        PopupMenu popupMenu=new PopupMenu(SetsActivity.this,view);
        popupMenu.getMenuInflater().inflate(R.menu.menu,popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.signout:
                        FirebaseAuth.getInstance().signOut();

                        startActivity(new Intent(SetsActivity.this, SignInActivity.class));
                        finishAffinity();
                        return true;
                    case R.id.help:
                        Toast.makeText(SetsActivity.this, "Help", Toast.LENGTH_SHORT).show();
                        return true;
                    default:
                        return false;
                }
            }
        });
        popupMenu.show();
    }



}