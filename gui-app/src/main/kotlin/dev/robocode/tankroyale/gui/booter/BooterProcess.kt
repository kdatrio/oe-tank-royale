package dev.robocode.tankroyale.gui.booter

import dev.robocode.tankroyale.gui.model.MessageConstants
import dev.robocode.tankroyale.gui.settings.MiscSettings
import dev.robocode.tankroyale.gui.settings.MiscSettings.BOT_DIRS_SEPARATOR
import dev.robocode.tankroyale.gui.settings.ServerSettings
import dev.robocode.tankroyale.gui.util.ResourceUtil
import kotlinx.serialization.decodeFromString
import java.io.*
import java.nio.file.Files
import java.nio.file.Paths
import java.util.concurrent.atomic.AtomicBoolean

object BooterProcess {

    private const val JAR_FILE_NAME = "robocode-tankroyale-booter"

    private val isRunning = AtomicBoolean(false)
    private var runProcess: Process? = null

    private var thread: Thread? = null

    private val json = MessageConstants.json

    private val botProcessIds = HashMap<Int, String>()

    fun info(): List<BotEntry> {
        val builder = ProcessBuilder(
            "java",
            "-jar",
            getBooterJar(),
            "info",
            "--dirs=${getBotDirs()}"
        )
        val process = builder.start()
        startThread(process)
        try {
            val entries = readInputLines(process).joinToString()
            if (entries.isBlank()) {
                return emptyList()
            }
            return json.decodeFromString(entries)

        } finally {
            stopThread()
        }
    }

    fun run(entries: List<String>) {
        if (isRunning.get()) {
            addBotsToRunningBotProcess(entries)
        } else {
            startRunningBotProcess(entries)
        }
    }

    private fun startRunningBotProcess(entries: List<String>) {
        val args = mutableListOf(
            "java",
            "-Dserver.url=${ServerSettings.serverUrl}",
            "-jar",
            getBooterJar(),
            "run",
            "--dirs=${getBotDirs()}"
        )
        entries.forEach { args += it }

        runProcess = ProcessBuilder(args).start()

        isRunning.set(true)
        startThread(runProcess!!)
    }

    private fun addBotsToRunningBotProcess(filenames: List<String>) {
        val printStream = PrintStream(runProcess?.outputStream!!)
        filenames.forEach { filename -> printStream.println(filename) }
        printStream.flush()
    }

    fun stopRunning() {
        if (!isRunning.get())
            return

        stopThread()
        isRunning.set(false)

        stopProcess()

        botProcessIds.clear()
    }

    private fun stopProcess() {
        val p = runProcess
        if (p != null && p.isAlive) {

            // Send quit signal to server
            val out = p.outputStream
            out.write("\n".toByteArray())
            out.flush()
        }
        runProcess = null
    }

    private fun getBooterJar(): String {
        val propertyValue = System.getProperty("booterJar")
        if (propertyValue != null) {
            val path = Paths.get(propertyValue)
            if (!Files.exists(path)) {
                throw FileNotFoundException(path.toString())
            }
            return path.toString()
        }
        val cwd = Paths.get("")
        val pathOpt = Files.list(cwd).filter { it.startsWith(JAR_FILE_NAME) && it.endsWith(".jar") }.findFirst()
        if (pathOpt.isPresent) {
            return pathOpt.get().toString()
        }
        return try {
            ResourceUtil.getResourceFile("$JAR_FILE_NAME.jar")?.absolutePath ?: ""
        } catch (ex: Exception) {
            System.err.println(ex.localizedMessage)
            ""
        }
    }

    private fun getBotDirs(): String {
        return MiscSettings.getBotDirectories().joinToString(separator = BOT_DIRS_SEPARATOR).trim()
    }

    private fun readInputToProcessIds(process: Process) {
        val reader = BufferedReader(InputStreamReader(process.inputStream!!))
        var line: String?
        while (run {
                line = reader.readLine()
                line
            } != null) {
            addProcessId(line!!)
        }
    }

    private fun readErrorToStdError(process: Process) {
        val reader = BufferedReader(InputStreamReader(process.errorStream!!))
        var line: String?
        while (run {
                line = reader.readLine()
                line
            } != null) {
            System.err.println(line)
        }
    }

    private fun readInputLines(process: Process): List<String> {
        val list = ArrayList<String>()
        val reader = BufferedReader(InputStreamReader(process.inputStream!!))
        var line: String?
        while (run {
                line = reader.readLine()
                line
            } != null) {
            list += line!!
        }
        return list
    }

    private fun startThread(process: Process) {
        thread = Thread {
            while (thread?.isInterrupted == false) {
                try {
                    readInputToProcessIds(process)
                    readErrorToStdError(process)
                } catch (e: InterruptedException) {
                    break
                }
            }
        }
        thread?.start()
    }

    private fun stopThread() {
        thread?.interrupt()
    }

    private fun addProcessId(line: String) {
        val pidAndName = line.split(":", limit = 2)
        if (pidAndName.size == 2) {
            val pid = pidAndName[0].toInt()
            val name = pidAndName[1]
            botProcessIds[pid] = name
        }
    }
}
