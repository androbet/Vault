package buisnesslogic.generator

import buisnesslogic.SPECIAL_SYMBOLS_STRING
import java.security.SecureRandom

/**
 * Generates random chars
 */
interface SymbolsGenerator {

    fun generate(): Char
}

class UppercaseSymbolsGenerator(private val random: SecureRandom) : SymbolsGenerator {

    override fun generate(): Char {
        return (random.nextInt(26) + 'A'.code).toChar()
    }
}

class LowercaseSymbolsGenerator(private val random: SecureRandom) : SymbolsGenerator {

    override fun generate(): Char {
        return (random.nextInt(26) + 'a'.code).toChar()
    }
}

class NumbersGenerator(private val random: SecureRandom) : SymbolsGenerator {

    override fun generate(): Char {
        return (random.nextInt(10) + '0'.code).toChar()
    }
}

class SpecialSymbolsGenerator(private val random: SecureRandom) : SymbolsGenerator {

    override fun generate(): Char {
        val randomIndex = random.nextInt(SPECIAL_SYMBOLS_STRING.length)
        return SPECIAL_SYMBOLS_STRING[randomIndex]
    }
}