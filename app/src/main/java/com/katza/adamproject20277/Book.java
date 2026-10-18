
package com.katza.adamproject20277;

public class Book {
    private String bookName;
    private String author;
    private String dateAdded;

    // בנאי ריק (חובה עבור Firebase)
    public Book() {
    }

    // בנאי מלא
    public Book(String bookName, String author, String dateAdded) {
        this.bookName = bookName;
        this.author = author;
        this.dateAdded = dateAdded;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(String dateAdded) {
        this.dateAdded = dateAdded;
    }

    @Override
    public String toString() {
        return "Book{" +
                "bookName='" + bookName + '\'' +
                ", author='" + author + '\'' +
                ", dateAdded='" + dateAdded + '\'' +
                '}';
    }
}
