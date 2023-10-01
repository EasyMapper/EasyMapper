package easymapper.kotlin

data class Pricing(val listPrice: Double, val discount: Double) {

    fun calculateSalePrice(): Double {
        return listPrice - discount
    }
}
