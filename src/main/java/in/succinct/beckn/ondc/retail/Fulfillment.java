package in.succinct.beckn.ondc.retail;

import in.succinct.beckn.Fulfillment.FulfillmentStatus.FulfillmentStatusConvertor;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class Fulfillment extends in.succinct.beckn.Fulfillment {
    public Fulfillment(){
        super();
    }
    @Override
    public boolean isExtendedAttributesDisplayed(){
        return false;
    }

    static final Map<String,FulfillmentType> ONDC_FULFILMENT_TYPES = new HashMap<>(){{
        put("Delivery",FulfillmentType.home_delivery);
        put("Self-Pickup",FulfillmentType.store_pickup);
        put("Delivery and Self-Pickup",FulfillmentType.store_pickup_and_home_delivery);
        put("Return",FulfillmentType.return_to_origin);
        put("Cancel",FulfillmentType.cancel);
    }};
    static final Map<FulfillmentType,String> ONDC_FULFILMENT_VALUES = new HashMap<>(){{
        put(FulfillmentType.home_delivery,"Delivery");
        put(FulfillmentType.store_pickup,"Self-Pickup");
        put(FulfillmentType.store_pickup_and_home_delivery,"Delivery and Self-Pickup");
        put(FulfillmentType.return_to_origin,"Return");
        put(FulfillmentType.cancel,"Cancel");
    }};

    public FulfillmentType getType(){
        String s = get("type");
        return ONDC_FULFILMENT_TYPES.get(s);
    }
    public void setType(FulfillmentType type){
        set("type",ONDC_FULFILMENT_VALUES.get(type));
    }

    @Override
    public String getCategory(){
        return get("@ondc/org/category");
    }
    @Override
    public void setCategory(String category){
        set("@ondc/org/category",category);
    }
    @Override
    public Duration getTAT(){
        String tat = get("@ondc/org/TAT");
        return tat == null ? null : Duration.parse(tat);
    }
    @Override
    public void setTAT(Duration tat){
        set("@ondc/org/TAT",tat == null ? null : tat.toString());
    }


    @Override
    public String getProviderName(){
        return get("@ondc/org/provider_name");
    }
    @Override
    public void setProviderName(String provider_name){
        set("@ondc/org/provider_name",provider_name);
    }


    private static class OndcFulfillmentStatusConvertor extends EnumConvertor<FulfillmentStatus>{
        @Override
        public String toString(FulfillmentStatus value) {
            if (value == FulfillmentStatus.Return_Liquidated){
                return "Liquidated";
            }else {
                return super.toString(value);
            }
        }

        @Override
        public FulfillmentStatus valueOf(String enumRepresentation) {
            if ("Liquidated".equals(enumRepresentation)){
                return FulfillmentStatus.Return_Liquidated;
            }else {
                return super.valueOf(enumRepresentation);
            }
        }
    }
    private static final OndcFulfillmentStatusConvertor convertor = new OndcFulfillmentStatusConvertor();
    @Override
    public EnumConvertor<FulfillmentStatus> getFulfillmentStatusConvertor() {
        return convertor;
    }
}
