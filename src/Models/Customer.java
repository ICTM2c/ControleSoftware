package Models;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Customer {
    private int _customerId;
    private String _company;
    private String _name;
    private String _telephoneNumber;
    private String _email;

    public Customer(int customerId, String name, String telephoneNumber, String email, String company) {
        _customerId = customerId;
        _name = name;
        _email = email;
        _telephoneNumber = telephoneNumber;
        _company = company;
    }
    public static Customer fromResultSet(ResultSet rs) throws SQLException {
        Customer customer = new Customer(
                rs.getInt("CustomerId"),
                rs.getString("Name"),
                rs.getString("TelephoneNumber"),
                rs.getString("Email"),
                rs.getString("Company")
        );
        return customer;
    }

    public String getName() {
        return _name;
    }
}
