package com.retrsoft.bilisponsorskip

internal fun firstTextArgument(arguments: Array<out Any?>): CharSequence? =
    arguments.firstOrNull() as? CharSequence
