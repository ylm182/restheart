/*
 * Copyright 2014 - 2015 SoftInstigate.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.restheart.security.handlers;

import io.undertow.security.idm.IdentityManager;
import io.undertow.server.HttpServerExchange;
import org.restheart.handlers.PipedHttpHandler;
import org.restheart.handlers.RequestContext;
import org.restheart.security.AccessManager;

/**
 *
 * @author Andrea Di Cesare {@literal <andrea@softinstigate.com>}
 */
public class SecurityHandlerDispacher extends PipedHttpHandler {

    public static final String SILENT_HEADER_KEY = "No-Auth-Challenge";
    public static final String SILENT_QUERY_PARAM_KEY = "noauthchallenge";

    private final SecurityHandler silentHandler;
    private final SecurityHandler challengingHandler;

    /**
     *
     * @param next
     * @param identityManager
     * @param accessManager
     */
    public SecurityHandlerDispacher(final PipedHttpHandler next, final IdentityManager identityManager, final AccessManager accessManager) {
        super(null);
        
        silentHandler = new SecurityHandler(next, identityManager, accessManager, false);
        challengingHandler = new SecurityHandler(next, identityManager, accessManager, true);
    }

    /**
     *
     * @param exchange
     * @param context
     * @throws Exception
     */
    @Override
    public void handleRequest(HttpServerExchange exchange, RequestContext context) throws Exception {
        if (exchange.getRequestHeaders().contains(SILENT_HEADER_KEY) || exchange.getQueryParameters().containsKey(SILENT_QUERY_PARAM_KEY)) {
            silentHandler.handleRequest(exchange, context);
        } else {
            challengingHandler.handleRequest(exchange, context);
        }
    }
}
