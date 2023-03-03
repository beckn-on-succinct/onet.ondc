package in.succinct.beckn.ondc.retail;

import in.succinct.beckn.BreakUp;
import in.succinct.beckn.Quantity;

public class BreakUpElement extends BreakUp.BreakUpElement {
    public BreakUpElement(){
        super();
    }

    @Override
    public boolean isExtendedAttributesDisplayed() {
        return false;
    }

    @Override
    public String getItemId(){
        return get("@ondc/org/item_id");
    }
    @Override
    public void setItemId(String item_id){
        set("@ondc/org/item_id",item_id);
    }

    @Override
    public Quantity getItemQuantity(){
        return get(Quantity.class,"@ondc/org/item_quantity");
    }
    @Override
    public void setItemQuantity(Quantity quantity){
        set("@ondc/org/item_quantity",quantity);
    }

    public BreakUpCategory getTitleType(){
        String s = get("@ondc/org/title_type");
        return s == null ? null : BreakUpCategory.valueOf(s);
    }

    public void setTitleType(BreakUpCategory title_type){
        set("@ondc/org/title_type",title_type == null ? null : title_type.toString());
    }

    @Override
    public BreakUpCategory getType(){
        return getTitleType();
    }
    @Override
    public void setType(BreakUpCategory type){
        setTitleType(type);
    }



}
