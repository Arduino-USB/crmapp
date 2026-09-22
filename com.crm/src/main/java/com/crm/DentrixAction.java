package com.crm;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.system.ApplicationHome;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

public class DentrixAction {

    // Tracks the global queue of waiting threads across all modules
    private static final List<Thread> globalQueue = new ArrayList<>();

    public static class ReturnData {
        public int returnCode;
        public String stdout;
        public String stderr;
    }

    public static class PowerShellArgEncoder {
        private static final ObjectMapper objectMapper = new ObjectMapper();

        public static String toBase64EncodedJson(Map<String, String> variables) {
            if (variables == null || variables.isEmpty()) {
                variables = Map.of();
            }

            try {
                String json = objectMapper.writeValueAsString(variables);
                return Base64.getEncoder().encodeToString(
                        json.getBytes(StandardCharsets.UTF_8)
                );
            } catch (Exception e) {
                throw new RuntimeException(
                        "Failed to serialize runtime variables to JSON for PowerShell",
                        e
                );
            }
        }
    }

    /**
     * Extracts text strictly between --JSON_START-- and --JSON_STOP-- to discard any Dentrix debug slop.
     */
    private static String extractJson(String rawOutput) {
        if (rawOutput == null || rawOutput.isBlank()) {
            return rawOutput;
        }

        String startMarker = "--JSON_START--";
        String stopMarker = "--JSON_STOP--";

        int startIndex = rawOutput.indexOf(startMarker);
        String jsonString = "";

        if (startIndex != -1) {
            int contentStart = startIndex + startMarker.length();
            int stopIndex = rawOutput.indexOf(stopMarker, contentStart);
            if (stopIndex != -1) {
                jsonString = rawOutput.substring(contentStart, stopIndex).trim();
            } else {
                jsonString = rawOutput.substring(contentStart).trim();
            }
        } else {
            jsonString = rawOutput.trim();
        }

        // NEW SAFETY STEP: Strip leading/trailing hyphens or garbage characters 
        // that might wrap the JSON string.
        if (jsonString.startsWith("-")) {
            jsonString = jsonString.substring(1);
        }
        if (jsonString.endsWith("-")) {
            jsonString = jsonString.substring(0, jsonString.length() - 1);
        }

        return jsonString.trim();
    }

    public static ReturnData run(String moduleName, Map<String, String> runtimeVars) {
        ReturnData result = new ReturnData();

        if (moduleName == null || moduleName.trim().isEmpty()) {
            result.returnCode = -1;
            result.stdout = "";
            result.stderr = "No module name provided.";
            System.err.println("[DentrixAction] ERROR: " + result.stderr);
            return result;
        }

        Thread currentThread = Thread.currentThread();

        // Enter global queue once and wait for turn
        synchronized (globalQueue) {
            globalQueue.add(currentThread);

            if (globalQueue.get(0) != currentThread) {
                int position = globalQueue.indexOf(currentThread);
                System.out.println("[DentrixAction] Operation " + moduleName + " is #" + position + " in the queue.");

                while (globalQueue.get(0) != currentThread) {
                    try {
                        globalQueue.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        result.returnCode = -1;
                        result.stdout = "";
                        result.stderr = "Operation interrupted while waiting in global queue.";
                        globalQueue.remove(currentThread);
                        return result;
                    }
                }
            }
        }

        int maxAttempts = 3;

        try {
            for (int attempt = 1; attempt <= maxAttempts; attempt++) {
                File stdoutFile = null;
                File stderrFile = null;

                try {
                    // Reset result for the current attempt
                    result = new ReturnData();

                    ApplicationHome home = new ApplicationHome(DentrixAction.class);

                    if (home.getSource() == null) {
                        throw new IllegalStateException(
                                "Unable to locate application home source path."
                        );
                    }

                    Path jarPath = home.getSource().toPath();
                    Path jarDir = jarPath.getParent();

                    Path pwshExePath = jarDir.resolve("powershell").resolve("pwsh.exe");
                    Path scrapeApiDir = jarDir.resolve("ScrapeApi");
                    Path scrapeApiScript = scrapeApiDir.resolve("ScrapeApi.ps1");
                    Path modulePath = jarDir
                            .resolve("DentrixScrape")
                            .resolve("modules")
                            .resolve(moduleName);

                    if (!Files.exists(modulePath)) {
                        throw new RuntimeException(
                                "Dentrix module does not exist at path: "
                                        + modulePath.toAbsolutePath()
                        );
                    }

                    String base64Vars =
                            PowerShellArgEncoder.toBase64EncodedJson(runtimeVars);

                    StringBuilder psCommand = new StringBuilder();

                    psCommand.append("Set-Location -LiteralPath '")
                            .append(scrapeApiDir.toAbsolutePath())
                            .append("'; ")

                            .append("$decodedJson = [System.Text.Encoding]::UTF8.GetString(")
                            .append("[Convert]::FromBase64String('")
                            .append(base64Vars)
                            .append("')); ")

                            .append("$RuntimeVarsHashtable = ")
                            .append("$decodedJson | ConvertFrom-Json -AsHashtable; ")

                            .append(". '")
                            .append(scrapeApiScript.toAbsolutePath())
                            .append("' -ModulePath '")
                            .append(modulePath.toAbsolutePath())
                            .append("' -RuntimeVars $RuntimeVarsHashtable; exit");

                    String encodedPwshCommand = Base64.getEncoder().encodeToString(
                            psCommand.toString().getBytes(StandardCharsets.UTF_16LE)
                    );

                    List<String> commandList = new ArrayList<>();
                    commandList.add(pwshExePath.toAbsolutePath().toString());
                    commandList.add("-Sta");
                    commandList.add("-NonInteractive");
                    commandList.add("-NoLogo");
                    commandList.add("-EncodedCommand");
                    commandList.add(encodedPwshCommand);

                    System.out.println("[DentrixAction] Running command via ProcessBuilder (Attempt " + attempt + "/" + maxAttempts + "):");
                    System.out.println("[DentrixAction] " + String.join(" ", commandList));

                    ProcessBuilder builder = new ProcessBuilder(commandList);
                    builder.directory(scrapeApiDir.toFile());

                    stdoutFile = File.createTempFile("pwsh_stdout_", ".log");
                    stderrFile = File.createTempFile("pwsh_stderr_", ".log");
                    builder.redirectOutput(stdoutFile);
                    builder.redirectError(stderrFile);

                    Process process = builder.start();

                    try {
                        process.getOutputStream().close();
                    } catch (Exception ignored) {}

                    result.returnCode = process.waitFor();

                    String rawStdout = Files.readString(stdoutFile.toPath(), StandardCharsets.UTF_8);
                    result.stdout = extractJson(rawStdout);
                    result.stderr = Files.readString(stderrFile.toPath(), StandardCharsets.UTF_8);

                    if (result.stdout != null && !result.stdout.isBlank()) {
                        System.out.println("[DentrixAction] Cleaned PowerShell JSON output:");
                        System.out.println(result.stdout);
                    }

                    if (result.stderr != null && !result.stderr.isBlank()) {
                        System.err.println("[DentrixAction] PowerShell ERROR:");
                        System.err.println(result.stderr);
                    }

                    if (result.returnCode != 0) {
                        System.err.println(
                                "[DentrixAction] PowerShell exited with code: "
                                        + result.returnCode + " on attempt " + attempt
                        );
                        if (attempt < maxAttempts) {
                            System.out.println("[DentrixAction] Retrying operation without leaving the queue...");
                            continue;
                        }
                    } else {
                        // Success! Break out of the retry loop
                        break;
                    }

                } catch (Exception e) {
                    result.returnCode = -1;
                    result.stdout = "";
                    result.stderr = e.toString();

                    System.err.println("[DentrixAction] Java ERROR on attempt " + attempt + ":");
                    e.printStackTrace();

                    if (attempt < maxAttempts) {
                        System.out.println("[DentrixAction] Retrying operation after exception...");
                    }
                } finally {
                    if (stdoutFile != null && stdoutFile.exists()) {
                        try { stdoutFile.delete(); } catch (Exception ignored) {}
                    }
                    if (stderrFile != null && stderrFile.exists()) {
                        try { stderrFile.delete(); } catch (Exception ignored) {}
                    }
                }
            }
        } finally {
            // Safely release queue lock only after all attempts (success or 3 failures) are complete
            synchronized (globalQueue) {
                globalQueue.remove(currentThread);
                globalQueue.notifyAll();
            }
        }

        return result;
    }
}
