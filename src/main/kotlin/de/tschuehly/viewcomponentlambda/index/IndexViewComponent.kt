package de.tschuehly.viewcomponentlambda.index


import de.tschuehly.spring.viewcomponent.core.component.ViewComponent
import de.tschuehly.spring.viewcomponent.core.toProperty
import de.tschuehly.spring.viewcomponent.thymeleaf.ViewContext
import java.util.function.Supplier

@ViewComponent
class IndexViewComponent() : Supplier<ViewContext> {

    override fun get() = ViewContext(
        "helloWorld" toProperty "Hello World"
    )
}