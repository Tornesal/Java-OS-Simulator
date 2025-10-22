package OS;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

// This class is used to convert the .txt files that contain instructions into the opcodes that the CPU can understand.
public class Parser {

    private int parseInstruction(String line) {

        // \\s+ is used to split the instruction into its parts regardless of number of whitespaces
        String[] parts = line.trim().split("\\s+");
        String opcodeString = parts[0].toUpperCase();

        // Gets the operand from the instruction
        int operand = (parts.length > 1) ? Integer.parseInt(parts[1]) : 0;

        // Switch case to translate the opcode into the corresponding integer
        int opcode = 0;
        switch (opcodeString) {

            case "ADD":
                opcode = 10;
                break;

            case "SUB":
                opcode = 20;
                break;

            case "LDA":
                opcode = 30;
                break;

            case "STR":
                opcode = 40;
                break;

            case "BRH":
                opcode = 50;
                break;

            case "CBR":
                opcode = 60;
                break;

            case "LDI":
                opcode = 70;
                break;

            case "YLD":
                opcode = 98;
                break;

            case "HLT":
                opcode = 99;
                break;

            // Default fallback
            default:
                System.out.println("Invalid opcode: " + opcodeString);
                break;

        }

        return (opcode << 16) | operand;

    }

    // This function parses the text file and puts the parsed instructions into an arraylist to be used by the CPU
    public ArrayList<Integer> parseFile(String filename) {

        ArrayList<Integer> code = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {

            String line;
            while ((line = reader.readLine()) != null) {

                // Ignore comments and empty lines
                if (!line.startsWith("#") && !line.trim().isEmpty()) {

                    int instruction = parseInstruction(line);
                    code.add(instruction);

                }
            }

        } catch (Exception e) {

            System.out.println(e.getMessage());

        }

        return code;

    }
}
