package com.tfg.models;

/**
 * Represents the location of a device
 * 
 * @author Jose Ramon Lozano
 */
public class News {

	// News item title
	private String title;

	// News item date
	private String date;
	
	// News item author
	private String author;
	
	// News item description
	private String description;
	
	// News item location
	private String location;
	
	// News item expansion
	private String expansion;
	
	// News item image
	private String image;
	
	// News item relevance
	private int relevance;

	/**
	 * @param title the title to set
	 * @param date the date to set
	 * @param author the author to set
	 * @param description the description to set
	 * @param location the location to set
	 * @param expansion the expansion to set
	 * @param image the image to set
	 * @param relevance the relevance to set
	 */
	public News(String title, String date, String author, String description, String location, String expansion,
			String image, int relevance) {
		super();
		this.title = title;
		this.date = date;
		this.author = author;
		this.description = description;
		this.location = location;
		this.expansion = expansion;
		this.image = image;
		this.relevance = relevance;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the date
	 */
	public String getDate() {
		return date;
	}

	/**
	 * @param date the date to set
	 */
	public void setDate(String date) {
		this.date = date;
	}

	/**
	 * @return the author
	 */
	public String getAuthor() {
		return author;
	}

	/**
	 * @param author the author to set
	 */
	public void setAuthor(String author) {
		this.author = author;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the location
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * @param location the location to set
	 */
	public void setLocation(String location) {
		this.location = location;
	}

	/**
	 * @return the expansion
	 */
	public String getExpansion() {
		return expansion;
	}

	/**
	 * @param expansion the expansion to set
	 */
	public void setExpansion(String expansion) {
		this.expansion = expansion;
	}

	/**
	 * @return the image
	 */
	public String getImage() {
		return image;
	}

	/**
	 * @param image the image to set
	 */
	public void setImage(String image) {
		this.image = image;
	}

	/**
	 * @return the relevance
	 */
	public int getRelevance() {
		return relevance;
	}

	/**
	 * @param relevance the relevance to set
	 */
	public void setRelevance(int relevance) {
		this.relevance = relevance;
	}


}
