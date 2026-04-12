package com.shortspark.emaliestates.util.helpers



// ─── Utility ──────────────────────────────────────────────────────────────────
fun formatPrice(price: Float): String = when {
    price >= 1_000_000 -> "${((price / 1_000_000 * 10).toInt() / 10.0).toInt()}M"
    price >= 1_000     -> "${(price / 1_000).toInt()}K"
    else               -> "${price.toInt()}"
}