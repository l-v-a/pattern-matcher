package lva.patternmatcher.classfinder

import com.google.common.reflect.ClassPath
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
            val (res, searchDuration) = measureTimedValue { matcher.match(pattern) }
            print("done\n")

            val resultSet = res.resultSet

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

            print("\nsearching time: ${searchDuration.inWholeMilliseconds} ms\nfound: ${resultSet.size}\n")
        }
    } finally {
        AnsiConsole.systemUninstall()
    }
}

private fun Matching.split(matchings: List<Matching>): List<Matching> {
    val splitted = ArrayList<Matching>(matchings.size * 2 + 1).apply { add(this@split) }

    for (m in matchings) {
        val last = splitted.last()
        if (m.to <= last.to) {
            splitted -= last
            splitted += Matching(last.from, m.from)
            splitted += Matching(m.from, m.to)
            splitted += Matching(m.to, last.to)
        }
    }

    return splitted
}

private fun print(msg: String, fgColor: Ansi.Color = DEFAULT, attribute: Ansi.Attribute = INTENSITY_BOLD) {
    AnsiConsole.out.print(Ansi.ansi().fg(fgColor).a(attribute).a(msg).reset())
    AnsiConsole.out.flush()
}

private class ClassName(private val classInfo: ClassPath.ClassInfo) :
    CharSequence by classInfo.simpleName,
    Comparable<ClassName> {

    val simpleName: String by classInfo::simpleName
    val packageName: String by classInfo::packageName
    private val fullName = simpleName + packageName

    override operator fun compareTo(other: ClassName) =
        fullName.compareTo(other.fullName)
}
