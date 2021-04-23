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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.List;
import java.util.Map;

import static com.example.adminapp.CategoryActivity.catList;
import static com.example.adminapp.CategoryActivity.selectedCatIndex;

import static com.example.adminapp.SetsActivity.seleted_set_index;

public class SetsAdapter extends RecyclerView.Adapter<SetsAdapter.ViewHolder> {

    private List<String> setList;

    public SetsAdapter(List<String> setList) {
        this.setList = setList;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View  view= LayoutInflater.from(parent.getContext()).inflate(R.layout.set_items,parent,false);
        return new ViewHolder( view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
       String setListId=setList.get(position);
        viewHolder.setData(position,setListId,this);

    }

    @Override
    public int getItemCount() {
        return setList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView setName;
        private ImageView setDeleteBtn;
        private Dialog isLoading;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            setName=itemView.findViewById(R.id.setName);
            setDeleteBtn=itemView.findViewById(R.id.delete_set);

            isLoading=new Dialog(itemView.getContext());
            isLoading.setContentView(R.layout.loading_screen);
            isLoading.setCancelable(false);
            isLoading.getWindow().setBackgroundDrawableResource(R.drawable.edittext);
            isLoading.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        private void setData(int position, final String setListId,final SetsAdapter adapter)
        {
            setName.setText("SET"+String.valueOf(position +1));
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    SetsActivity.selectedCatIndex=position;
                    seleted_set_index=position;
                    Intent intent=new Intent(itemView.getContext(),QuestionsActivity.class);
                    itemView.getContext().startActivity(intent);
                }
            });

            setDeleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog dialog=new AlertDialog.Builder(itemView.getContext()).setTitle("Delete Set")
                            .setMessage("Do you want to delete this  Set ?").setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    deleteSet(position,setListId,itemView.getContext(),adapter);

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

        }
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        private void deleteSet(int position,String setListId,final Context context,final SetsAdapter adapter)
        {
            isLoading.show();
           final FirebaseFirestore firestore=FirebaseFirestore.getInstance();
            firestore.collection("QUIZ").document(catList.get(selectedCatIndex).getId()).collection(setListId).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    WriteBatch batch=firestore.batch();
                    for(QueryDocumentSnapshot documentSnapshot:queryDocumentSnapshots)
                    {
                        batch.delete(documentSnapshot.getReference());
                    }
                    batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            Map<String,Object>catDoc=new ArrayMap<>();
                            int index=1;
                            for(int i=0;i<setList.size();i++)
                            {
                                if(i!=position)
                                {


                                    catDoc.put("SET"+String.valueOf(index)+"_ID",setList.get(i));
                                    index++;
                                }

                            }
                            catDoc.put("SETS",index-1);
                            firestore.collection("QUIZ").document(catList.get(selectedCatIndex)
                                    .getId()).update(catDoc).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(context,"Successfully Deleted",Toast.LENGTH_SHORT).show();
                                    setList.remove(position);

                                    catList.get(selectedCatIndex).setNoOfSets(String.valueOf(setList.size()));
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
