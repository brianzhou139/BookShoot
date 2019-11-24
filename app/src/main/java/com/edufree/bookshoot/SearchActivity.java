package com.edufree.bookshoot;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.edufree.bookshoot.models.Search;
public class SearchActivity extends AppCompatActivity {

    EditText etTitle,etAuthor,etPublisher,etIsbn;
    Button btn_search;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        etTitle=(EditText)findViewById(R.id.etTitle);
        etAuthor=(EditText)findViewById(R.id.etAuthor);
        etPublisher=(EditText)findViewById(R.id.etPublisher);
        etIsbn=(EditText)findViewById(R.id.etIsbn);
        btn_search=(Button)findViewById(R.id.btnSearch);

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title=etTitle.getText().toString().trim();
                String author=etAuthor.getText().toString().trim();
                String publisher=etPublisher.getText().toString().trim();
                String isbn=etIsbn.getText().toString().trim();

                if(title.isEmpty() && author.isEmpty() && publisher.isEmpty() && isbn.isEmpty()){
                    String message="no search term";
                    Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
                    etTitle.setError("fill at least one field");
                    etAuthor.setError("fill at least one field");
                    etPublisher.setError("fill at least one field");
                    etIsbn.setError("fill at least one field");
                    return;
                }

                Search userSearch=new Search(title,author,publisher,isbn);
                Intent goToBookListIntent=new Intent(getApplicationContext(),MainActivity.class);
                goToBookListIntent.putExtra("Query",userSearch);
                startActivity(goToBookListIntent);
            }
        });

    }//end of onCreate............
}
