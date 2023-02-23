package in.succinct.onet.ondc.extensions;

import in.succinct.beckn.Request;
import in.succinct.bpp.core.adaptor.CommerceAdaptor;
import in.succinct.bpp.core.adaptor.NetworkAdaptor;
import in.succinct.bpp.core.adaptor.api.NetworkApiAdaptor;
import in.succinct.bpp.search.adaptor.SearchAdaptor;

public class OndcApiAdaptor extends NetworkApiAdaptor {
    OndcApiAdaptor(NetworkAdaptor networkAdaptor) {
        super(networkAdaptor);
    }

    @Override
    public void search(CommerceAdaptor adaptor, Request request, Request reply) {
        new SearchAdaptor(adaptor).search(request,reply);
    }

    @Override
    public void select(CommerceAdaptor adaptor, Request request, Request reply) {
        request.getMessage().setOrder(getInputOrder(adaptor,request.getMessage().getOrder()));
        new SearchAdaptor(adaptor).select(request,reply);
        reply.getMessage().setOrder(getOutputOrder(adaptor,reply.getMessage().getOrder()));
    }


}
