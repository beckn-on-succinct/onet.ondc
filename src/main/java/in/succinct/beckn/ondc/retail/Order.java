package in.succinct.beckn.ondc.retail;

import in.succinct.beckn.Cancellation;

public class Order extends in.succinct.beckn.Order {
    @Override
    public boolean isExtendedAttributesDisplayed(){
        return false;
    }

    public Order() { super(); }

    @Override
    public Cancellation getCancellation(){
        return get(Cancellation.class,"@org/ondc/cancellation");
    }

    @Override
    public void setCancellation(Cancellation cancellation){
        set("@org/ondc/cancellation",cancellation.getInner());
    }


}
