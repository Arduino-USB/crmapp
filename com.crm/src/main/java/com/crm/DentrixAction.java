package com.crm;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.system.ApplicationHome;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DentrixAction {

    public static class ReturnData {
        public int returnCode;
        public String stdout;
        public String stderr;
    }

    /**
     * Helper class to securely convert Java Maps into a Base64 encoded JSON string
     * that can be safely unpacked inside PowerShell 7 as a hashtable.
     */
    public static class PowerShellArgEncoder {
        private static final ObjectMapper objectMapper = new ObjectMapper();

        public static String toBase64EncodedJson(Map<String, String> variables) {
            if (variables == null || variables.isEmpty()) {
                variables = Map.of();
            }
            try {
                String json = objectMapper.writeValueAsString(variables);
                return Base64.getEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_8));
            } catch (Exception e) {
                throw new RuntimeException("Failed to serialize runtime variables to JSON for PowerShell", e);
            }
        }
    }

    /**
     * Executes a Dentrix module script via PowerShell 7 with optional parameters.
     * 
     * @param moduleName The module name folder/script prefix inside DentrixScrape/modules
     * @param runtimeVars Map of runtime string variables to pass into the script
     * @return ReturnData containing return code (0 = success), stdout, and stderr.
     */
    public static synchronized ReturnData run(String moduleName, Map<String, String> runtimeVars) {
        ReturnData result = new ReturnData();

        if (moduleName == null || moduleName.trim().isEmpty()) {
            result.returnCode = -1;
            result.stdout = "";
            result.stderr = "No module name provided.";
            return result;
        }

        try {
            // Get directory where the JAR (or compiled class file) resides
            ApplicationHome home = new ApplicationHome(DentrixAction.class);
            if (home.getSource() == null) {
                throw new IllegalStateException("Unable to locate application home source path.");
            }
            
            Path jarPath = home.getSource().toPath();
            Path jarDir = jarPath.getParent();

            // Resolve paths
            Path pwshExePath = jarDir.resolve("powershell").resolve("pwsh.exe");
            Path scrapeApiScript = jarDir.resolve("ScrapeApi").resolve("ScrapeApi.ps1");
            Path modulePath = jarDir.resolve("DentrixScrape").resolve("modules").resolve(moduleName);

            // Validate that the target module folder/path exists
            if (!Files.exists(modulePath)) {
                throw new RuntimeException("Dentrix module does not exist at path: " + modulePath.toAbsolutePath());
            }

            // Encode runtime variables via JSON -> Base64 to avoid escaping issues
            String base64Vars = PowerShellArgEncoder.toBase64EncodedJson(runtimeVars);

            // Construct PowerShell snippet that decodes base64, converts to hashtable, and runs the script
            StringBuilder psCommand = new StringBuilder();
            psCommand.append("$decodedJson = [System.Text.Encoding]::UTF8.GetString([Convert]::FromBase64String('")
                     .append(base64Vars)
                     .append("')); ")
                     .append("$RuntimeVarsHashtable = $decodedJson | ConvertFrom-Json -AsHashtable; ")
                     .append("& '")
                     .append(scrapeApiScript.toAbsolutePath())
                     .append("' -ModulePath '")
                     .append(modulePath.toAbsolutePath())
                     .append("' -RuntimeVars $RuntimeVarsHashtable");

            // Build command list for ProcessBuilder using pwsh.exe
            List<String> commandList = new ArrayList<>();
            commandList.add(pwshExePath.toAbsolutePath().toString());
            commandList.add("-Command");
            commandList.add(psCommand.toString());

            ProcessBuilder builder = new ProcessBuilder(commandList);
            Process process = builder.start();

            // Read standard output
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                result.stdout = reader.lines()
                        .collect(Collectors.joining(System.lineSeparator()));
            }

            // Read standard error
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getErrorStream(), StandardCharsets.UTF_8))) {
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
