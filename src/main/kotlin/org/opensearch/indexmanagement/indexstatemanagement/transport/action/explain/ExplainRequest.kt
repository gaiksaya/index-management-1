/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.opensearch.indexmanagement.indexstatemanagement.transport.action.explain

import org.opensearch.action.ActionRequest
import org.opensearch.action.ActionRequestValidationException
import org.opensearch.action.ValidateActions
import org.opensearch.common.io.stream.StreamInput
import org.opensearch.common.io.stream.StreamOutput
import org.opensearch.common.unit.TimeValue
import org.opensearch.indexmanagement.indexstatemanagement.model.SearchParams
import org.opensearch.indexmanagement.indexstatemanagement.util.DEFAULT_INDEX_TYPE
import java.io.IOException

class ExplainRequest : ActionRequest {

    val indices: List<String>
    val local: Boolean
    val masterTimeout: TimeValue
    val searchParams: SearchParams
    val showPolicy: Boolean
    val indexType: String

    constructor(
        indices: List<String>,
        local: Boolean,
        masterTimeout: TimeValue,
        searchParams: SearchParams,
        showPolicy: Boolean,
        indexType: String
    ) : super() {
        this.indices = indices
        this.local = local
        this.masterTimeout = masterTimeout
        this.searchParams = searchParams
        this.showPolicy = showPolicy
        this.indexType = indexType
    }

    @Throws(IOException::class)
    constructor(sin: StreamInput) : this(
        indices = sin.readStringList(),
        local = sin.readBoolean(),
        masterTimeout = sin.readTimeValue(),
        searchParams = SearchParams(sin),
        showPolicy = sin.readBoolean(),
        indexType = sin.readString()
    )

    override fun validate(): ActionRequestValidationException? {
        var validationException: ActionRequestValidationException? = null
        if (indexType != DEFAULT_INDEX_TYPE && indices.size > 1) {
            validationException = ValidateActions.addValidationError(
                MULTIPLE_INDICES_CUSTOM_INDEX_TYPE_ERROR,
                validationException
            )
        }
        return validationException
    }

    @Throws(IOException::class)
    override fun writeTo(out: StreamOutput) {
        out.writeStringCollection(indices)
        out.writeBoolean(local)
        out.writeTimeValue(masterTimeout)
        searchParams.writeTo(out)
        out.writeBoolean(showPolicy)
        out.writeString(indexType)
    }

    companion object {
        const val MULTIPLE_INDICES_CUSTOM_INDEX_TYPE_ERROR =
            "Cannot call explain on more than one index name/pattern when using a custom index type"
    }
}
