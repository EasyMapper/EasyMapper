package easymapper.kotlin.fixture

data class Pricing(val listPrice: Double, val discount: Double) {

    fun calculateSalePrice(): Double {
        return listPrice - discount
    }
}
