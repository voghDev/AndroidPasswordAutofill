package es.voghdev.androidpasswordautofill

import android.service.autofill.AutofillService
import android.service.autofill.FillCallback
import android.service.autofill.FillRequest
import android.service.autofill.SaveCallback
import android.service.autofill.SaveRequest
import android.os.CancellationSignal

class MyAutofillService : AutofillService() {
    override fun onFillRequest(
        request: FillRequest,
        cancellationSignal: CancellationSignal,
        callback: FillCallback
    ) {
        // TODO: Implement fill logic
    }

    override fun onSaveRequest(request: SaveRequest, callback: SaveCallback) {
        // TODO: Implement save logic
    }

    override fun onConnected() {
        // TODO: Initialize service
    }

    override fun onDisconnected() {
        // TODO: Clean up service
    }
}
