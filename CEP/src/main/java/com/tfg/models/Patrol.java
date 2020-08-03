package com.tfg.models;

/**
 * Represents the location of a device
 * 
 * @author Jose Ramon Lozano
 */
public class Patrol {

	// Patrol location latitude
	private double lat;

	// Patrol location longitude
	private double lng;
	
	

	/**
	 * @param lat the lat to set
	 * @param lng the lng to set
	 */
	public Patrol(double lat, double lng) {
		super();
		this.lat = lat;
		this.lng = lng;
	}

	/**
	 * @return the lat
	 */
	public double getLat() {
		return lat;
	}

	/**
	 * @param lat the lat to set
	 */
	public void setLat(double lat) {
		this.lat = lat;
	}

	/**
	 * @return the lng
	 */
	public double getLng() {
		return lng;
	}

	/**
	 * @param lng the lng to set
	 */
	public void setLng(double lng) {
		this.lng = lng;
	}
	
	
}
