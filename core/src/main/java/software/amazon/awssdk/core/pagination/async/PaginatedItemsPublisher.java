/*
 * Copyright 2010-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package software.amazon.awssdk.core.pagination.async;

import java.util.Iterator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import org.reactivestreams.Subscriber;

/**
 * A publisher to request for a stream of paginated items. The class can be used to request data for paginated items
 * across multiple pages.
 *
 * @param <ResponseT> The type of a single response page
 * @param <ItemT> The type of paginated member in a response page
 */
public class PaginatedItemsPublisher<ResponseT, ItemT> implements SdkPublisher<ItemT> {

    private final AsyncPageFetcher<ResponseT> nextPageFetcher;

    private final Function<ResponseT, Iterator<ItemT>> getIteratorFunction;

    public PaginatedItemsPublisher(AsyncPageFetcher<ResponseT> nextPageFetcher,
                                   Function<ResponseT, Iterator<ItemT>> getIteratorFunction) {
        this.nextPageFetcher = nextPageFetcher;
        this.getIteratorFunction = getIteratorFunction;
    }

    @Override
    public void subscribe(Subscriber<? super ItemT> subscriber) {
        subscriber.onSubscribe(new ItemsSubscription(subscriber));
    }

    private class ItemsSubscription implements org.reactivestreams.Subscription {

        private AtomicLong outstandingRequests = new AtomicLong(0);

        private AtomicBoolean isTaskRunning = new AtomicBoolean(false);

        private final Subscriber subscriber;

        private volatile Iterator<ItemT> singlePageItemsIterator;

        private volatile ResponseT currentPage;

        ItemsSubscription(Subscriber subscriber) {
            this.subscriber = subscriber;
        }

        @Override
        public void request(long n) {
            if (n <= 0) {
                throw new IllegalArgumentException("Non-positive request signals are illegal");
            }

            outstandingRequests.addAndGet(n);

            synchronized (this) {
                if (!isTaskRunning.get()) {
                    isTaskRunning.set(true);
                    handleRequestsRecursively();
                }
            }
        }

        private void handleRequestsRecursively() {
            if (currentPage != null && !nextPageFetcher.hasNextPage(currentPage) &&
                    singlePageItemsIterator != null && !singlePageItemsIterator.hasNext()) {
                subscriber.onComplete();
                isTaskRunning.set(false);
                return;
            }

            if (outstandingRequests.get() <= 0) {
                isTaskRunning.set(false);
                return;
            }

            /**
             * Current page is null only the first time the method is called.
             * Once initialized, current page will never be null
             */
            if (currentPage == null && singlePageItemsIterator == null) {
                fetchNextPage();

            } else if (singlePageItemsIterator != null && singlePageItemsIterator.hasNext()) {
                sendNextElement();

            } else if (singlePageItemsIterator != null && !singlePageItemsIterator.hasNext() &&
                       currentPage != null && nextPageFetcher.hasNextPage(currentPage)) {
                fetchNextPage();

            // All valid cases are covered above. Throw an exception if any combination is missed
            } else {
                throw new IllegalStateException("Execution should have not reached here");
            }
        }

        private void fetchNextPage() {
            CompletableFuture<ResponseT> future = nextPageFetcher.nextPage(currentPage);
            future.whenComplete(((response, error) -> {
                if (response != null) {
                    currentPage = response;
                    singlePageItemsIterator = getIteratorFunction.apply(response);
                    sendNextElement();
                }
                if (error != null) {
                    subscriber.onError(error);
                }
            }));
        }

        /**
         * Calls onNext and calls the recursive method.
         */
        private void sendNextElement() {
            if (singlePageItemsIterator.hasNext()) {
                subscriber.onNext(singlePageItemsIterator.next());
                outstandingRequests.getAndDecrement();
            }

            handleRequestsRecursively();
        }

        @Override
        public void cancel() {
            outstandingRequests.set(0);
            isTaskRunning.set(false);
        }
    }
}
