package software.amazon.awssdk.services.jsonprotocoltests.paginators;

import java.util.concurrent.CompletableFuture;
import javax.annotation.Generated;
import org.reactivestreams.Subscriber;
import software.amazon.awssdk.core.pagination.async.AsyncPageFetcher;
import software.amazon.awssdk.core.pagination.async.ResponsesSubscription;
import software.amazon.awssdk.core.pagination.async.SdkPublisher;
import software.amazon.awssdk.services.jsonprotocoltests.JsonProtocolTestsAsyncClient;
import software.amazon.awssdk.services.jsonprotocoltests.model.PaginatedOperationWithoutResultKeyRequest;
import software.amazon.awssdk.services.jsonprotocoltests.model.PaginatedOperationWithoutResultKeyResponse;

/**
 * <p>
 * Represents the output for the
 * {@link software.amazon.awssdk.services.jsonprotocoltests.JsonProtocolTestsAsyncClient#paginatedOperationWithoutResultKeyPaginator(software.amazon.awssdk.services.jsonprotocoltests.model.PaginatedOperationWithoutResultKeyRequest)}
 * operation which is a paginated operation. This class is a type of {@link org.reactivestreams.Publisher} which can be
 * used to provide a sequence of
 * {@link software.amazon.awssdk.services.jsonprotocoltests.model.PaginatedOperationWithoutResultKeyResponse} response
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
 * software.amazon.awssdk.services.jsonprotocoltests.paginators.PaginatedOperationWithoutResultKeyPublisher publisher = client.paginatedOperationWithoutResultKeyPaginator(request);
 * CompletableFuture<Void> future = publisher.forEach(res -> { // Do something with the response });
 * future.get();
 * }
 * </pre>
 *
 * 2) Using a custom subscriber
 *
 * <pre>
 * {@code
 * software.amazon.awssdk.services.jsonprotocoltests.paginators.PaginatedOperationWithoutResultKeyPublisher publisher = client.paginatedOperationWithoutResultKeyPaginator(request);
 * publisher.subscribe(new Subscriber<software.amazon.awssdk.services.jsonprotocoltests.model.PaginatedOperationWithoutResultKeyResponse>() {
 *
 * public void onSubscribe(org.reactivestreams.Subscriber subscription) { //... };
 *
 *
 * public void onNext(software.amazon.awssdk.services.jsonprotocoltests.model.PaginatedOperationWithoutResultKeyResponse response) { //... };
 * });}
 * </pre>
 *
 * As the response is a publisher, it can work well with third party reactive streams implementations like RxJava2.
 * <p>
 * <b>Note: If you prefer to have control on service calls, use the
 * {@link #paginatedOperationWithoutResultKey(software.amazon.awssdk.services.jsonprotocoltests.model.PaginatedOperationWithoutResultKeyRequest)}
 * operation.</b>
 * </p>
 */
@Generated("software.amazon.awssdk:codegen")
public final class PaginatedOperationWithoutResultKeyPublisher implements
                                                               SdkPublisher<PaginatedOperationWithoutResultKeyResponse> {
    private final JsonProtocolTestsAsyncClient client;

    private final PaginatedOperationWithoutResultKeyRequest firstRequest;

    private final AsyncPageFetcher nextPageFetcher;

    public PaginatedOperationWithoutResultKeyPublisher(final JsonProtocolTestsAsyncClient client,
                                                       final PaginatedOperationWithoutResultKeyRequest firstRequest) {
        this.client = client;
        this.firstRequest = firstRequest;
        this.nextPageFetcher = new PaginatedOperationWithoutResultKeyResponseFetcher();
    }

    @Override
    public void subscribe(Subscriber<? super PaginatedOperationWithoutResultKeyResponse> subscriber) {
        subscriber.onSubscribe(new ResponsesSubscription(subscriber, nextPageFetcher));
    }

    private class PaginatedOperationWithoutResultKeyResponseFetcher implements
                                                                    AsyncPageFetcher<PaginatedOperationWithoutResultKeyResponse> {
        @Override
        public boolean hasNextPage(final PaginatedOperationWithoutResultKeyResponse previousPage) {
            return previousPage.nextToken() != null;
        }

        @Override
        public CompletableFuture<PaginatedOperationWithoutResultKeyResponse> nextPage(
            final PaginatedOperationWithoutResultKeyResponse previousPage) {
            if (previousPage == null) {
                return client.paginatedOperationWithoutResultKey(firstRequest);
            }
            return client
                .paginatedOperationWithoutResultKey(firstRequest.toBuilder().nextToken(previousPage.nextToken()).build());
        }
    }
}
