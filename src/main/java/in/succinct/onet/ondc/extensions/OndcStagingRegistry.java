package in.succinct.onet.ondc.extensions;

import in.succinct.bpp.core.adaptor.NetworkAdaptorFactory;

public class OndcStagingRegistry extends OndcRegistry{
    static {
        NetworkAdaptorFactory.getInstance().registerAdaptor(new OndcStagingRegistry()   );
    }
    public OndcStagingRegistry() {
        super("ondc");
    }
}
