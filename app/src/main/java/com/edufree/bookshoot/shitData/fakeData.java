package com.edufree.bookshoot.shitData;

import com.edufree.bookshoot.models.Book;

import java.util.ArrayList;

public class fakeData {
    private fakeData(){};

    public static ArrayList<Book> getShitBooksSamples(){
        ArrayList<Book> mList=new ArrayList<>();
        mList.add(new Book("1111111book","Harry Potter and the Sorcerer's Stone","https://d9nvuahg4xykp.cloudfront.net/1933636224871900486/141782641845099385.jpg","3"));
        mList.add(new Book("22222222book","The Catcher in the Rye","https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQMQMRf4_DJbeugFPgrYPI54T5fz-JE0Wk0Gcu_S3t_3zi4aylr9Q&s","2"));
        mList.add(new Book("333333333book","The Hobbit","https://cp-img-prod-ssl.akamaized.net/474w/mgm/HOBBITANUNEXPECTEDJOURNEYY2012M/HOBBITANUNEXPECTEDJOURNEYY2012M-474x677-PST.jpg","5"));
        mList.add(new Book("444444444book","Fahrenheit 451","https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQH4kAzzHiVajEcBX1bHcD4A-Ell9QjFUBiFosrE4BIC3JF2hPR&s","1.5"));
        mList.add(new Book("55555555book","Pride and Prejudice","https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRfvsAzg9nikd9NdQG4Gu_-2rQNHbAzDXZencbubt9Q0qvp0vz6nw&s","4.7"));
        mList.add(new Book("666666666book","Brave New World","https://i.gr-assets.com/images/S/compressed.photo.goodreads.com/books/1510240733i/36564552.jpg","4"));
        mList.add(new Book("77777777777book","A Wrinkle in Time","https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcR8qvdP3vysXpidC8vqgGqA5b8H7OIufE5P8_VcHbAItbHUQfQ1Rw&s","3.4"));
        return mList;

    }


}
