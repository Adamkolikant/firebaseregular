package com.katza.adamproject20277;

public class Book {
    // הסרנו את @DocumentId מכאן כדי שלא תהיה התנגשות עם שדה ה-id שמגיע מהמסמך
    private String id;
    private String bookName;
    private String author;
    private String dateAdded;

    public Book() {
    }

    public Book(String id, String bookName, String author, String dateAdded) {
        this.id = id;
        this.bookName = bookName;
        this.author = author;
        this.dateAdded = dateAdded;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
        return "ID: " + id + " | ספר: " + bookName + " | מחבר: " + author + " | תאריך: " + dateAdded;
    }
}