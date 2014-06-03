package org.xbib.elasticsearch.action.simple;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.junit.Test;

public class SimpleActionTest extends AbstractNodeTestHelper {

    @Test
    public void testSimpleAction() throws NullPointerException {
        Client client = client("1");

        // index a test document
        IndexRequest indexRequest = new IndexRequest().index("test").type("test").id("test")
                .source("Hello", "World")
                .refresh(true);
        client.index(indexRequest).actionGet();

        // execute simple action (match all query)
        SimpleRequestBuilder builder = new SimpleRequestBuilder(client);
        SearchResponse response = client.execute(SimpleAction.INSTANCE, builder.request()).actionGet();
        logger.info("result of simple action = {}", response.toString());
    }

}
