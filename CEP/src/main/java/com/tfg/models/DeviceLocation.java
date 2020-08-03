package com.tfg.models;

/**
 * Represents the location of a device
 * 
 * @author Jose Ramon Lozano
 */
public class DeviceLocation {

	// Device ID
	private String _id;
	
	// Device GeoLatitude
	private double geoLat;
	
	// Device GeoLongitude
	private double geoLong;
	
	/**
	 * @param _id the _id to set
	 * @param geoLat the geoLat to set
	 * @param geoLong the geoLong to set
	 */
	public DeviceLocation(String _id, double geoLat, double geoLong) {
		super();
		this._id = _id;
		this.geoLat = geoLat;
		this.geoLong = geoLong;
	}

	/**
	 * @return the _id
	 */
	public String get_id() {
		return _id;
	}

	/**
	 * @param _id the _id to set
	 */
	public void set_id(String _id) {
		this._id = _id;
	}

	/**
	 * @return the geoLat
	 */
	public double getGeoLat() {
		return geoLat;
	}

	/**
	 * @param geoLat the geoLat to set
	 */
	public void setGeoLat(double geoLat) {
		this.geoLat = geoLat;
	}

	/**
	 * @return the geoLong
	 */
	public double getGeoLong() {
		return geoLong;
	}

	/**
	 * @param geoLong the geoLong to set
	 */
	public void setGeoLong(double geoLong) {
		this.geoLong = geoLong;
	}
	

}
