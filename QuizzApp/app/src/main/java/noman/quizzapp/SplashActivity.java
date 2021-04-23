package noman.quizzapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.DoubleBounce;
import com.github.ybq.android.spinkit.style.FadingCircle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.List;

import noman.quizzapp.models.CategoryModel;

import static java.lang.Thread.sleep;

public class SplashActivity extends AppCompatActivity {

TextView logo;
public static List<CategoryModel> catList=new ArrayList<>();
public static int selected_cat_index=0;
private FirebaseFirestore firestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        logo=findViewById(R.id.logo);
        Animation animation= AnimationUtils.loadAnimation(this,R.anim.splashanimantion);
        logo.setAnimation(animation);
        firestore=FirebaseFirestore.getInstance();
        new Thread(new Runnable() {
            @Override
            public void run() {
//                try {
//                    sleep(5000);
                    loadData();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }

            }
        }).start();


    }

    private void loadData() {
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
                            String catID=doc.getString("CAT"+String.valueOf(i)+"_ID");
                            catList.add(new CategoryModel(catName,catID));
                        }
                        Intent intent=new Intent(SplashActivity.this,MainActivity.class);
                        startActivity(intent);
                        SplashActivity.this.finish();
                    }
                    else
                    {
                        Toast.makeText(SplashActivity.this,"No Category Document Exist ! ",Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
                else
                {
                    Toast.makeText(SplashActivity.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();

                }
            }
        });
    }
}