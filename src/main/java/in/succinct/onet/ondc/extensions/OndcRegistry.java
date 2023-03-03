package in.succinct.onet.ondc.extensions;

import in.succinct.bpp.core.adaptor.NetworkAdaptor;
import in.succinct.bpp.core.adaptor.api.NetworkApiAdaptor;

public class OndcRegistry extends NetworkAdaptor {
    protected OndcRegistry(){

    }

    protected OndcRegistry(String networkName){
        super(networkName);
    }

    private final transient OndcApiAdaptor networkApiAdaptor = new OndcApiAdaptor(this);
    @Override
    public NetworkApiAdaptor getApiAdaptor() {
        return networkApiAdaptor;
    }


}
