package com.edufree.bookshoot.shitData;

import com.edufree.bookshoot.models.Book;

import java.util.ArrayList;

public class fakeData {
    private fakeData(){};

    public static ArrayList<Book> getShitBooksSamples(){
        ArrayList<Book> mList=new ArrayList<>();

        //URI Harry Potter and the Sorcerer's Stone
        //https://www.googleapis.com/books/v1/volumes?q=flowers&key=AIzaSyC1L5KH0baDylHl9d05bpsq7x8Cpy0s-I0
        //https://www.googleapis.com/books/v1/volumes?q=intitle:The Hobbit&key=AIzaSyC1L5KH0baDylHl9d05bpsq7x8Cpy0s-I0
        String[] a1_auth={"J. K. Rowling"};
        Book a1=new Book();
        a1.setId("gqX7rQEACAAJ");
        a1.setIsbn("0545790352");
        a1.setTitle("Harry Potter and the Sorcerer's Stone");
        a1.setDescription("Rescued from the outrageous neglect of his aunt and uncle, a young boy with a great destiny proves his worth while attending Hogwarts School for Witchcraft and Wizardry.");
        a1.setSubtitle("");
        a1.setPublisher("Arthur A. Levine Books");
        a1.setPublishedDate("2015-10-06");
        a1.setThumbnail("https://books.google.com/books/content?id=gqX7rQEACAAJ&printsec=frontcover&img=1&zoom=1&source=gbs_api");
        a1.setAverageRating("4.5");
        a1.setAuthors(a1_auth);

        String[] a2_auth={"Sarah Graham"};
        Book a2=new Book();
        a2.setId("htdSEMt1ZKAC");
        a2.setIsbn("9781441143105");
        a2.setTitle("Salinger's The Catcher in the Rye");
        a2.setDescription("J. D. Salinger's 1951 novel, The Catcher in the Rye, is the definitive coming-of-age novel and Holden Caulfield remains one of the most famous characters in modern literature. This jargon-free guide to the text sets The Catcher in the Rye in its historical, intellectual and cultural contexts, offering analyses of its themes, style and structure, and presenting an up-to-date account of its critical reception.");
        a2.setSubtitle("");
        a2.setPublisher("A&C Black");
        a2.setPublishedDate("2007-10-25");
        a2.setThumbnail("https://books.google.com/books/content?id=htdSEMt1ZKAC&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api");
        a2.setAverageRating("3.2");
        a2.setAuthors(a2_auth);

        String[] a3_auth={"J.R.R. Tolkien"};
        Book a3=new Book();
        a3.setId("OlCHcjX0RT4C");
        a3.setIsbn("9780544115552");
        a3.setTitle("The Hobbit");
        a3.setDescription("This lavish gift edition of J.R.R. Tolkien's classic features cover art, illustrations, and watercolor paintings by the artist Alan Lee. Bilbo Baggins is a hobbit who enjoys a comfortable, unambitious life, rarely traveling any farther than his pantry or cellar. But his contentment is disturbed when the wizard Gandalf and a company of dwarves arrive on his doorstep one day to whisk him away on an adventure. They have launched a plot to raid the treasure hoard guarded by Smaug the Magnificent, a large and very dangerous dragon. Bilbo reluctantly joins their quest, unaware that on his journey to the Lonely Mountain he will encounter both a magic ring and a frightening creature known as Gollum. Written for J.R.R. Tolkien's own children, The Hobbit has sold many millions of copies worldwide and established itself as a modern classic.");
        a3.setSubtitle("");
        a3.setPublisher("Houghton Mifflin Harcourt");
        a3.setPublishedDate("2012-11-08");
        a3.setThumbnail("https://books.google.com/books/content?id=OlCHcjX0RT4C&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api");
        a3.setAverageRating("4.0");
        a3.setAuthors(a3_auth);

        String[] a4_auth={"Ray Bradbury"};
        Book a4=new Book();
        a4.setId("LelQDAAAQBAJ");
        a4.setIsbn("9782072455148");
        a4.setTitle("Fahrenheit 451");
        a4.setDescription("451 degrees Fahrenheit represents the temperature at which a book burns and is consumed.In this future society where reading, source of questioning and reflection, is considered an antisocial act, a special body of firefighters is Charged with burning all the books, the possession of which is forbidden for the collective good, Montag, the firefighter arsonist, begins however to dream of a different world, which would not ban the literature and the imaginary in the profit of a happiness immediately consumable, he becomes a dangerous criminal, ruthlessly pursued by a society that disavows his past. ");
        a4.setSubtitle("");
        a4.setPublisher("Editions Gallimard");
        a4.setPublishedDate("2016-06-13");
        a4.setThumbnail("https://books.google.com/books/content?id=LelQDAAAQBAJ&printsec=frontcover&img=1&zoom=1&source=gbs_api");
        a4.setAverageRating("4.2");
        a4.setAuthors(a4_auth);

        String[] a5_auth={"Jane Austen"};
        Book a5=new Book();
        a5.setId("alG_Ymyje4UC");
        a5.setIsbn("9781616416959");
        a5.setTitle("Pride and Prejudice");
        a5.setDescription("At the turn of eighteenth-century England, Elizabeth Bennett's spirited copes with the following of the snobbish Mr. Darcy while trying to spell out the romantic entanglements of two of her sisters, sweet and beautiful Jane and scatterbrained Lydia.");
        a5.setSubtitle("");
        a5.setPublisher("ABDO");
        a5.setPublishedDate("2011-09-01");
        a5.setThumbnail("https://books.google.com/books/content?id=alG_Ymyje4UC&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api");
        a5.setAverageRating("4.5");
        a5.setCategory("Fiction");
        a5.setAuthors(a5_auth);

        String[] a6_auth={"Aldous Huxley"};
        Book a6=new Book();
        a6.setId("zyePPwAACAAJ");
        a6.setIsbn("1405882581");
        a6.setTitle("Brave New World");
        a6.setDescription("Huxley's story shows a futuristic World State where all emotion, love, art, and human individuality have been replaced by social stability. An ominous warning to the world's population, this literary classic is a must-read.");
        a6.setSubtitle("");
        a6.setPublisher("Longman");
        a6.setPublishedDate("2009-02-27");
        a6.setThumbnail("https://books.google.com/books/content?id=zyePPwAACAAJ&printsec=frontcover&img=1&zoom=1&source=gbs_api");
        a6.setAverageRating("3.9");
        a6.setCategory("Language");
        a6.setAuthors(a6_auth);

        mList.add(a1);
        mList.add(a2);
        mList.add(a3);
        mList.add(a4);
        mList.add(a5);
        mList.add(a6);

        return mList;

    }

    public static ArrayList<Book> getShitBooksForDB_LIKED(){
        ArrayList<Book> mList=new ArrayList<>();
        String[] a1_auth={"titus","benevolence"};
        Book a1=new Book();
        a1.setId("LIKED__gkdjjhjdhhdh");
        a1.setIsbn("1111111book");
        a1.setTitle("Harry Potter and the Sorcerer's Stone");
        a1.setDescription("great book ..its harry et potter gg");
        a1.setSubtitle("hr");
        a1.setPublisher("titus 86 the publisher");
        a1.setPublishedDate("10-06-2006");
        a1.setThumbnail("https://d9nvuahg4xykp.cloudfront.net/1933636224871900486/141782641845099385.jpg");
        a1.setAverageRating("4.2");
        a1.setAuthors(a1_auth);

        String[] a2_auth={"titus2","benevolence2"};
        Book a2=new Book();
        a2.setId("LIKED__jdhhhh72g7773");
        a2.setIsbn("22222222book");
        a2.setTitle("The Catcher in the Rye");
        a2.setDescription("the catcher yeaaaaaaaaaa");
        a2.setSubtitle("oOps");
        a2.setPublisher("no publisher");
        a2.setPublishedDate("12-9-2004");
        a2.setThumbnail("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQMQMRf4_DJbeugFPgrYPI54T5fz-JE0Wk0Gcu_S3t_3zi4aylr9Q&s");
        a2.setAverageRating("3.2");
        a2.setAuthors(a2_auth);

        String[] a3_auth={"titus3","benevolence3"};
        Book a3=new Book();
        a3.setId("LIKED__hhdgggggsgggg3g4553");
        a3.setIsbn("77777777777book");
        a3.setTitle("A Wrinkle in Time");
        a3.setDescription("a winkles in the sky");
        a3.setSubtitle("oOps");
        a3.setPublisher("tutles badge");
        a3.setPublishedDate("03-18-1999");
        a3.setThumbnail("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcR8qvdP3vysXpidC8vqgGqA5b8H7OIufE5P8_VcHbAItbHUQfQ1Rw&s");
        a3.setAverageRating("2.9");
        a3.setAuthors(a3_auth);

        mList.add(a1);
        mList.add(a2);
        mList.add(a3);

        return mList;
    }

    public static ArrayList<Book> getShitBooksForDB_SEARCHED(){
        ArrayList<Book> mList=new ArrayList<>();
        String[] a1_auth={"titus","benevolence"};
        Book a1=new Book();
        a1.setId("searched_book1");
        a1.setIsbn("1111111book");
        a1.setTitle("Harry Potter and the Sorcerer's Stone");
        a1.setDescription("great book ..its harry et potter gg");
        a1.setSubtitle("hr");
        a1.setPublisher("titus 86 the publisher");
        a1.setPublishedDate("10-06-2006");
        a1.setThumbnail("https://d9nvuahg4xykp.cloudfront.net/1933636224871900486/141782641845099385.jpg");
        a1.setAverageRating("4.2");
        a1.setAuthors(a1_auth);

        String[] a2_auth={"titus2","benevolence2"};
        Book a2=new Book();
        a2.setId("searched_book2");
        a2.setIsbn("22222222book");
        a2.setTitle("The Catcher in the Rye");
        a2.setDescription("the catcher yeaaaaaaaaaa");
        a2.setSubtitle("oOps");
        a2.setPublisher("no publisher");
        a2.setPublishedDate("12-9-2004");
        a2.setThumbnail("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQMQMRf4_DJbeugFPgrYPI54T5fz-JE0Wk0Gcu_S3t_3zi4aylr9Q&s");
        a2.setAverageRating("3.2");
        a2.setAuthors(a2_auth);

        String[] a3_auth={"titus3","benevolence3"};
        Book a3=new Book();
        a3.setId("searched_book3");
        a3.setIsbn("77777777777book");
        a3.setTitle("A Wrinkle in Time");
        a3.setDescription("a winkles in the sky");
        a3.setSubtitle("oOps");
        a3.setPublisher("tutles badge");
        a3.setPublishedDate("03-18-1999");
        a3.setThumbnail("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcR8qvdP3vysXpidC8vqgGqA5b8H7OIufE5P8_VcHbAItbHUQfQ1Rw&s");
        a3.setAverageRating("2.9");
        a3.setAuthors(a3_auth);

        mList.add(a1);
        mList.add(a2);
        mList.add(a3);

        return mList;
    }

    public static ArrayList<Book> getShitBooksForDB_RECOMMENDED(){
        ArrayList<Book> mList=new ArrayList<>();
        String[] a1_auth={"titus","benevolence"};
        Book a1=new Book();
        a1.setId("RECOMMENDED_887736665");
        a1.setIsbn("1111111book");
        a1.setTitle("Harry Potter and the Sorcerer's Stone");
        a1.setDescription("great book ..its harry et potter gg");
        a1.setSubtitle("hr");
        a1.setPublisher("titus 86 the publisher");
        a1.setPublishedDate("10-06-2006");
        a1.setThumbnail("https://d9nvuahg4xykp.cloudfront.net/1933636224871900486/141782641845099385.jpg");
        a1.setAverageRating("4.2");
        a1.setAuthors(a1_auth);

        String[] a2_auth={"titus2","benevolence2"};
        Book a2=new Book();
        a2.setId("RECOMMENDED_97762554555");
        a2.setIsbn("22222222book");
        a2.setTitle("The Catcher in the Rye");
        a2.setDescription("the catcher yeaaaaaaaaaa");
        a2.setSubtitle("oOps");
        a2.setPublisher("no publisher");
        a2.setPublishedDate("12-9-2004");
        a2.setThumbnail("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQMQMRf4_DJbeugFPgrYPI54T5fz-JE0Wk0Gcu_S3t_3zi4aylr9Q&s");
        a2.setAverageRating("3.2");
        a2.setAuthors(a2_auth);

        String[] a3_auth={"titus3","benevolence3"};
        Book a3=new Book();
        a3.setId("RECOMMENDED_878762525636");
        a3.setIsbn("77777777777book");
        a3.setTitle("A Wrinkle in Time");
        a3.setDescription("a winkles in the sky");
        a3.setSubtitle("oOps");
        a3.setPublisher("tutles badge");
        a3.setPublishedDate("03-18-1999");
        a3.setThumbnail("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcR8qvdP3vysXpidC8vqgGqA5b8H7OIufE5P8_VcHbAItbHUQfQ1Rw&s");
        a3.setAverageRating("2.9");
        a3.setAuthors(a3_auth);

        mList.add(a1);
        mList.add(a2);
        mList.add(a3);

        return mList;
    }


}
