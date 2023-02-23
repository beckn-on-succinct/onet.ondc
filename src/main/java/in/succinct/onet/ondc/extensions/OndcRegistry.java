package in.succinct.onet.ondc.extensions;

import in.succinct.bpp.core.adaptor.NetworkAdaptor;
import in.succinct.bpp.core.adaptor.api.NetworkApiAdaptor;

public class OndcRegistry extends NetworkAdaptor {

    public OndcRegistry(String networkName){
        super(networkName);
    }

    private final OndcApiAdaptor networkApiAdaptor = new OndcApiAdaptor(this);
    @Override
    public NetworkApiAdaptor getApiAdaptor() {
        return networkApiAdaptor;
    }


}
