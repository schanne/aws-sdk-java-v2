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

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

/**
 * A publisher to request for a stream of response pages. The class can be used to request data until
 * there are no more pages.
 *
 * @param <ResponseT> The type of a single response page
 */
public class ResponsesSubscription<ResponseT> implements Subscription {
    private final Subscriber subscriber;

    private AtomicLong outstandingRequests = new AtomicLong(0);

    private AsyncPageFetcher<ResponseT> nextPageFetcher;

    private volatile ResponseT currentPage;

    private AtomicBoolean isTaskRunning = new AtomicBoolean(false);

    public ResponsesSubscription(Subscriber subscriber, AsyncPageFetcher<ResponseT> nextPageFetcher) {
        this.subscriber = subscriber;
        this.nextPageFetcher = nextPageFetcher;
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
                handleRequest();
            }
        }
    }

    /**
     * Recursive method to deal with requests until there are no outstandingRequests or
     * no more pages.
     */
    private synchronized void handleRequest() {
        if (currentPage != null && !nextPageFetcher.hasNextPage(currentPage)) {
            subscriber.onComplete();
            isTaskRunning.set(false);
            return;
        }

        if (outstandingRequests.get() <= 0) {
            isTaskRunning.set(false);
            return;
        }

        outstandingRequests.getAndDecrement();

        CompletableFuture<ResponseT> future = nextPageFetcher.nextPage(currentPage);
        future.whenComplete((response, error) -> {
            if (response != null) {
                currentPage = response;
                subscriber.onNext(response);
                handleRequest();
            }

            if (error != null) {
                subscriber.onError(error);
            }
        });
    }

    @Override
    public void cancel() {
        outstandingRequests.set(0);
        isTaskRunning.set(false);
    }
}
