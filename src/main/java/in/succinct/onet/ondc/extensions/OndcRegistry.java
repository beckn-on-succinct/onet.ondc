package in.succinct.onet.ondc.extensions;

import in.succinct.beckn.BecknObject;
import in.succinct.bpp.core.adaptor.NetworkAdaptor;

public class OndcRegistry extends NetworkAdaptor {

    public OndcRegistry(String networkName){
        super(networkName);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <O extends BecknObject, E extends O> Class<E> getExtensionClass(String domain,Class<O> clazz) {
        String possibleExtensionClassName = String.format("%s.%s",getDomains().get(domain).getExtensionPackage(),clazz.getSimpleName());
        try {
            return (Class<E>) Class.forName(possibleExtensionClassName);
        }catch (Exception ex){
            return null;
        }
    }
}
