package in.succinct.onet.ondc.extensions;

import in.succinct.beckn.Request;
import in.succinct.bpp.core.adaptor.CommerceAdaptor;
import in.succinct.bpp.core.adaptor.NetworkAdaptor;
import in.succinct.bpp.core.adaptor.api.NetworkApiAdaptor;
import in.succinct.bpp.search.adaptor.SearchAdaptor;
import in.succinct.onet.ondc.db.model.BecknOrderMeta;

import java.util.Map;

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
        new SearchAdaptor(adaptor).select(request,reply);
    }


    @Override
    public void call(CommerceAdaptor adaptor, Map<String, String> headers, Request request, Request response) {
        super.call(adaptor, headers, request, response);
        adaptor.getOrderMeta().getRawRecord().getAsProxy(BecknOrderMeta.class).mineTransactionLines(getNetworkAdaptor(),adaptor);
    }

    @Override
    public void callback(CommerceAdaptor adaptor, Request reply) {
        super.callback(adaptor, reply);
    }
}
