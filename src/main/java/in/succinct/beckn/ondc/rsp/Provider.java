package in.succinct.beckn.ondc.rsp;

import in.succinct.beckn.Descriptor;

public class Provider extends in.succinct.beckn.ondc.retail.Provider {
    public Provider() {
    }

    public Provider(String payload) {
        super(payload);
    }


    public Descriptor getDescriptor(){
        return get(Descriptor.class, "name");
    }
    public void setDescriptor(Descriptor descriptor){
        set("name",descriptor);
    }


}
