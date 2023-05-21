package in.succinct.onet.ondc.extensions;

import in.succinct.bpp.core.adaptor.NetworkAdaptorFactory;

public class OndcPreProdRegistry extends OndcRegistry{
    static {
        NetworkAdaptorFactory.getInstance().registerAdaptor(new OndcPreProdRegistry()   );
    }
    public OndcPreProdRegistry() {
        super("ondc-preprod");
    }
}
