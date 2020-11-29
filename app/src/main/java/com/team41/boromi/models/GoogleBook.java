package com.team41.boromi.models;

/**
 * A holder class for api call
 */
public class GoogleBook {

  public String kind;
  public int totalItems;
  public Item[] items;

  private class Item {
    public VolumeInfo volumeInfo;
  }

  private class VolumeInfo {
    public String title;
    public String[] authors;
  }

  /**
   * Gets the title of the book
   * @param idx
   * @return
   */
  public String getTitle(int idx) {
    if(idx >= totalItems || idx < 0)
      return null;

    return items[0].volumeInfo.title;
  }

  /**
   * Gets the first author from the api
   * @param idx
   * @return Author's name
   */
  public String getFirstAuthor(int idx) {
    if(idx >= totalItems || idx < 0)
      return null;

    else if(items[0].volumeInfo == null || items[0].volumeInfo.authors == null || items[0].volumeInfo.authors.length <= 0)
      return null;

    return items[0].volumeInfo.authors[0];
  }

}
