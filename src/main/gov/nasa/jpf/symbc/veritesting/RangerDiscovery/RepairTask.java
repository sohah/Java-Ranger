package gov.nasa.jpf.symbc.veritesting.RangerDiscovery;

import gov.nasa.jpf.symbc.veritesting.RangerDiscovery.mutation.MutationResult;
import gov.nasa.jpf.symbc.veritesting.RangerDiscovery.mutation.ProcessMutants;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import static gov.nasa.jpf.symbc.veritesting.RangerDiscovery.Config.evaluationMode;

public class RepairTask implements Runnable {
    final MutationResult mutationResult;
    final String jpfConfigPath;
    String jpfConfigContent = new String("");
    Path jpfFile;
    int repairNum;

    public RepairTask(MutationResult mutationResult, String jpfConfigPath, int repairNum) {
        this.mutationResult = mutationResult;
        this.jpfConfigPath = jpfConfigPath;
        this.repairNum = repairNum;
        prepareJPFConfig();
    }

    @Override
    public void run() {
        callContractDR();
        clean();
    }

    private void callContractDR() {
        callContractDRWithProcessOutput();
/*
        String scriptName = "/home/soha/git/ranger-discovery/scripts/runMutationShellThread.sh";
        String commands[] = new String[]{scriptName, jpfFile.toAbsolutePath().toString(), String.valueOf(repairNum)};

        StringBuffer output = new StringBuffer();

        Runtime rt = Runtime.getRuntime();
        Process process = null;
        try {
            process = rt.exec(commands);
            process.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = "";
            while ((line = reader.readLine()) != null) {
                output.append(line + "\n");
            }
        } catch (Exception e) {
            System.out.println("Problem running ContractDR thread. Failing.");
            e.printStackTrace();
            assert false;
        }

        System.out.println(output.toString());*/
    }

    //used for debugging the process execution.
    public void callContractDRWithProcessOutput() {
        Process p;
        String scriptName = "/home/soha/git/ranger-discovery/scripts/runMutationShellThread.sh";

        String commands[] = new String[]{scriptName, jpfFile.toAbsolutePath().toString(), String.valueOf(repairNum)};
        ProcessBuilder pBuilder = new ProcessBuilder(commands);
        pBuilder.redirectErrorStream();

        try {
            p = pBuilder.start();
            InputStream in = p.getInputStream();
            final Scanner scanner = new Scanner(in);
            new Thread(new Runnable() {
                public void run() {
                    while (scanner.hasNextLine()) {
                        System.out.println(scanner.nextLine());
                    }
                    scanner.close();
                }
            }).start();
            try {
                int result = p.waitFor();
                int len;
                if ((len = p.getErrorStream().available()) > 0) {
                    byte[] buf = new byte[len];
                    p.getErrorStream().read(buf);
                    System.err.println("Command error:\t\"" + new String(buf) + "\"");
                }
                p.destroy();
                System.out.println("exit result: " + result);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void clean() {
        if (!evaluationMode)
            return;
        try {
            Files.deleteIfExists(jpfFile);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("jpfFile should exist at this point. Assumption Violated. Failing");
            assert false;
        }
    }

    private void prepareJPFConfig() {
        jpfConfigContent = new String("target=DiscoveryExamples.wbs.DiscoveryWBS\n" +
                "classpath=${jpf-symbc}/build/examples,${jpf-symbc}/lib/com.ibm.wala.util-1.4.4-SNAPSHOT.jar\n" +
                "sourcepath=${jpf-symbc}/src/examples\n" +
                "vm.storage.class=nil\n" +
                "#symbolic.debug=true\n" +
                "\n" +
                "\n" +
                "######## miscellaneous  ######\n" +
                "symbolic.method=DiscoveryExamples.wbs.DiscoveryWBS.discoveryLaunch(sym#sym#sym#sym#sym#sym#sym)\n" +
                "\n" +
                "symbolic.dp=z3bitvectorinc\n" +
                "\n" +
                "listener = .symbc.VeritestingListener,gov.nasa.jpf.symbc.numeric.solvers.IncrementalListener\n" +
                "#listener = gov.nasa.jpf.symbc.numeric.solvers.IncrementalListener\n" +
                "jitAnalysis = true\n" +
                "veritestingMode = 3\n" +
                "performanceMode = false\n" +
                "simplify = false\n" +
                "\n" +
                "\n" +
                "################## contract Discovery configurations ###############\n" +
                "spec = wbs\n" +
                "prop=3\n" +
                "SpecDirectory = WBS/Prop3\n" +
                "depthFixed = false\n" +
                "evaluationMode = true\n" +
                "\n" +
                "##### in discoveryMode=2, all the configurations are the same except for the faultySpec, it becomes the specification that we want to repair, also there will a new configuration for origSpec\n" +
                "origSpec=Prop3\n" +
                "executionMode=1\n" +
                "faultySpec=" + mutationResult.fileName);

        List<String> lines = Arrays.asList(jpfConfigContent);
        jpfFile = Paths.get(jpfConfigPath);
        try {
            Files.write(jpfFile, lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.out.println("problem writing to all statistics file");
            assert false;
        }
    }
}
