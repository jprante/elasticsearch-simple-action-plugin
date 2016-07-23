package org.xbib.elasticsearch.plugin.simple;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.common.logging.ESLoggerFactory;
import org.junit.Test;
import org.xbib.elasticsearch.action.simple.SimpleAction;
import org.xbib.elasticsearch.action.simple.SimpleRequestBuilder;

public class SimpleActionTest extends NodeTestUtils {

    private final static ESLogger logger = ESLoggerFactory.getLogger(SimpleAction.class.getName());

    @Test
    public void testSimpleAction() throws NullPointerException {
        Client client = client();

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
