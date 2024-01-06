package in.succinct.beckn.ondc.retail;

import in.succinct.beckn.Categories;
import in.succinct.beckn.Locations;

public class Provider extends in.succinct.beckn.Provider {
    @Override
    public boolean isExtendedAttributesDisplayed(){
        return false;
    }

    public Provider(){
        super();
    }
    public Provider(String payload){
        super(payload);
    }


    @Override
    public String getFssaiLicenceNo(){
        return get("@org/ondc/fssai_licence_no");
    }

    @Override
    public void setFssaiLicenceNo(String fssai_licence_no){
        set("@org/ondc/fssai_licence_no",fssai_licence_no);
    }


    public ServiceablityTags getServiceablityTags(){
        return get(ServiceablityTags.class, "tags");
    }
    public void setServiceablityTags(ServiceablityTags tags){
        set("tags",tags);
    }


    public Categories getCategories(){
        return super.getCategories();
    }
    public void setCategories(Categories categories){
        rm("categories"); //Ondc does not want categories unless there is a customization enabled for that category.
    }

}
