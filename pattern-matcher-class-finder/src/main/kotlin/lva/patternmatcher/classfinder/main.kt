package lva.patternmatcher.classfinder

import com.google.common.reflect.ClassPath
import com.google.common.reflect.ClassPath.ClassInfo
import lva.patternmatcher.MatchingResultSet.Matching
import lva.patternmatcher.PatternMatcher
import org.fusesource.jansi.Ansi
import org.fusesource.jansi.Ansi.Attribute.INTENSITY_BOLD
import org.fusesource.jansi.Ansi.Attribute.INTENSITY_BOLD_OFF
import org.fusesource.jansi.Ansi.Color.DEFAULT
import org.fusesource.jansi.Ansi.Color.RED
import org.fusesource.jansi.Ansi.Color.WHITE
import org.fusesource.jansi.AnsiConsole
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

/**
 * @author vlitvinenko
 */

private val FG_COLORS = arrayOf(WHITE, RED)

@ExperimentalTime
fun main() {
    val classPath = ClassPath.from(ClassLoader.getSystemClassLoader())
    val classNames = classPath.topLevelClasses.map { ClassName(it) }.toList()

    print("loading ... ")
    val matcher = PatternMatcher(classNames)
    print("done\n")

    AnsiConsole.systemInstall()
    try {
        while (true) {
            print("> ")
            val pattern = readLine()

            print("searching for '$pattern' ... ")
            val (resultSet, searchDuration) = measureTimedValue { matcher.match(pattern).resultSet }
            print("done\n")

            resultSet.forEach { (className, entries) ->
                val simpleName = className.simpleName
                val fullMatching = Matching(0, simpleName.length)
                val fullMatchings = fullMatching.split(entries.matchings)

                fullMatchings.forEachIndexed { i, matching ->
                    val matchedText = simpleName.substring(matching.from, matching.to)
                    print(matchedText, FG_COLORS[i % FG_COLORS.size])
                }

                print(" (${className.packageName})\n", WHITE, INTENSITY_BOLD_OFF)
            }

            print("\nfound ${resultSet.size} in ${searchDuration.inWholeMilliseconds} ms\n")
        }
    } finally {
        AnsiConsole.systemUninstall()
    }
}

private fun Matching.split(matchings: List<Matching>): List<Matching> {
    val splitted = ArrayList<Matching>(matchings.size * 2 + 1).apply { add(this@split) }

    for (matching in matchings) {
        splitted.last().takeIf { matching in it }?.let {
            splitted -= it
            splitted += it.splitByMatching(matching)
        }
    }

    return splitted
}

private operator fun Matching.contains(matching: Matching) =
    to >= matching.to

private fun Matching.splitByMatching(matching: Matching) = listOf(
    Matching(from, matching.from), Matching(matching.from, matching.to), Matching(matching.to, to)
)

private fun print(msg: String, fgColor: Ansi.Color = DEFAULT, attribute: Ansi.Attribute = INTENSITY_BOLD) {
    AnsiConsole.out.print(Ansi.ansi().fg(fgColor).a(attribute).a(msg).reset())
    AnsiConsole.out.flush()
}

private class ClassName(private val classInfo: ClassInfo) : Comparable<ClassName>, CharSequence by classInfo.simpleName {
    val simpleName: String by classInfo::simpleName
    val packageName: String by classInfo::packageName
    private val fullName = simpleName + packageName

    override operator fun compareTo(other: ClassName) =
        fullName.compareTo(other.fullName)
}
