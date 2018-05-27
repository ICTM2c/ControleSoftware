package Database;

import Models.Address;
import Models.Customer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DbAddress extends Db {
    private static DbAddress s_dbOrder;

    private DbAddress() {
    }

    public static DbAddress Get() {
        if (s_dbOrder == null) {
            s_dbOrder = new DbAddress();
        }
        return s_dbOrder;
    }

    public Address getAddressFromId(int deliveryAddressId) {
        try {
            PreparedStatement stmt = getConnection().prepareStatement("SELECT * FROM Address WHERE AddressId = ?");
            stmt.setInt(1, deliveryAddressId);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            Address res = Address.fromResultSet(rs);

            stmt.close();

            return res;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
