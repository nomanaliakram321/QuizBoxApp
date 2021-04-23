package com.example.adminapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adminapp.Model.CategoryModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Map;

import static com.example.adminapp.CategoryActivity.catList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
private List<CategoryModel>cat_List;

    public CategoryAdapter(List<CategoryModel> cat_List) {
        this.cat_List = cat_List;
    }

    @NonNull
    @Override
    public CategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      View  view= LayoutInflater.from(parent.getContext()).inflate(R.layout.category_items,parent,false);
        return new ViewHolder( view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.ViewHolder viewholder, int position) {
    String title=cat_List.get(position).getName();
    viewholder.setData(title,position,this);
    }

    @Override
    public int getItemCount() {
        return cat_List.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView catName,update;
        private ImageView deletebtn;
        private ImageView editbtn;
        private Dialog isLoading;
        private Dialog editDialog;
        private EditText edit_category_field;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            catName=itemView.findViewById(R.id.catName);
            deletebtn=itemView.findViewById(R.id.delete_category);
            editbtn=itemView.findViewById(R.id.edit_category);
           ;

            isLoading=new Dialog(itemView.getContext());
            isLoading.setContentView(R.layout.loading_screen);
            isLoading.setCancelable(true);
            isLoading.getWindow().setBackgroundDrawableResource(R.drawable.edittext);
            isLoading.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);

            //edit dialog
            editDialog=new Dialog(itemView.getContext());
            editDialog.setContentView(R.layout.edit_dialogue_category);
            editDialog.setCancelable(true);
            editDialog.getWindow().setBackgroundDrawableResource(R.drawable.edittext);
            editDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            edit_category_field= (EditText) editDialog.findViewById(R.id.edit_category_field);
            update=editDialog.findViewById(R.id.update);
        }

        private void setData(String title,int position,CategoryAdapter adapter)
        {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CategoryActivity.selectedCatIndex=position;
                    Intent intent=new Intent(itemView.getContext(),SetsActivity.class);
                    itemView.getContext().startActivity(intent);

                }
            });
            catName.setText(title);
            deletebtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog dialog=new AlertDialog.Builder(itemView.getContext()).setTitle("Delet Category")
                            .setMessage("Do you want to delete this  Category ?").setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    deleteCategory(position,itemView.getContext(),adapter);

                                }
                            }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).show();
                    dialog.getWindow().setBackgroundDrawableResource(R.drawable.edittext);

                    dialog.getButton(dialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#00FFCB"));
                    dialog.getButton(dialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#00FFCB"));
                }



            });
            editbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    edit_category_field.setText(cat_List.get(position).getName());
                    editDialog.show();
                }

            });

            update.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void onClick(View v) {
                    if(edit_category_field.getText().toString().isEmpty())
                    {
                        edit_category_field.setError("Enter Category");
                        return;
                    }
                    updateCategory(edit_category_field.getText().toString(),position,itemView.getContext(),adapter);
                }
            });
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        private void deleteCategory(final int id, Context context,CategoryAdapter adapter) {
        isLoading.show();
            FirebaseFirestore firestore=FirebaseFirestore.getInstance();
            Map<String,Object>catDoc=new ArrayMap<>();
            int index=1;
            for(int i=0;i<cat_List.size();i++)
            {
                if(i!=id)
                {

                    catDoc.put("CAT"+String.valueOf(index)+"_NAME",cat_List.get(i).getName());
                    catDoc.put("CAT"+String.valueOf(index)+"_ID",cat_List.get(i).getId());
                    index++;
                }

            }
            catDoc.put("COUNT",index-1);
            firestore.collection("QUIZ").document("Categories").set(catDoc).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(context,"Successfully Deleted",Toast.LENGTH_SHORT).show();
                    catList.remove(id);
                    adapter.notifyDataSetChanged();
                    isLoading.dismiss();


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(context,e.getMessage(),Toast.LENGTH_SHORT).show();
                    isLoading.dismiss();


                }
            });
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        private void updateCategory(String updateCategoryName,int position,Context context,CategoryAdapter adapter)
        {
            editDialog.dismiss();
            isLoading.show();
            Map<String,Object> catData =new ArrayMap<>();
            catData.put("NAME",updateCategoryName);
            FirebaseFirestore firestore=FirebaseFirestore.getInstance();
            firestore.collection("QUIZ").document(cat_List.get(position).getId())
                    .update(catData).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Map<String,Object> catDoc =new ArrayMap<>();
                    catDoc.put("CAT"+String.valueOf(position+1)+"_NAME",updateCategoryName);

                    firestore.collection("QUIZ").document("Categories").update(catDoc).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(context,"Successfully Updated Categry",Toast.LENGTH_SHORT).show();
                            catList.get(position).setName(updateCategoryName);
                            adapter.notifyDataSetChanged();
                            isLoading.dismiss();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context,e.getMessage(),Toast.LENGTH_SHORT).show();
                            isLoading.dismiss();
                        }
                    });



                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(context,e.getMessage(),Toast.LENGTH_SHORT).show();
                    isLoading.dismiss();
                }
            });
        }
    }
}
