import kotlin.system.measureTimeMillis
import java.util.concurrent.ForkJoinPool
import java.util.concurrent.RecursiveTask

//hereda de la clase RecursiveTask
class SumaP(private val numbers: List<Int>) : RecursiveTask<Int>() {
    override fun compute(): Int {
        return if (numbers.size <= 10_000) {
            numbers.sum()
        } else {
            val mid = numbers.size / 2
            val leftTask = SumaP(numbers.subList(0, mid))
            val rightTask = SumaP(numbers.subList(mid, numbers.size))
            leftTask.fork()
            rightTask.compute() + leftTask.join()
        }
    }
}

//sin paralelismo
fun sumList(numbers: List<Int>): Int {
    var sum = 0
    for (number in numbers) {
        sum += number
    }
    return sum
}
fun main() {
    val numbers = List(1_000_000_000) { (1..10).random() }
    // Tiempo sin paralelo
    val timeOriginal = measureTimeMillis {
        println("Suma secuencial: ${sumList(numbers)}")
    }
    println("Tiempo suma secuencial: $timeOriginal ms")

    // Tiempo con paralelismo.
    val pool = ForkJoinPool()
    val timeForkJoin = measureTimeMillis {
        val result = pool.invoke(SumaP(numbers))
        println("Suma ForkJoinPool: $result")
    }
    println("Tiempo ForkJoinPool: $timeForkJoin ms")
}
