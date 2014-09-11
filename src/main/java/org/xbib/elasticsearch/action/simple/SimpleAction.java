package org.xbib.elasticsearch.action.simple;

import org.elasticsearch.action.ClientAction;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;

public class SimpleAction extends ClientAction<SimpleRequest, SearchResponse, SimpleRequestBuilder> {

    public static final SimpleAction INSTANCE = new SimpleAction();

    public static final String NAME = "org.xbib.elasticsearch.action.simple";

    private SimpleAction() {
        super(NAME);
    }

    @Override
    public SimpleRequestBuilder newRequestBuilder(Client client) {
        return new SimpleRequestBuilder(client);
    }

    @Override
    public SearchResponse newResponse() {
        return new SearchResponse();
    }
}
