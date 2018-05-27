package Models;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Address {
    private int _addressId;
    private String _address;
    private String _city;
    private String _postalCode;

    public Address(int addressId, String address, String city, String postalCode) {
        _addressId = addressId;
        _address = address;
        _city = city;
        _postalCode = postalCode;
    }

    public static Address fromResultSet(ResultSet rs) throws SQLException {
        return new Address(rs.getInt("AddressId"), rs.getString("Address"), rs.getString("City"), rs.getString("PostalCode"));
    }

    public String getAddress() {
        return _address;
    }

    public String getCity() {
        return _city;
    }

    public String getPostalCode() {
        return _postalCode;
    }
}
