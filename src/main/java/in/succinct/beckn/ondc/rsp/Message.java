package in.succinct.beckn.ondc.rsp;

import in.succinct.beckn.BecknObject;
import in.succinct.beckn.Order.Orders;
import org.json.simple.JSONObject;

public class Message extends in.succinct.beckn.Message {
    public Message() {
    }

    public Message(String payload) {
        super(payload);
    }

    public void setOrders(Orders orders){
        OrderBook orderBook = getOrderBook();
        if (orderBook == null){
            orderBook = new OrderBook();
            setOrderBook(orderBook);
        }
        orderBook.setOrders(orders);
    }
    public Orders getOrders(){
        return getOrderBook().getOrders();
    }

    public OrderBook getOrderBook(){
        return get(OrderBook.class, "order_book");
    }
    public void setOrderBook(OrderBook order_book){
        set("order_book",order_book);
    }

    public static class OrderBook extends BecknObject{
        public Orders getOrders(){
            return get(Orders.class, "orders");
        }
        public void setOrders(Orders orders){
            set("orders",orders);
        }
    }

}
