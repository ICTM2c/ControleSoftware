package Database;

import Models.Customer;
import Models.Order;
import Models.Product;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DbCustomer extends Db {
    private static DbCustomer s_dbOrder;

    private DbCustomer() {
    }

    public static DbCustomer Get() {
        if (s_dbOrder == null) {
            s_dbOrder = new DbCustomer();
        }
        return s_dbOrder;
    }

    public Customer getCustomerForOrder(Order order) {
        try {
            PreparedStatement stmt = getConnection().prepareStatement("SELECT c.* FROM `order` o INNER JOIN Customer c ON c.CustomerId = o.CustomerId WHERE o.OrderId = ?");
            stmt.setInt(1, order.getOrderId());
            ResultSet rs = stmt.executeQuery();
            rs.next();
            Customer res = Customer.fromResultSet(rs);

            stmt.close();

            return res;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
