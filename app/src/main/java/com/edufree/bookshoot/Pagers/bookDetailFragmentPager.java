package com.edufree.bookshoot.Pagers;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.edufree.bookshoot.models.Book;
import com.edufree.bookshoot.models.Comment;
import com.edufree.bookshoot.pagerFragments.DetailsFragment;
import com.edufree.bookshoot.pagerFragments.RelatedFragment;
import com.edufree.bookshoot.pagerFragments.ReviewsFragment;
import com.edufree.bookshoot.utils.pConstants;

import java.util.ArrayList;

public class bookDetailFragmentPager extends FragmentPagerAdapter {

    final int PAGE_COUNT = 3;
    private String tabTitles[] = new String[] { "DETAILS", "REVIEWS", "RELATED" };
    private Context mContext;
    private Book book;
    private boolean hasChildCommentNodes;

    public bookDetailFragmentPager(@NonNull FragmentManager fm, Context context, Book book, boolean hasChildCommentNodes) {
        super(fm);
        mContext = context;
        this.book = book;
        this.hasChildCommentNodes = hasChildCommentNodes;
    }

    @Override
    public Fragment getItem(int position) {
        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putParcelable(pConstants.BOOK_FRAGMENT_KEY,book);

        if (position == 0) {
            DetailsFragment details = new DetailsFragment();
            details.setArguments(args);
            return details;
        } else if (position == 1){
            ReviewsFragment reviews=new ReviewsFragment();
            args.putBoolean(pConstants.COMMENTS_KEY,hasChildCommentNodes);
            reviews.setArguments(args);
            return reviews;
        } else {
            RelatedFragment related=new RelatedFragment();
            related.setArguments(args);
            return related;
        }

    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }
}
