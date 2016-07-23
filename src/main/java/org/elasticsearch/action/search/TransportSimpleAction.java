package org.elasticsearch.action.search;

import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.support.ActionFilters;
import org.elasticsearch.action.support.HandledTransportAction;
import org.elasticsearch.cluster.ClusterService;
import org.elasticsearch.cluster.ClusterState;
import org.elasticsearch.cluster.metadata.IndexNameExpressionResolver;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.IndexNotFoundException;
import org.elasticsearch.indices.IndexClosedException;
import org.elasticsearch.search.action.SearchServiceTransportAction;
import org.elasticsearch.search.controller.SearchPhaseController;
import org.elasticsearch.threadpool.ThreadPool;
import org.elasticsearch.transport.TransportService;
import org.xbib.elasticsearch.action.simple.SimpleAction;
import org.xbib.elasticsearch.action.simple.SimpleRequest;

import java.util.Map;
import java.util.Set;

import static org.elasticsearch.action.search.SearchType.COUNT;
import static org.elasticsearch.action.search.SearchType.QUERY_AND_FETCH;
import static org.elasticsearch.action.search.SearchType.SCAN;

/**
 * copied from TransportSearchAction
 */
public class TransportSimpleAction extends HandledTransportAction<SimpleRequest, SearchResponse> {

    private final SearchServiceTransportAction searchService;
    private final SearchPhaseController searchPhaseController;
    private final ClusterService clusterService;
    private final boolean optimizeSingleShard;

    @Inject
    public TransportSimpleAction(Settings settings, ThreadPool threadPool, SearchPhaseController searchPhaseController,
                                 TransportService transportService, SearchServiceTransportAction searchService,
                                 ClusterService clusterService, ActionFilters actionFilters, IndexNameExpressionResolver
                                             indexNameExpressionResolver) {
        super(settings, SimpleAction.NAME, threadPool, transportService, actionFilters, indexNameExpressionResolver, SimpleRequest.class);
        this.searchPhaseController = searchPhaseController;
        this.searchService = searchService;
        this.clusterService = clusterService;
        this.optimizeSingleShard = this.settings.getAsBoolean("action.search.optimize_single_shard", true);
    }

    @Override
    protected void doExecute(SimpleRequest simpleRequest, ActionListener<SearchResponse> listener) {
        SearchRequest searchRequest = simpleRequest.getSearchRequest();
        // optimize search type for cases where there is only one shard group to search on
        if (optimizeSingleShard && searchRequest.searchType() != SCAN && searchRequest.searchType() != COUNT) {
            try {
                ClusterState clusterState = clusterService.state();
                String[] concreteIndices = indexNameExpressionResolver.concreteIndices(clusterState, searchRequest);
                Map<String, Set<String>> routingMap = indexNameExpressionResolver.resolveSearchRouting(clusterState, searchRequest.routing(), searchRequest.indices());
                int shardCount = clusterService.operationRouting().searchShardsCount(clusterState, concreteIndices, routingMap);
                if (shardCount == 1) {
                    // if we only have one group, then we always want Q_A_F, no need for DFS, and no need to do THEN since we hit one shard
                    searchRequest.searchType(QUERY_AND_FETCH);
                }
            } catch (IndexNotFoundException | IndexClosedException e) {
                // ignore this, we will notify the search response if its really the case
                // from the actual action
            } catch (Exception e) {
                logger.debug("failed to optimize search type, continue as normal", e);
            }
        }

        AbstractSearchAsyncAction searchAsyncAction;
        switch(searchRequest.searchType()) {
            case DFS_QUERY_THEN_FETCH:
                searchAsyncAction = new SearchDfsQueryThenFetchAsyncAction(logger, searchService, clusterService,
                        indexNameExpressionResolver, searchPhaseController, threadPool, searchRequest, listener);
                break;
            case QUERY_THEN_FETCH:
                searchAsyncAction = new SearchQueryThenFetchAsyncAction(logger, searchService, clusterService,
                        indexNameExpressionResolver, searchPhaseController, threadPool, searchRequest, listener);
                break;
            case DFS_QUERY_AND_FETCH:
                searchAsyncAction = new SearchDfsQueryAndFetchAsyncAction(logger, searchService, clusterService,
                        indexNameExpressionResolver, searchPhaseController, threadPool, searchRequest, listener);
                break;
            case QUERY_AND_FETCH:
                searchAsyncAction = new SearchQueryAndFetchAsyncAction(logger, searchService, clusterService,
                        indexNameExpressionResolver, searchPhaseController, threadPool, searchRequest, listener);
                break;
            case SCAN:
                searchAsyncAction = new SearchScanAsyncAction(logger, searchService, clusterService, indexNameExpressionResolver,
                        searchPhaseController, threadPool, searchRequest, listener);
                break;
            case COUNT:
                searchAsyncAction = new SearchCountAsyncAction(logger, searchService, clusterService, indexNameExpressionResolver,
                        searchPhaseController, threadPool, searchRequest, listener);
                break;
            default:
                throw new IllegalStateException("Unknown search type: [" + searchRequest.searchType() + "]");
        }
        searchAsyncAction.start();
    }

}
