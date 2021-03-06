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

package wisp.api;

/**
 * Plugin which loads some functionality into the Wisp server.
 *
 * @author <a href="mailto:kyle.downey@gmail.com">Kyle F. Downey</a>
 */
public interface ServiceModule extends Configurable, Destroyable {
    @Override
    default void configure(Configuration config) { }

    /**
     * Starts up provided services, e.g. opening sockets, starting threads, etc..
     */
    default void start() { }

    /**
     * Cleanly shuts down provided services, e.g. closing resources, stopping threads, etc..
     */
    default void stop() { }

    @Override
    default void destroy() { }
}
