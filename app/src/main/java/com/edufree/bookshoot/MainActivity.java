package com.edufree.bookshoot;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.edufree.bookshoot.models.Book;
import com.edufree.bookshoot.models.Comment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private static final String TAG="MAinActivity";
    private ArrayList<Comment> mCommentList=new ArrayList<>();
    private ArrayList<Comment> mBookCommentsList=new ArrayList<>();
    private boolean hasReviews=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_recos, R.id.navigation_favourites)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        //setUp titles yeah include this one
        //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }

    private void writeDataV1(){
        Comment firstComm=new Comment();
        firstComm.setContent("hahaha titus");
        firstComm.setEmail("tito@gmail.com");
        firstComm.setId("lllllllllllllllllllllllllllllll");
        firstComm.setRating("4.5");
        firstComm.setThumbnail("https:myfoto");


        Book v1=new Book();
        v1.setIsbn("11111111111111111");
        //Book v2=new Book();
        //v2.setIsbn("22222222222222222");

        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("books").child(v1.getIsbn());

        //inset testing datat into db ..... heres
        HashMap<String,Object> data=new HashMap<>();
        data.put("content",firstComm.getContent());
        data.put("email",firstComm.getEmail());
        data.put("id",firstComm.getId());
        data.put("rating",firstComm.getRating());
        data.put("thumbnail",firstComm.getThumbnail());

        myRef.child(firstComm.getId()).updateChildren(data);
    }

    private void writeDatav2(){
        Comment firstComm=new Comment();
        firstComm.setContent("hahaha titus");
        firstComm.setEmail("tito@gmail.com");
        firstComm.setId("ddddddddddd");
        firstComm.setRating("4.5");
        firstComm.setThumbnail("https:myfoto");


        //Book v1=new Book();
        //v1.setIsbn("11111111111111111");
        Book v2=new Book();
        v2.setIsbn("22222222222222222222222");

        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("books").child(v2.getIsbn());

        //inset testing datat into db ..... heres
        HashMap<String,Object> data=new HashMap<>();
        data.put("content",firstComm.getContent());
        data.put("email",firstComm.getEmail());
        data.put("id",firstComm.getId());
        data.put("rating",firstComm.getRating());
        data.put("thumbnail",firstComm.getThumbnail());

        myRef.child(firstComm.getId()).updateChildren(data);
    }

    private void loadComments(){
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("books").child("isbn");
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.getValue() == null) {
                    // The child doesn't exist
                    Log.d(TAG,"create new values here yeah ############");
                }else{
                    Toast.makeText(getApplicationContext(),"Fuck you this child exists here",Toast.LENGTH_SHORT).show();
                    Log.d(TAG,"child already exists ************");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void RIP_ALL_COMMENTS() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("comments");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mCommentList.clear();
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Comment comment=snapshot.getValue(Comment.class);
                    if(comment!=null){
                        mCommentList.add(comment);
                    }
                }
                //call JOHN ..its his time now
                //jOhN_thE_RiPpeR(mCommentList,v3);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }//end of readUsers...

    private void jOhN_thE_RiPpeR(ArrayList<Comment> mList,Book book){
        ArrayList<Comment> trimmedList=new ArrayList<>();
        ArrayList<Comment> playingList=mList;

        for(int a=0;a<20;a++){
            Comment chosenComment=playingList.get(new Random().nextInt(playingList.size()));
            trimmedList.add(chosenComment);
            playingList.remove(chosenComment);
        }

        Log.d(TAG,"Finished L getting the new 20 Books yeah");

        for(Comment comment:trimmedList){
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("books").child(book.getIsbn()).child(comment.getId());
            myRef.setValue(comment);
        }
        //getBookComments(book);
    }

    private void getBookComments2(Book book){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("comments").child(book.getIsbn());

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mBookCommentsList.clear();
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Comment comment=snapshot.getValue(Comment.class);
                    if(comment!=null){
                        mBookCommentsList.add(comment);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
