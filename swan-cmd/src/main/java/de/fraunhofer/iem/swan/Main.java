package de.fraunhofer.iem.swan;

import de.fraunhofer.iem.swan.cli.CliRunner;
import picocli.CommandLine;

public class Main {

    public static void main(String[] args) {

        new CommandLine(new CliRunner()).execute(args);
    }
}
