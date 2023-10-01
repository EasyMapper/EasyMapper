package easymapper.kotlin

import easymapper.Mapper
import easymapper.MapperConfiguration
import java.util.function.Consumer

class KotlinMapper : Mapper {

    constructor(configurer: Consumer<MapperConfiguration>) : super({ config ->
        config.useKotlin().apply { configurer.accept(this) }
    })

    constructor() : this({ })
}
