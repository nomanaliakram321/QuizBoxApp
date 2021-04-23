package noman.quizzapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class ResultActivity extends AppCompatActivity {
TextView start,score;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        start=findViewById(R.id.start);
        score=findViewById(R.id.score);
        String score_str=getIntent().getStringExtra("Score");
//        int scoreint=Integer.parseInt(score_str.toString());
//        if(scoreint>=3)
//        {
//            "You are excellent"
//        }
        score.setText(score_str);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ResultActivity.this,CategoryActivity.class);
                startActivity(intent);
                ResultActivity.this.finish();
            }
        });
    }
}