package com.vaadin.sass;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class SassCompiler {

    public static void main(String[] args) throws Exception {
        String input = null;
        String output = null;
        if (args.length == 0) {
            System.out
                    .println("usage: SassCompile <scss file to compile> <css file to write>");
            return;
        } else if (args.length == 1) {
            input = args[0];
        } else {
            input = args[0];
            output = args[1];
        }
        File inputFile = new File(input);
        ScssStylesheet scss = ScssStylesheet.get(inputFile);
        scss.compile();
        if (output == null) {
            System.out.println(scss.toString());
        } else {
            writeFile(output, scss.toString());
        }
    }

    public static void writeFile(String filename, String output)
            throws IOException {
        File file = new File(filename);
        FileWriter writer = new FileWriter(file);
        writer.write(output);
        writer.close();
    }
}
