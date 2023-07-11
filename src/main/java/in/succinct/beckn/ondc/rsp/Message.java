package in.succinct.beckn.ondc.rsp;

import in.succinct.beckn.Order.Orders;

public class Message extends in.succinct.beckn.Message {
    public Message() {
    }

    public Message(String payload) {
        super(payload);
    }

    public Orders getOrders(){
        return getExtendedAttributes().get(Orders.class, "order_book");
    }
    public void setOrders(Orders orders){
        getExtendedAttributes().set("order_book",orders);
    }

}
