package com.team41.boromi.callbacks;


import com.team41.boromi.models.Book;

import java.util.ArrayList;

/**
 * An interface defining callback methods for books
 */
public interface BookCallback {

    void onSuccess(ArrayList<Book> books);

    void onFailure(Exception e);

}
