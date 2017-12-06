package software.amazon.awssdk.services.jsonprotocoltests.paginators;

import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import javax.annotation.Generated;
import org.reactivestreams.Subscriber;
import software.amazon.awssdk.core.pagination.async.AsyncPageFetcher;
import software.amazon.awssdk.core.pagination.async.PaginatedItemsPublisher;
import software.amazon.awssdk.core.pagination.async.ResponsesSubscription;
import software.amazon.awssdk.core.pagination.async.SdkPublisher;
import software.amazon.awssdk.services.jsonprotocoltests.JsonProtocolTestsAsyncClient;
import software.amazon.awssdk.services.jsonprotocoltests.model.PaginatedOperationWithResultKeyRequest;
import software.amazon.awssdk.services.jsonprotocoltests.model.PaginatedOperationWithResultKeyResponse;
import software.amazon.awssdk.services.jsonprotocoltests.model.SimpleStruct;

/**
 * <p>
 * Represents the output for the
 * {@link software.amazon.awssdk.services.jsonprotocoltests.JsonProtocolTestsAsyncClient#paginatedOperationWithResultKeyPaginator(software.amazon.awssdk.services.jsonprotocoltests.model.PaginatedOperationWithResultKeyRequest)}
 * operation which is a paginated operation. This class is a type of {@link org.reactivestreams.Publisher} which can be
 * used to provide a sequence of
 * {@link software.amazon.awssdk.services.jsonprotocoltests.model.PaginatedOperationWithResultKeyResponse} response
 * pages as per demand from the subscriber.
 * </p>
 * <p>
 * When the operation is called, an instance of this class is returned. At this point, no service calls are made yet and
 * so there is no guarantee that the request is valid. The subscribe method should be called as a request to stream
 * data. For more info, see {@link org.reactivestreams.Publisher#subscribe(org.reactivestreams.Subscriber)}. If there
 * are errors in your request, you will see the failures only after you start streaming the data.
 * </p>
 *
 * <p>
 * The following are few ways to use the response class:
 * </p>
 * 1) Using the forEach helper method. This uses @
 * {@link software.amazon.awssdk.core.pagination.async.SequentialSubscriber} internally
 *
 * <pre>
 * {@code
 * software.amazon.awssdk.services.jsonprotocoltests.paginators.PaginatedOperationWithResultKeyPublisher publisher = client.paginatedOperationWithResultKeyPaginator(request);
 * CompletableFuture<Void> future = publisher.forEach(res -> { // Do something with the response });
 * future.get();
 * }
 * </pre>
 *
 * 2) Using a custom subscriber
 *
 * <pre>
 * {@code
 * software.amazon.awssdk.services.jsonprotocoltests.paginators.PaginatedOperationWithResultKeyPublisher publisher = client.paginatedOperationWithResultKeyPaginator(request);
 * publisher.subscribe(new Subscriber<software.amazon.awssdk.services.jsonprotocoltests.model.PaginatedOperationWithResultKeyResponse>() {
 *
 * public void onSubscribe(org.reactivestreams.Subscriber subscription) { //... };
 *
 *
 * public void onNext(software.amazon.awssdk.services.jsonprotocoltests.model.PaginatedOperationWithResultKeyResponse response) { //... };
 * });}
 * </pre>
 *
 * As the response is a publisher, it can work well with third party reactive streams implementations like RxJava2.
 * <p>
 * <b>Note: If you prefer to have control on service calls, use the
 * {@link #paginatedOperationWithResultKey(software.amazon.awssdk.services.jsonprotocoltests.model.PaginatedOperationWithResultKeyRequest)}
 * operation.</b>
 * </p>
 */
@Generated("software.amazon.awssdk:codegen")
public final class PaginatedOperationWithResultKeyPublisher implements SdkPublisher<PaginatedOperationWithResultKeyResponse> {
    private final JsonProtocolTestsAsyncClient client;

    private final PaginatedOperationWithResultKeyRequest firstRequest;

    private final AsyncPageFetcher nextPageFetcher;

    public PaginatedOperationWithResultKeyPublisher(final JsonProtocolTestsAsyncClient client,
                                                    final PaginatedOperationWithResultKeyRequest firstRequest) {
        this.client = client;
        this.firstRequest = firstRequest;
        this.nextPageFetcher = new PaginatedOperationWithResultKeyResponseFetcher();
    }

    @Override
    public void subscribe(Subscriber<? super PaginatedOperationWithResultKeyResponse> subscriber) {
        subscriber.onSubscribe(new ResponsesSubscription(subscriber, nextPageFetcher));
    }

    /**
     * Returns a publisher that can be used to get a stream of data. You need to subscribe to the publisher to request
     * the stream of data. The publisher has a helper forEach method that takes in a {@link java.util.function.Consumer}
     * and then applies that consumer to each response returned by the service.
     */
    public SdkPublisher<SimpleStruct> items() {
        Function<PaginatedOperationWithResultKeyResponse, Iterator<SimpleStruct>> getIterator = response -> {
            if (response != null && response.items() != null) {
                return response.items().iterator();
            }
            return Collections.emptyIterator();
        };
        return new PaginatedItemsPublisher(new PaginatedOperationWithResultKeyResponseFetcher(), getIterator);
    }

    private class PaginatedOperationWithResultKeyResponseFetcher implements
                                                                 AsyncPageFetcher<PaginatedOperationWithResultKeyResponse> {
        @Override
        public boolean hasNextPage(final PaginatedOperationWithResultKeyResponse previousPage) {
            return previousPage.nextToken() != null;
        }

        @Override
        public CompletableFuture<PaginatedOperationWithResultKeyResponse> nextPage(
            final PaginatedOperationWithResultKeyResponse previousPage) {
            if (previousPage == null) {
                return client.paginatedOperationWithResultKey(firstRequest);
            }
            return client.paginatedOperationWithResultKey(firstRequest.toBuilder().nextToken(previousPage.nextToken()).build());
        }
    }
}
