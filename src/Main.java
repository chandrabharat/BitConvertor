import java.math.BigInteger;
import java.util.HashMap;
import java.util.LinkedList;

public class Main {
    private String floatingPoint = "";
    private String risc_V = "";
    private String unsigned_int = "";
    private String signed_int = "";
    private String binary = "";
    private HashMap<String, String> intToRegister = new HashMap<String, String>() {
        {
            put("0", "x0");
            put("1", "ra");
            put("2", "sp");
            put("3", "gp");
            put("4", "tp");
            put("5", "t0");
            put("6", "t1");
            put("7", "t2");
            put("8", "s0");
            put("9", "s1");
            put("10", "a0");
            put("11", "a1");
            put("12", "a2");
            put("13", "a3");
            put("14", "a4");
            put("15", "a5");
            put("16", "a6");
            put("17", "a7");
            put("18", "s2");
            put("19", "s3");
            put("20", "s4");
            put("21", "s5");
            put("22", "s6");
            put("23", "s7");
            put("24", "s8");
            put("25", "s9");
            put("26", "s10");
            put("27", "s11");
            put("28", "t3");
            put("29", "t4");
            put("30", "t5");
            put("31", "t6");
        }};

    // Takes a binary value and interprets the remaining values
    public void acceptBinary(String binary) {
        this.binary = binary;
        signed_int = binaryToSigned(binary);
        unsigned_int = binaryToUnsigned(binary);
        binaryToFloatingPoint();
        binarytoRISC();
    }

    // Takes a decimal value and interprets the remaining values
    public void acceptInt(String num, boolean isSigned) {
        binary = intToBinary(num);
        if (isSigned) {
            signed_int = num;
            unsigned_int = binaryToUnsigned(binary);
        } else {
            unsigned_int = num;
            signed_int = binaryToSigned(binary);
        }
        binaryToFloatingPoint();
        binarytoRISC();
    }


    // Interprets a binary string as a 32-bit IEEE 754 Floating Point Number
     void binaryToFloatingPoint() {
        double mantissa_val = 0, mantissa_pow = 0.5;
        int exponent_val = 0, exponent_pow = 1;
        char sign_bit = binary.charAt(0);
        int sign_val = sign_bit == '0' ? 1 : -1;
        String exponent_bits = binary.substring(1, 9);
        String mantissa_bits = binary.substring(9);

        // Go through the mantissa region of the binary representation and convert into fraction value by doing mantissa pow * curr_bit
        // Mantissa pow will be 2^-n where n is the number of the current iteration and iterations start from 1
        for (int idx = 0; idx < mantissa_bits.length(); idx++) {
            char curr_bit = mantissa_bits.charAt(idx);
            int curr_val = curr_bit == '0' ? 0 : 1;
            mantissa_val += curr_val * mantissa_pow;
            mantissa_pow /= 2;
        }

        // Convert the exponent bit string to an integer val by starting from LSB and multiplying the curr bit by exponent_pow where
        // exponent_pow = 2^n where n is the amount of iterations complete
        for (int idx = exponent_bits.length() - 1; idx >= 0; idx--) {
            char curr_bit = exponent_bits.charAt(idx);
            int curr_val = curr_bit == '0' ? 0 : 1;
            exponent_val += curr_val * exponent_pow;
            exponent_pow *= 2;
        }

        // Depending on the exponent bits decide to interpret the binary rep as normal, denormalized, infinity, or NAN float
        // Bias is 2^(n) - 1, which would be 127 here because n == 7
        switch (exponent_val) {
            // Denormalized float
            case 0:
                // (-1)^sign_bit * 2 ^ (exp + bias + 1) * significand
                floatingPoint = Double.toString(sign_val * Math.pow(2, exponent_val - 127 + 1) * mantissa_val);
                break;
            // Nan or Infinity
            case 255:
                if (mantissa_val == 0) {
                    floatingPoint = sign_val == 1 ? "inf" : "-inf";
                } else {
                    floatingPoint = "NaN";
                }
                break;
            // Normal Float
            default:
                // (-1)^sign_bit * 2 ^ (exp + bias) * (1 + significand)
                floatingPoint = Double.toString(sign_val * Math.pow(2, exponent_val - 127) * (1 + mantissa_val));
                break;
        }
    }

    // Converts binary to Risc V by sending the binary string to the appropriate subroutine based on the opcode
     void binarytoRISC() {
        if (binary.length() != 32) {
            risc_V = "Invalid Instruction";
        }
        String opcode = binary.substring(25, 32);
        switch (opcode) {
            case "0000011":
            case "0010011":
            case "1100111":
                iType(opcode);
                break;
            case "0110011":
                rType();
                break;
            case "0100011":
                sType();
                break;
            case "1100011":
                sbType();
                break;
            case "0010111":
            case "0110111":
                uType(opcode);
                break;
            case "1101111":
                ujType();
                break;
            default:
                risc_V = "Invalid Instruction";
                break;
        }
    }

    // Uses the UJType format to create the RISC V instruction
     void ujType() {
        // Only UJ instruction is jal
        StringBuilder sb = new StringBuilder().append(binary.charAt(0));
        sb.append(binary, 12, 20);
        sb.append(binary.charAt(11));
        String immediate = sb.append(binary, 1, 11).toString();
        String rd_bits = binary.substring(20, 25);
        risc_V = "jal " + intToRegister.get(binaryToUnsigned(rd_bits)) + ","
                + Integer.toString(Integer.valueOf(binaryToSigned(immediate)) * 2);
    }

    // Uses the UType format to create the RISC V instruction
     void uType(String opcode) {
        // Don't need to account for immediate being in upper 20 bit position
        String immediate = binary.substring(0, 20);
        String rd_bits = binary.substring(20, 25);
        switch (opcode) {
            case "0010111":
                risc_V = "auipc ";
                break;
            case "0110111":
                risc_V = "lui ";
                break;
        }
        if (risc_V.length() <= 0) {
            risc_V = "Invalid Instruction";
            return;
        }
        // Include the Register Destination after converting to unsigned int
        risc_V += intToRegister.get(binaryToUnsigned(rd_bits)) + ",";
        risc_V += binaryToSigned(immediate);
    }

    // Uses the SBType format to create the RISC V instruction
     void sbType() {
        StringBuilder sb = new StringBuilder().append(binary.charAt(0));
        sb.append(binary.charAt(24));
        sb.append(binary, 1, 7);
        String immediate = sb.append(binary, 20, 24).toString();
        String funct3_bits = binary.substring(17, 20);
        String rs1_bits = binary.substring(12, 17);
        String rs2_bits = binary.substring(7, 12);
        switch(funct3_bits) {
            case "000":
                risc_V = "beq ";
                break;
            case "001":
                risc_V = "bne ";
                break;
            case "100":
                risc_V = "blt ";
                break;
            case "101":
                risc_V = "bge ";
                break;
            case "110":
                risc_V = "bltu ";
                break;
            case "111":
                risc_V = "bgeu ";
                break;
        }
        if (risc_V.length() <= 0) {
            risc_V = "Invalid Instruction";
            return;
        }
        // Include the Register 1 bits after converting to unsigned int
        risc_V += intToRegister.get(binaryToUnsigned(rs1_bits)) + ",";
        // Include the Register 2 bits after converting to unsigned int
        risc_V += intToRegister.get(binaryToUnsigned(rs2_bits)) + ",";
        // Include the immediate after converting to a signed int
        // Include the immediate bit string after converting to signed int remember that there is an implicit * 2
        risc_V += Integer.toString(Integer.valueOf(binaryToSigned(immediate)) * 2);
    }

    // Uses the SType format to create the RISC V instruction
     void sType() {
        StringBuilder sb = new StringBuilder(binary.substring(0, 7));
        String immediate = sb.append(binary, 20, 25).toString();
        String funct3_bits = binary.substring(17, 20);
        String rs1_bits = binary.substring(12, 17);
        String rs2_bits = binary.substring(7, 12);
        switch(funct3_bits) {
            case "000":
                risc_V = "sb ";
                break;
            case "001":
                risc_V = "sh ";
                break;
            case "010":
                risc_V = "sw ";
                break;
        }
        if (risc_V.length() <= 0) {
            risc_V = "Invalid Instruction";
            return;
        }
        // Include the Register 1 bits after converting to unsigned int
        risc_V += intToRegister.get(binaryToUnsigned(rs2_bits)) + ",";
        // Include the immediate bit string after converting to signed int
        risc_V += binaryToSigned(immediate) + "(";
        // Include the Register 2 bits after converting to unsigned int
        risc_V += intToRegister.get(binaryToUnsigned(rs1_bits)) + ")";
    }


    // Uses the RType format to create the RISC V instruction
     void rType() {
         String rd_bits = binary.substring(20, 25);
         String funct3_bits = binary.substring(17, 20);
         String rs1_bits = binary.substring(12, 17);
         String rs2_bits = binary.substring(7, 12);
         String funct7_bits = binary.substring(0, 7);

         switch (funct3_bits) {
            case "000":
                risc_V = funct7_bits.equals("0000000") ? "add " : risc_V;
                risc_V = funct7_bits.equals("0100000") ? "sub " : risc_V;
                break;
            case "001":
                risc_V = funct7_bits.equals("0000000") ? "sll " : risc_V;
                break;
            case "010":
                risc_V = funct7_bits.equals("0000000") ? "slt " : risc_V;
                break;
            case "011":
                risc_V = funct7_bits.equals("0000000") ? "sltu " : risc_V;
                break;
            case "100":
                risc_V = funct7_bits.equals("0000000") ? "xor " : risc_V;
                break;
            case "101":
                risc_V = funct7_bits.equals("0000000") ? "srl " : risc_V;
                risc_V = funct7_bits.equals("0100000") ? "sra " : risc_V;
                break;
            case "110":
                risc_V = funct7_bits.equals("0000000") ? "or " : risc_V;
                break;
            case "111":
                risc_V = funct7_bits.equals("0000000") ? "and " : risc_V;
                break;
        }
        if (risc_V.length() <= 0) {
            risc_V = "Invalid Instruction";
            return;
        }
        // Include the Register Destination bits after converting to unsigned int
        risc_V += intToRegister.get(binaryToUnsigned(rd_bits)) + ",";
        // Include the Register 1 bits after converting to unsigned int
        risc_V += intToRegister.get(binaryToUnsigned(rs1_bits)) + ",";
        // Include the Register 2 bits after converting to unsigned int
        risc_V += intToRegister.get(binaryToUnsigned(rs2_bits)) + ",";
    }

    // Uses the IType format to create the RISC V instruction
     void iType(String opcode) {
        String rd_bits = binary.substring(20, 25);
        String funct3_bits = binary.substring(17, 20);
        String rs1_bits = binary.substring(12, 17);
        String immediate = binary.substring(0,12);
        String funct7_bits = binary.substring(0, 7);
        switch (opcode) {
            case "0000011":
                inst0000011(rd_bits, funct3_bits, rs1_bits, immediate);
                break;
            case "0010011":
                inst0010011(rd_bits, funct3_bits, rs1_bits, immediate, funct7_bits);
                break;
            default:
                inst1100111(rd_bits, funct3_bits, rs1_bits, immediate);
                break;
        }
    }

    // Assuming the opcode is 1100111 set the risc v instruction
     void inst1100111(String rd_bits, String funct3_bits, String rs1_bits, String immediate) {
        // Must be JALR instruction ex jalr x0,ra,0
        if (funct3_bits.equals("000")) {
            risc_V = "jalr " + intToRegister.get(binaryToUnsigned(rd_bits)) + ","
                    + intToRegister.get(binaryToUnsigned(rs1_bits)) + "," + binaryToSigned(immediate);
        } else {
            risc_V = "Invalid Instruction";
        }
    }

    // Assuming the opcode is 0010011 set the risc v instruction
     void inst0010011(String rd_bits, String funct3_bits, String rs1_bits, String immediate, String funct7_bits) {
        switch (funct3_bits) {
            case "000":
                risc_V = "addi ";
                break;
            case "001":
                if (funct7_bits.equals("0000000")) {
                    risc_V = "slli ";
                    // Need to re adjust the immediate such that only the bottom 5 bits of the immediate encode the shift amount
                    // Added an extra zero in the front so that when calculating the immediate I can still use binary to signed method
                    // instead of binary to unsigned
                    immediate = "0" + immediate.substring(7,12);
                }
                break;
            case "010":
                risc_V = "slti ";
                break;
            case "011":
                risc_V = "sltiu ";
                break;
            case "100":
                risc_V = "xori ";
                break;
            case "101":
                if (funct7_bits.equals("0000000")) {
                   risc_V = "srli ";
                    // Need to re adjust the immediate such that only the bottom 5 bits of the immediate encode the shift amount
                    // Added an extra zero in the front so that when calculating the immediate I can still use binary to signed method
                    // instead of binary to unsigned
                   immediate = "0" + immediate.substring(7,12);
                } else if (funct7_bits.equals("0100000")) {
                    risc_V = "srai ";
                    // Need to re adjust the immediate such that only the bottom 5 bits of the immediate encode the shift amount
                    // Added an extra zero in the front so that when calculating the immediate I can still use binary to signed method
                    // instead of binary to unsigned
                    immediate = "0" + immediate.substring(7,12);
                }
                break;
            case "110":
                risc_V = "ori ";
                break;
            case "111":
                risc_V = "andi ";
                break;
        }
        if (risc_V.length() <= 0) {
            risc_V = "Invalid Instruction";
            return;
        }
        // Include the Register Destination bits after converting to unsigned int
        risc_V += intToRegister.get(binaryToUnsigned(rd_bits)) + ",";
        // Include the Register 1 bits after converting to unsigned int
        risc_V += intToRegister.get(binaryToUnsigned(rs1_bits)) + ",";
        // Include the immediate after converting to a signed int
        // Include the immediate bit string after converting to signed int
        risc_V += binaryToSigned(immediate);
    }

    // Assuming the opcode is 0000011 set the risc v instruction
     void inst0000011(String rd_bits, String funct3_bits, String rs1_bits, String immediate) {
        switch (funct3_bits) {
            case "000":
                risc_V = "lb ";
                break;
            case "001":
                risc_V = "lh ";
                break;
            case "010":
                risc_V = "lw ";
                break;
            case "100":
                risc_V = "lbu ";
                break;
            case "101":
                risc_V = "lhu ";
                break;
            case "110":
                risc_V = "lwu ";
                break;
        }
        if (risc_V.length() <= 0) {
            risc_V = "Invalid Instruction";
            return;
        }


        // Include the Register Destination bits after converting to unsigned int
        risc_V += intToRegister.get(binaryToUnsigned(rd_bits)) + ",";
        // Include the immediate bit string after converting to signed int
        risc_V += binaryToSigned(immediate) + "(";
        // Include the Register 1 bits after converting to unsigned int
        risc_V += intToRegister.get(binaryToUnsigned(rs1_bits)) + ")";
    }


    // Corresponds to Test 3
    // Accepts a binary string and converts it into an unsigned decimal value
     String binaryToUnsigned(String str) {
         // Will contain all the integers that need to eventually be added
         LinkedList<String> to_add = new LinkedList<>();
         long multiplier = 1;
        // Start from LSB and if current bit is 1 then store 2^(currentBit_index) to be added
        for (int idx = str.length() - 1; idx >= 0; idx--) {
            if (str.charAt(idx) == '1') {
                to_add.add(Long.toString(multiplier));
            }
            multiplier *= 2;
        }
        return binaryToUnisgned(to_add, "0");
    }

    // Corresponds to Test 3
    // Helper function for binaryToUnsigned and adds everything in to_add into total and returns total
    String binaryToUnisgned(LinkedList<String> to_add, String total) {
         // Base Case if to_add is empty return total
         if (to_add.isEmpty()) {
             return total;
         }
         // Current element from to_add that will be added
         String current = to_add.pop();
         // The sum of current and total will be stored in reverse order in this string builder
         StringBuilder new_total = new StringBuilder();
         int carry = 0;
         for (int idx = 0; idx < Math.max(total.length(), current.length()); idx++) {
             // Start from LSD and convert that character into an integer if idx is larger than or equal to string length default to 0
             int current_digit = idx < current.length() ? current.charAt(current.length() - 1 - idx) - '0' : 0;
             int total_digit = idx < total.length() ? total.charAt(total.length() - 1 - idx) - '0' : 0;
             // Append the sum of the digits and carry to the new total i.e 9 + 9 + 1 = 18 so 8 will be stored
             new_total.append((current_digit + total_digit + carry) % 10);
             // Calculate the new carry digit for the next digit addition i.e 9 + 9 + 1 = 18 so the carry digit would be 1
             carry = (current_digit + total_digit + carry) / 10;
         }
         // If carry digit remains append ot the sum
         if (carry == 1) {
             new_total.append(carry);
         }
         // Recursively call function until to_add is empty
         return binaryToUnisgned(to_add, new_total.reverse().toString());
    }

    // Corresponds to test 2
    // Accepts a binary string and converts it into a signed decimal value
    String binaryToSigned(String str) {
        int val = 0, multiplier = 1;
        // Start from LSB and multiply curr_bit by 2^n where n is the number of iterations already completed and add to val until
        // the MSB is reached
        for (int idx = str.length() - 1; idx > 0; idx--) {
            char curr_bit = str.charAt(idx);
            int curr_val = curr_bit == '0' ? 0 : 1;
            val += multiplier * curr_val;
            multiplier *= 2;
        }
        // Multiply the MSB by 2^(n - 1) where n is length of the binary string and subtract this value from the val computed above
        return Integer.toString(-1 * Character.getNumericValue(str.charAt(0)) * multiplier + val);
    }

    // Corresponds to test 1
    // Accepts a decimal string num and converts it to a 32-bit binary number
    String intToBinary(String num) {
        Integer decimal_val = Integer.valueOf(num);
        return String.format("%32s", Integer.toBinaryString(decimal_val)).replace(' ', '0');
    }

    // Helper method for testing binaryToRisc method that sets the binary bit string
     void insertBinary(String binary) {
         this.binary = binary;
    }

    // Helper method for testing that converts hex strings into binary strings
     void hexToBin(String s) {
        binary = new BigInteger(s, 16).toString(2);
    }

    public String getFloatingPoint() {
        return floatingPoint;
    }
    public String getRisc_V() {
        return risc_V;
    }

    public String getUnsigned_int() {
        return unsigned_int;
    }

    public String getSigned_int() {
        return signed_int;
    }

    public String getBinary() {
        return binary;
    }

    public void clear() {
        floatingPoint = "";
        risc_V = "";
        unsigned_int = "";
        signed_int = "";
        binary = "";
    }
}
