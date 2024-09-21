package com.amarnath.sicassembler

import java.util.Locale

data class Preset(
    val srcCode: String,
    val optabContent: String
)

val presets = listOf(
    Preset(
        srcCode = """
COPY	START   1000
-	    LDA	    ALPHA
-	    ADD	    ONE
-	    SUB	    TWO
-	    STA	    BETA
ALPHA	BYTE	C'CSE'
ONE 	RESB	2
TWO	    WORD	2
BETA	RESW	2
-	    END 	1000
    """.trimIndent(),
        optabContent = """
SUB     05 
CMP     03
LDA	    00
STA	    23
ADD	    01
JNC	    08
    """.trimIndent()
    )
)


fun decimalToHex(decimal: Int): String {
    return decimal.toString(16).uppercase()
}

fun processAssemblerPass1(srcCode: String, optabContent: String): Triple<String, String, String> {
    if (srcCode.isEmpty()) {
        return Triple("", "", "Error! Source code is empty.")
    } else if (optabContent.isEmpty()) {
        return Triple("", "", "Error! Operation table is empty.")
    }

    try {

        val savedLabels = mutableListOf<String>()
        val symtab = StringBuilder()
        val intermediateFile = StringBuilder()

        val srcLines = srcCode.lines().iterator()
        val optabLines = optabContent.lines()

        var startingAddress = 0
        var locationCounter = 0
        var opcodeFound: Boolean

        if (srcLines.hasNext()) {
            val firstLine = srcLines.next().split("\\s+".toRegex())
            val label = firstLine[0]
            val opcode = firstLine[1]
            val operand = firstLine[2]

            if (opcode == "START") {
                startingAddress = operand.toInt(16)
                locationCounter = startingAddress
            }

            intermediateFile.append("-\t$label\t$opcode\t$operand\n")
        }

        while (srcLines.hasNext()) {
            val lineParts = srcLines.next().split("\\s+".toRegex())
            if (lineParts.size < 2) {
                return Triple("", "", "Error! Syntax error: ${lineParts.joinToString(" ")}")
            }

            val label = lineParts[0]
            val opcode = lineParts[1]
            val operand = if (lineParts.size > 2) lineParts[2] else ""

            if (opcode == "END") {
                break
            }

            if (label != "-") {
                if (savedLabels.contains(label)) {
                    return Triple("", "", "Error! Duplicate label: $label found.")
                }
                savedLabels.add(label)
                symtab.append("$label\t${decimalToHex(locationCounter)}\n")
            }

            opcodeFound = false
            for (optabLine in optabLines) {
                val optabParts = optabLine.split("\\s+".toRegex())
                val optabOpcode = optabParts[0]

                if (optabOpcode == opcode) {
                    opcodeFound = true
                    break
                }
            }

            intermediateFile.append("${decimalToHex(locationCounter)}\t$label\t$opcode\t$operand\n")

            locationCounter += when {
                opcodeFound -> 3
                opcode == "BYTE" -> operand.length - 3
                opcode == "RESB" -> operand.toInt()
                opcode == "WORD" -> 3
                opcode == "RESW" -> 3 * operand.toInt()
                else -> return Triple("", "", "Error! Invalid opcode: $opcode found.")
            }
        }

        intermediateFile.append("${decimalToHex(locationCounter)}\t-\tEND\t-\n")
        val programSize = decimalToHex(locationCounter - startingAddress)

        symtab.append("\nProgram Size: $programSize\n")
        return Triple(symtab.toString(), intermediateFile.toString(), "")
    } catch (e: Exception) {
        return Triple("", "", "Error! ${e.message}")
    }
}

fun generateObjectCode(
    intermediateCode: String,
    symbolTable: String,
    operationTable: String
): Pair<String, String> {
    fun hexToInt(hexStr: String): Int {
        return hexStr.toInt(16)
    }

    fun lenify(num: Int): String {
        return num.toString(16).uppercase(Locale.ROOT).padStart(6, '0')
    }

    try {

        if (intermediateCode.isEmpty()) {
            return Pair("", "Error! Intermediate code is empty.")
        } else if (symbolTable.isEmpty()) {
            return Pair("", "Error! Symbol table is empty.")
        } else if (operationTable.isEmpty()) {
            return Pair("", "Error! Operation table is empty.")
        }

        val lines = intermediateCode.lines()
        val loc = mutableListOf<String>()
        val lab = mutableListOf<String>()
        val opc = mutableListOf<String>()
        val opr = mutableListOf<String>()

        for (line in lines) {
            if (line.isNotEmpty()) {
                val words = line.split(Regex("\\s+"))
                loc.add(words[0])
                lab.add(if (words[1] == "-") "" else words[1])
                opc.add(words[2])
                opr.add(words[3])
            }
        }

        val sym = mutableMapOf<String, String>()
        val symtab = symbolTable.lines()
        for (line in symtab) {
            if (line.isNotEmpty()) {
                val words = line.split(Regex("\\s+"))
                sym[words[0]] = words[1]
            }
        }

        val op = mutableMapOf<String, String>()
        val optab = operationTable.lines()
        for (line in optab) {
            if (line.isNotEmpty()) {
                val words = line.split(Regex("\\s+"))
                op[words[0]] = words[1]
            }
        }

        val start = if (loc[0] != "-") hexToInt(loc[0]) else hexToInt(loc[1])
        val end = if (loc.last() == "-") hexToInt(loc[loc.size - 2]) else hexToInt(loc.last())

        val header = "H^${lab[0]}^${lenify(start)}^${lenify(end - start)}"

        var length = 0
        var currentRecord = StringBuilder("T^${lenify(start)}^")
        val textRecords = mutableListOf<String>()
        val maxBytes = 30

        for (i in 1 until loc.size) {
            if (loc[i] == "-") break

            if (opc[i] in op) {
                length += 3
                if (sym.contains(opr[i])) {
                    currentRecord.append(op[opc[i]]).append(sym[opr[i]])
                } else {
                    return Pair("", "Error! Undefined symbol: '${opr[i]}' found.")
                }
            } else {
                when (opc[i]) {
                    "BYTE" -> {
                        val byteValue = opr[i].drop(2).dropLast(1)
                        val byteString = byteValue.map {
                            it.code.toString(16).uppercase(Locale.ROOT).padStart(2, '0')
                        }.joinToString("")
                        currentRecord.append(byteString)
                        length += byteValue.length

                        println("BYTE: $byteString")
                    }

                    "WORD" -> {
                        currentRecord.append(lenify(opr[i].toInt()))
                        length += 3
                        println("WORD: ${opr[i].toInt()}")
                    }

                    "RESB", "RESW" -> {
                    }
                }
            }

            if (length >= maxBytes) {
                currentRecord.insert(9, length.toString(16).uppercase(Locale.ROOT).padStart(2, '0'))
                textRecords.add(currentRecord.toString())
                currentRecord = StringBuilder("T^${lenify(hexToInt(loc[i]))}^")
                length = 0
            } else {
                if (currentRecord.last() != '^') {
                    currentRecord.append('^')
                }
            }
        }

        if (currentRecord.isNotEmpty()) {
            currentRecord.insert(
                9,
                length.toString(16).uppercase(Locale.ROOT).padStart(2, '0') + "^"
            )
            textRecords.add(currentRecord.toString().trimEnd('^'))
        }

        val endRecord = "E^${lenify(start)}"

        val result = StringBuilder(header)
        textRecords.forEach { result.append("\n$it") }
        result.append("\n$endRecord")

        return Pair(result.toString(), "")
    } catch (e: Exception) {
        return Pair("", "Error! ${e.message}")
    }
}