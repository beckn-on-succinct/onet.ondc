package in.succinct.beckn.ondc.retail;

import in.succinct.beckn.Provider.ServiceablityTags;

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
        put("Pickup",FulfillmentType.store_pickup);
        put("Delivery and Pickup",FulfillmentType.store_pickup_and_home_delivery);
        put("Reverse QC",FulfillmentType.return_to_origin);
    }};
    static final Map<FulfillmentType,String> ONDC_FULFILMENT_VALUES = new HashMap<>(){{
        put(FulfillmentType.home_delivery,"Delivery");
        put(FulfillmentType.store_pickup,"Pickup");
        put(FulfillmentType.store_pickup_and_home_delivery,"Delivery and Pickup");
        put(FulfillmentType.return_to_origin,"Reverse QC");
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


}
