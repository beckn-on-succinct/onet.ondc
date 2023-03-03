package in.succinct.beckn.ondc.retail;

import org.json.simple.JSONObject;

import java.time.Duration;

public class Item extends in.succinct.beckn.Item {
    @Override
    public boolean isExtendedAttributesDisplayed(){
        return false;
    }

    public Item() {
        super();
    }

    public Item(JSONObject item) {
        super(item);
    }


    @Override
    public boolean isReturnable() {
        return getBoolean("@ondc/org/returnable");
    }
    @Override
    public void setReturnable(boolean returnable) {
        set("@ondc/org/returnable", returnable);
    }
    @Override
    public boolean isSellerPickupReturn() {
        return getBoolean("@ondc/org/seller_pickup_return");
    }
    @Override
    public void setSellerPickupReturn(boolean seller_pickup_return) {
        set("@ondc/org/seller_pickup_return", seller_pickup_return);
    }
    @Override
    public boolean isCancellable() {
        return getBoolean("@ondc/org/cancellable");
    }

    @Override
    public void setCancellable(boolean cancellable) {
        set("@ondc/org/cancellable", cancellable);
    }

    @Override
    public Duration getReturnWindow() {
        return Duration.parse(get("@ondc/org/return_window"));
    }

    @Override
    public void setReturnWindow(Duration return_window) {
        set("@ondc/org/return_window", return_window.toString());
    }

    @Override
    public Duration getTimeToShip() {
        String tos = get("@ondc/org/time_to_ship");
        return tos != null ? Duration.parse(tos):null;
    }

    @Override
    public void setTimeToShip(Duration time_to_ship) {
        set("@ondc/org/time_to_ship", time_to_ship.toString());
    }

    @Override
    public boolean isAvailableOnCod() {
        return getBoolean("@ondc/org/available_on_cod");
    }

    @Override
    public void setAvailableOnCod(boolean available_on_cod) {
        set("@ondc/org/available_on_cod", available_on_cod);
    }

    @Override
    public String getContactDetailsConsumerCare() {
        return get("@ondc/org/contact_details_consumer_care");
    }

    @Override
    public void setContactDetailsConsumerCare(String contact_details_consumer_care) {
        set("@ondc/org/contact_details_consumer_care", contact_details_consumer_care);
    }

    @Override
    public PackagedCommodity getPackagedCommodity() {
        return get(PackagedCommodity.class, "@ondc/org/statutory_reqs_packaged_commodities");
    }


    @Override
    public void setPackagedCommodity(PackagedCommodity statutory_reqs_packaged_commodities) {
        set("@ondc/org/statutory_reqs_packaged_commodities", statutory_reqs_packaged_commodities);
    }

    public PrepackagedFood getPrepackagedFood(){
        return get(PrepackagedFood.class, "@ondc/org/statutory_reqs_prepackaged_food");
    }
    public void setPrepackagedFood(PrepackagedFood prepackaged_food){
        set("@ondc/org/statutory_reqs_prepackaged_food",prepackaged_food);
    }

    public VeggiesFruits getVeggiesFruits(){
        return get(VeggiesFruits.class, "@ondc/org/mandatory_reqs_veggies_fruits");
    }
    public void setVeggiesFruits(VeggiesFruits veggies_fruits){
        set("@ondc/org/mandatory_reqs_veggies_fruits",veggies_fruits);
    }
}
