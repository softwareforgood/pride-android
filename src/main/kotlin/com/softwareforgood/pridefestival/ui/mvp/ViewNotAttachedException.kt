package com.softwareforgood.pridefestival.ui.mvp

class ViewNotAttachedException : RuntimeException(
        "Must attach view to presenter before attempting to perform view operations")
