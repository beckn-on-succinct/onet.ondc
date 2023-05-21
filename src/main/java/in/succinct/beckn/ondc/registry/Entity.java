package in.succinct.beckn.ondc.registry;

import com.venky.swf.plugins.collab.db.model.user.Phone;
import in.succinct.beckn.BecknObject;
import in.succinct.beckn.BecknObjects;
import in.succinct.beckn.Request;
import in.succinct.beckn.Subscriber;
import org.json.simple.JSONArray;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Entity extends BecknObject {
    public Entity(){
        setCountry("IND");
        setCallbackUrl("/spp");
    }
    public Gst getGst(){
        return get(Gst.class, "gst");
    }
    public void setGst(Gst gst){
        set("gst",gst);
    }

    public static class Gst extends BecknObject {
        public String getLegalEntityName() {
            return get("legal_entity_name");
        }

        public void setLegalEntityName(String legal_entity_name) {
            set("legal_entity_name", legal_entity_name);
        }

        public String getBusinessAddress() {
            return get("business_address");
        }

        public void setBusinessAddress(String business_address) {
            set("business_address", business_address);
        }

        public CityCode getCityCode() {
            return get(CityCode.class, "city_code");
        }

        public void setCityCode(CityCode city_code) {
            set("city_code", city_code);
        }

        public String getGstNo() {
            return get("gst_no");
        }

        public void setGstNo(String gst_no) {
            set("gst_no", gst_no);
        }
    }
    public Pan getPan(){
        return get(Pan.class, "pan");
    }
    public void setPan(Pan pan){
        set("pan",pan);
    }

    public String getNameOfAuthorizedSignatory(){
        return get("name_of_authorised_signatory");
    }
    public void setNameOfAuthorizedSignatory(String name_of_authorized_signatory){
        set("name_of_authorised_signatory",name_of_authorized_signatory);
    }
    public String getAddressOfAuthorizedSignatory(){
        return get("address_of_authorised_signatory");
    }
    public void setAddressOfAuthorizedSignatory(String address_of_authorized_signatory){
        set("address_of_authorised_signatory",address_of_authorized_signatory);
    }

    public String getEmailId(){
        return get("email_id");
    }
    public void setEmailId(String email_id){
        set("email_id",email_id);
    }

    public String getMobileNo(){
        return get("mobile_no");
    }
    public void setMobileNo(String mobile_no){
        set("mobile_no", Phone.sanitizePhoneNumber(mobile_no).substring(3));
    }

    public String getCountry(){
        return get("country");
    }
    public void setCountry(String country){
        set("country",country);
    }
    public String getSubscriberId(){
        return get("subscriber_id");
    }
    public void setSubscriberId(String subscriber_id){
        set("subscriber_id",subscriber_id);
    }
    public String getUniqueKeyId(){
        return get("unique_key_id");
    }
    public void setUniqueKeyId(String unique_key_id){
        set("unique_key_id",unique_key_id);
    }
    public String getCallbackUrl(){
        return get("callback_url");
    }
    public void setCallbackUrl(String callback_url){
        set("callback_url",callback_url);
    }
    public KeyPair getKeyPair(){
        return get(KeyPair.class, "key_pair");
    }
    public void setKeyPair(KeyPair key_pair){
        set("key_pair",key_pair);
    }


    public static class KeyPair extends BecknObject{
        public String getSigningPublicKey(){
            return get("signing_public_key");
        }
        public void setSigningPublicKey(String signing_public_key){
            set("signing_public_key",signing_public_key);
        }
        public String getEncryptionPublicKey(){
            return get("encryption_public_key");
        }
        public void setEncryptionPublicKey(String encryption_public_key){
            set("encryption_public_key",Request.getPemEncryptionKey(encryption_public_key));
        }
        
        public Date getValidFrom(){
            return getTimestamp("valid_from");
        }
        public void setValidFrom(Date valid_from){
            set("valid_from",valid_from,TIMESTAMP_FORMAT);
        }

        public Date getValidUntil(){
            return getTimestamp("valid_until");
        }
        public void setValidUntil(Date valid_until){
            set("valid_until",valid_until,TIMESTAMP_FORMAT);
        }
    }

    public static class NetworkParticipants extends BecknObjects<NetworkParticipant>{

        public NetworkParticipants() {
        }

        public NetworkParticipants(JSONArray value) {
            super(value);
        }

        public NetworkParticipants(String payload) {
            super(payload);
        }

    }
    public static class NetworkParticipant extends BecknObject {
        public NetworkParticipant(){
            setSubscriberUrl("/spp");
            setMsn(false);
            setDomain("nic2004:52110");
        }
        public String getSubscriberUrl(){
            return get("subscriber_url");
        }
        public void setSubscriberUrl(String subscriber_url){
            set("subscriber_url",subscriber_url);
        }
        public String getDomain(){
            return get("domain");
        }
        public void setDomain(String domain){
            set("domain",domain);
        }
        public String getType(){
            return get("type");
        }
        Map<String,String> typeMap = new HashMap<>(){{
           put(Subscriber.SUBSCRIBER_TYPE_BPP,"sellerApp");
           put(Subscriber.SUBSCRIBER_TYPE_BAP,"buyerApp");
           put(Subscriber.SUBSCRIBER_TYPE_BG,"gateway");
        }};
        public void setType(String type){
            String mappedType =  typeMap.get(type);
            set("type", mappedType == null ? type : mappedType);
        }

        public boolean isMsn(){
            return getBoolean("msn");
        }
        public void setMsn(boolean msn){
            set("msn",msn);
        }

        public CityCode getCityCode(){
            return get(CityCode.class, "city_code");
        }
        public void setCityCode(CityCode city_code){
            set("city_code",city_code);
        }
    }
    public static class CityCode extends BecknObjects<String>{
    
    }
    public static class Pan extends BecknObject {
        public String getNameAsPerPan(){
            return get("name_as_per_pan");
        }
        public void setNameAsPerPan(String name_as_per_pan){
            set("name_as_per_pan",name_as_per_pan);
        }
        public String getPanNo(){
            return get("pan_no");
        }
        public void setPanNo(String pan_no){
            set("pan_no",pan_no);
        }
        public Date getDateOfIncorporation(){
            return getDate("date_of_incorporation",BecknObject.APP_DATE_FORMAT);
        }
        public void setDateOfIncorporation(Date date_of_incorporation){
            set("date_of_incorporation",date_of_incorporation, BecknObject.APP_DATE_FORMAT);
        }

    }
}
