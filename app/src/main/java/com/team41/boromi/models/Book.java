package com.team41.boromi.models;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.UUID;

import static com.team41.boromi.constants.CommonConstants.BookStatus;
import static com.team41.boromi.constants.CommonConstants.BookWorkflowStage;

/** A model class storing an information on the book And on which stage the book is currently in */
public class Book implements Serializable {
  @NonNull private UUID bookId;
  @NonNull private String owner;
  private String ISBN;
  private String author;
  private String desc;
  private BookStatus status;
  private BookWorkflowStage workflow;

  // TODO
  // I didn't include logic for the requesters/borrowers/img/location
  // To allow the person implementing that logic to decide how they want to do it.
  // You can go through the BookRequests, then only do a read on DB
  // If a user wants more info on a book, or you can store all the requester usernames here..
  // And wipe them on each acceptance.

  /**
   * When UUID not specified, we generate a random UUID to identify the book
   *
   * @param owner
   */
  public Book(String owner) {
    this.bookId = UUID.randomUUID();
    this.owner = owner;
    this.status = BookStatus.AVAILABLE;
    this.workflow = BookWorkflowStage.AVAILABLE;
  }

  public Book(String owner, UUID uuid) {
    this.bookId = uuid;
    this.owner = owner;
    this.status = BookStatus.AVAILABLE;
    this.workflow = BookWorkflowStage.AVAILABLE;
  }

  // Setters / Getters Start
  @NonNull
  public UUID getBookId() {
    return bookId;
  }

  @NonNull
  public String getOwner() {
    return owner;
  }

  public String getISBN() {
    return ISBN;
  }

  public void setISBN(String ISBN) {
    this.ISBN = ISBN;
  }

  public String getAuthor() {
    return author;
  }

  public void setAuthor(String author) {
    this.author = author;
  }

  public String getDesc() {
    return desc;
  }

  public void setDesc(String desc) {
    this.desc = desc;
  }

  public BookStatus getStatus() {
    return status;
  }

  public void setStatus(BookStatus status) {
    this.status = status;
  }

  public BookWorkflowStage getWorkflow() {
    return workflow;
  }

  public void setWorkflow(BookWorkflowStage workflow) {
    this.workflow = workflow;
  }
}
