package ufrpe.sbpc.botpcd

import io.cucumber.java.en.Given

class StepDefinitions {
    @Given("I have {int} cukes in my belly")
    fun I_have_cukes_in_my_belly(cukes: Int) {
        val belly: Belly = Belly()
        belly.eat(cukes)
    }
}
