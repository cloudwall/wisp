/*
 * (C) Copyright 2017 Kyle F. Downey.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * (C) Copyright 2017 Kyle F. Downey.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package wisp.websocket.api;

import jdk.incubator.http.WebSocket;
import wisp.api.*;

/**
 * Servlet-like plugin point for implementing a Websocket server-side component.
 * When first registered to listen on a particular wss:// URI it will receive an
 * {@link #onOpen(WebSocket)} callback and then subsequently will receive ping,
 * pong, text &amp; binary frame messages via {@link WebSocket.Listener} callbacks.
 *
 * @author <a href="mailto:kyle.downey@gmail.com">Kyle F. Downey</a>
 */
public interface WebSocketService extends WebSocket.Listener, Configurable, Destroyable {
    @Override
    default void configure(Configuration config) { }

    @Override
    default void destroy() { }

    /**
     * Gets the distinct relative path that this service listens on.
     */
    String getPath();
}
