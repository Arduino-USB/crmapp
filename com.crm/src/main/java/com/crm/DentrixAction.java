package com.crm;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DentrixAction {

    public static class ReturnData {
        public int returnCode;
        public String stdout;
        public String stderr;
    }

    /**
     * Queues and synchronously executes a Dentrix script.
     * Blocks execution if another thread is currently running a command.
     * 
     * @param scriptAndArgs The script filename followed by any optional arguments.
     * @return ReturnData containing return code (0 = success), stdout, and stderr.
     */
    public static synchronized ReturnData run(String... scriptAndArgs) {
        ReturnData result = new ReturnData();

        if (scriptAndArgs == null || scriptAndArgs.length == 0) {
            result.returnCode = -1;
            result.stdout = "";
            result.stderr = "No command provided.";
            return result;
        }

        try {
            // Get current working directory
            String currentDir = Paths.get("").toAbsolutePath().toString();
            
            // Build absolute path to: {currentDir}/DentrixUtils/{scriptName}
            Path scriptPath = Path.of(currentDir)
                    .resolve("DentrixUtils")
                    .resolve(scriptAndArgs[0]);

            // Assemble full command array: "cmd", "/c", scriptPath, arg1, arg2...
            List<String> commandList = new ArrayList<>();
            commandList.add("cmd");
            commandList.add("/c");
            commandList.add(scriptPath.toString() + ".exe");

            // Append any additional arguments provided after the script name
            if (scriptAndArgs.length > 1) {
                commandList.addAll(Arrays.asList(scriptAndArgs).subList(1, scriptAndArgs.length));
            }

            ProcessBuilder builder = new ProcessBuilder(commandList);
            Process process = builder.start();

            // Read standard output
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                result.stdout = reader.lines()
                        .collect(Collectors.joining(System.lineSeparator()));
            }

            // Read standard error
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getErrorStream()))) {
                result.stderr = reader.lines()
                        .collect(Collectors.joining(System.lineSeparator()));
            }

            // Wait for exit code (0 = success, non-zero = fail)
            result.returnCode = process.waitFor();

        } catch (Exception e) {
            result.returnCode = -1;
            result.stdout = "";
            result.stderr = e.toString();
        }

        return result;
    }
}
