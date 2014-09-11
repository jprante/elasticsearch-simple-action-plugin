package org.xbib.elasticsearch.action.simple;

import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.ActionRequestBuilder;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilders;

public class SimpleRequestBuilder extends ActionRequestBuilder<SimpleRequest, SearchResponse, SimpleRequestBuilder, Client> {

    public SimpleRequestBuilder(Client client) {
        super(client, new SimpleRequest());

        // here: a built-in query definition, a match all query

        SearchRequestBuilder searchRequestBuilder = new SearchRequestBuilder(client);
        searchRequestBuilder
                .setQuery(QueryBuilders.matchAllQuery());

        request.setSearchRequest(searchRequestBuilder.request());
    }

    @Override
    public SimpleRequest request() {
        return request;
    }

    @Override
    protected void doExecute(ActionListener<SearchResponse> listener) {
        client.execute(SimpleAction.INSTANCE, request(), listener);
    }
}
