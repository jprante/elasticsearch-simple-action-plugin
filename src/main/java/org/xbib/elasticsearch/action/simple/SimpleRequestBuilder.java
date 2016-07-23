package org.xbib.elasticsearch.action.simple;

import org.elasticsearch.action.ActionRequestBuilder;
import org.elasticsearch.action.search.SearchAction;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.ElasticsearchClient;
import org.elasticsearch.index.query.QueryBuilders;

public class SimpleRequestBuilder extends ActionRequestBuilder<SimpleRequest, SearchResponse, SimpleRequestBuilder> {

    public SimpleRequestBuilder(ElasticsearchClient client) {
        super(client, SimpleAction.INSTANCE, new SimpleRequest());

        // here: a built-in query definition, a match all query

        SearchRequestBuilder searchRequestBuilder = new SearchRequestBuilder(client, SearchAction.INSTANCE);
        searchRequestBuilder
                .setQuery(QueryBuilders.matchAllQuery());

        request.setSearchRequest(searchRequestBuilder.request());
    }

    @Override
    public SimpleRequest request() {
        return request;
    }

}
