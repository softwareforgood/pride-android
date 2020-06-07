package com.softwareforgood.pridefestival.ui.misc

import com.softwareforgood.pridefestival.ApplicationScope
import com.softwareforgood.pridefestival.data.model.Event
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

@ApplicationScope
class EventClickHandler @Inject constructor() {

    private val eventStream = PublishSubject.create<Event>()

    fun publishClick(event: Event) {
        eventStream.onNext(event)
    }

    fun eventClicks(): Observable<Event> = eventStream
}
