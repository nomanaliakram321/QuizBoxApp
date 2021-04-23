package com.example.adminapp;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.ArrayMap;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adminapp.Model.CategoryModel;
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


public class CategoryActivity extends AppCompatActivity {
    RecyclerView category_recycler;
    private Dialog isLoading,addCategoryDialog;
    private FirebaseFirestore firestore;
    private EditText addfield;
    private FirebaseAuth firebaseAuth;
    private TextView addcategorybtn,addbtn,optionMenu;
    private  CategoryAdapter adapter;
    public static List<CategoryModel> catList=new ArrayList<>();
    public  static int selectedCatIndex=0;

    Toolbar toolbar;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        category_recycler=findViewById(R.id.categoryRecycler);
        addbtn=findViewById(R.id.addbutton);
        firebaseAuth=FirebaseAuth.getInstance();

        optionMenu=findViewById(R.id.optionMenu);


        isLoading=new Dialog(CategoryActivity.this);
        isLoading.setContentView(R.layout.loading_screen);
        isLoading.setCancelable(false);
        isLoading.getWindow().setBackgroundDrawableResource(R.drawable.edittext);
        isLoading.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);

        addCategoryDialog=new Dialog(CategoryActivity.this);
        addCategoryDialog.setContentView(R.layout.add_dialogue_category);
        addCategoryDialog.setCancelable(true);
        addCategoryDialog.getWindow().setBackgroundDrawableResource(R.drawable.edittext);
     //  addCategoryDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        addfield=addCategoryDialog.findViewById(R.id.add_category_name);
        addcategorybtn=addCategoryDialog.findViewById(R.id.add_category_btn);


        firestore=FirebaseFirestore.getInstance();


        addcategorybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(addfield.getText().toString().isEmpty())
                {
                    addfield.setError("Enter Category");
                    return;
                }
                addNewCategory(addfield.getText().toString());
            }
        });
        optionMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            showMenu(v);

            }
        });

        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addfield.getText().clear();
                addCategoryDialog.show();
            }
        });




        LinearLayoutManager layoutManager=new GridLayoutManager(this, 2);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        category_recycler.setLayoutManager(layoutManager);
        loadData();


    }


    private void loadData() {
        isLoading.show();
        catList.clear();
        firestore.collection("QUIZ").document("Categories")
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                {
                    DocumentSnapshot doc=task.getResult();
                    if(doc.exists())
                    {
                        long count=(long)doc.get("COUNT");
                        for(int i=1;i<= count;i++)
                        {
                            String catName=doc.getString("CAT"+String.valueOf(i)+"_NAME");
                            String catId=doc.getString("CAT"+String.valueOf(i)+"_ID");
                            catList.add(new CategoryModel(catId,catName,"0","1"));
                        }
                      adapter= new CategoryAdapter(catList);
                        category_recycler.setAdapter(adapter);

                    }
                    else
                    {
                        Toast.makeText(CategoryActivity.this,"No Category Document Exist ! ",Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
                else
                {
                    Toast.makeText(CategoryActivity.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();


                }
                isLoading.dismiss();
            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void addNewCategory(String categoryName ) {
        addCategoryDialog.dismiss();
        isLoading.show();
        Map<String,Object> catData =new ArrayMap<>();
        catData.put("NAME",categoryName);
        catData.put("SETS",0);
            catData.put("COUNTER","1");
        String doc_id=firestore.collection("QUIZ").document().getId();
        firestore.collection("QUIZ").document(doc_id).set(catData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Map<String,Object>catDoc=new ArrayMap<>();
                catDoc.put("CAT"+String.valueOf(catList.size()+1)+"_NAME",categoryName);
                catDoc.put("CAT"+String.valueOf(catList.size()+1)+"_ID",doc_id);
                catDoc.put("COUNT",catList.size()+1);
                firestore.collection("QUIZ").document("Categories").update(catDoc).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(CategoryActivity.this,"Successfully Add Categry",Toast.LENGTH_SHORT).show();
                        catList.add( new CategoryModel(doc_id,categoryName,"0","1"));
                        adapter.notifyItemInserted(catList.size());
                        isLoading.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CategoryActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                        isLoading.dismiss();
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CategoryActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                isLoading.dismiss();

            }
        });


    }
    private void showMenu(View view)
    {
        PopupMenu popupMenu=new PopupMenu(CategoryActivity.this,view);
        popupMenu.getMenuInflater().inflate(R.menu.menu,popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.signout:
                        FirebaseAuth.getInstance().signOut();

                        startActivity(new Intent(CategoryActivity.this, SignInActivity.class));
                        finishAffinity();
                        return true;
                    case R.id.help:
                        Toast.makeText(CategoryActivity.this, "Help", Toast.LENGTH_SHORT).show();
                        return true;
                    default:
                        return false;
                }
            }
        });
            popupMenu.show();
    }
}