package com.unex.tfg.data.remote;

public class DeviceIDResponse {

    // ID of the device
    private String _id;

    /**
     * Return the Device ID of the response
     * @return ID of the Device
     */
    public String get_id() {
        return _id;
    }

    /**
     * Set the ID of the response
     * @param _id ID of the Device
     */
    @SuppressWarnings("unused")
    public void set_id(String _id) {
        this._id = _id;
    }
}
