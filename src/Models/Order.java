package Models;

import Database.DbAddress;
import Database.DbCustomer;

public class Order {
    private int OrderId;
    private int CustomerId;
    private int DeliveryAddressId;

    public int getOrderId() {
        return OrderId;
    }
    public int getAddressId() {return DeliveryAddressId;}

    public Customer getCustomer() {
        return DbCustomer.Get().getCustomerForOrder(this);
    }

    public Models.Address getAddress() {
        return DbAddress.Get().getAddressFromId(DeliveryAddressId);
    }
}
