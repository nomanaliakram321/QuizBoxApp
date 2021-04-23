package noman.quizzapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.widget.GridView;
import android.widget.Toolbar;

import static noman.quizzapp.SplashActivity.catList;


public class CategoryActivity extends AppCompatActivity {
GridView categorGridView;
    Toolbar toolbar;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        categorGridView=findViewById(R.id.categoryview);


        CategoryAdapter adapter= new CategoryAdapter(catList);
        categorGridView.setAdapter(adapter);
    }


}