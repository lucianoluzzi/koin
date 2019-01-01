/*
 * Copyright 2017-2018 the original author or authors.
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
package org.koin.core.scope

import org.koin.core.Koin
import org.koin.core.error.ClosedScopeException
import org.koin.core.parameter.ParametersDefinition

data class ScopeInstance(
    val id: String,
    val scopeDefinition: ScopeDefinition? = null
) {

    var koin: Koin? = null

    /**
     * Is Scope associated to Koin
     */
    internal fun isRegistered() = koin != null

    /**
     * Close all instances from this scope
     */
    fun close() = synchronized(this) {
        scopeDefinition?.release(this)
        koin?.deleteScope(this.id)
        koin = null
    }

    /**
     * Register in Koin instance
     */
    internal fun register(koin: Koin) {
        this.koin = koin
    }

    /**
     * Lazy inject a Koin instance
     * @param name
     * @param parameters
     */
    inline fun <reified T> inject(
        name: String? = null,
        noinline parameters: ParametersDefinition? = null
    ): Lazy<T> =
        lazy { get<T>(name, parameters) }

    /**
     * Get a Koin instance
     * @param name
     * @param parameters
     */
    inline fun <reified T> get(
        name: String? = null,
        noinline parameters: ParametersDefinition? = null
    ): T {
        return koin?.get(T::class, name, this, parameters) ?: throw  ClosedScopeException("Scope $this is closed")
    }
}