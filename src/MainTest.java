import org.junit.jupiter.api.Assertions;

class MainTest {


    // Test 1
    @org.junit.jupiter.api.Test
    void intToBinary() {
        Main main = new Main();

        // Checking zero case
        main.intToBinary("0");
        Assertions.assertEquals("00000000000000000000000000000000", main.getBinary());

        // Checking small number for zero padding
        main.intToBinary("12");
        Assertions.assertEquals("00000000000000000000000000001100", main.getBinary());

        // Checking largest positive 32-bit integer
        main.intToBinary("2147483647");
        Assertions.assertEquals("01111111111111111111111111111111", main.getBinary());

        // Checking smallest positive 32-bit integer
        main.intToBinary("-2147483648");
        Assertions.assertEquals("10000000000000000000000000000000", main.getBinary());

        // Checking negative one
        main.intToBinary("-1");
        Assertions.assertEquals("11111111111111111111111111111111", main.getBinary());

        // Checking random positive integer
        main.intToBinary("9864234");
        Assertions.assertEquals("00000000100101101000010000101010", main.getBinary());

        // Checking random negative integer
        main.intToBinary("-7823541");
        Assertions.assertEquals("11111111100010001001111101001011", main.getBinary());
    }

    // Test 2
    @org.junit.jupiter.api.Test
    void binaryToSigned() {
        Main main = new Main();

        // Checking zero case
        String res = main.binaryToSigned("00000000000000000000000000000000");
        Assertions.assertEquals(res, "0");

        // Checking negative case
        res = main.binaryToSigned("11110000100100110001000000000001");
        Assertions.assertEquals(res, "-258797567");

        // Checking positive case
        res = main.binaryToSigned("01110100000000010001000000000001");
        Assertions.assertEquals(res, "1946226689");

        // Checking negative 1
        res = main.binaryToSigned("11111111111111111111111111111111");
        Assertions.assertEquals(res, "-1");

        // Checking 12-bit val negative one case
        res = main.binaryToSigned("111111111111");
        Assertions.assertEquals(res, "-1");

        // Checking 12-bit val positive case
        res = main.binaryToSigned("011011100111");
        Assertions.assertEquals(res, "1767");

        // Checking 12-bit val negative case
        res = main.binaryToSigned("111001110111");
        Assertions.assertEquals(res, "-393");
    }

    // Test 3
    @org.junit.jupiter.api.Test
    void binaryToUnsigned() {
        Main main = new Main();

        // Checking zero case
        String res = main.binaryToUnsigned("00000000000000000000000000000000");
        Assertions.assertEquals("0", res);

        // Checking largest 32-bit value
        res = main.binaryToUnsigned("11111111111111111111111111111111");
        Assertions.assertEquals("4294967295", res);

        // Checking random bit pattern
        res = main.binaryToUnsigned("00100101110110000001101101101100");
        Assertions.assertEquals("634919788", res);

        // Checking random bit pattern pt 2
        res = main.binaryToUnsigned("10000100100100000000000000000001");
        Assertions.assertEquals("2224029697", res);

        // Checking 5 bit pattern odd
        res = main.binaryToUnsigned("01101");
        Assertions.assertEquals("13", res);

        // Checking 5 bit pattern even
        res = main.binaryToUnsigned("10100");
        Assertions.assertEquals("20", res);
    }

    // Test 4
    @org.junit.jupiter.api.Test
    void binarytoRISC() {
        // Testing all I type instructions supported by RV32I and will also use all registers over the course of the tests
        Main main = new Main();

        // Testing lb
        // lb x0,0(ra) Testing an offset of 0
        main.clear();
        main.insertBinary("00000000000000001000000000000011");
        main.binarytoRISC();
        Assertions.assertEquals("lb x0,0(ra)", main.getRisc_V());

        // lb sp,678(gp) Testing a positive offset
        main.clear();
        main.insertBinary("00101010011000011000000100000011");
        main.binarytoRISC();
        Assertions.assertEquals("lb sp,678(gp)", main.getRisc_V());

        // lb tp,-786(t0) Testing a negative offset
        main.clear();
        main.insertBinary("11001110111000101000001000000011");
        main.binarytoRISC();
        Assertions.assertEquals("lb tp,-786(t0)", main.getRisc_V());


        // Testing lh
        // lh t1,0(t2) Testing an offset of 0
        main.clear();
        main.insertBinary("00000000000000111001001100000011");
        main.binarytoRISC();
        Assertions.assertEquals("lh t1,0(t2)", main.getRisc_V());

        // lh s0,2047(s1) Testing a positive offset
        main.clear();
        main.insertBinary("01111111111101001001010000000011");
        main.binarytoRISC();
        Assertions.assertEquals("lh s0,2047(s1)", main.getRisc_V());

        // lh a0,-2048(a1)
        main.clear();
        main.insertBinary("10000000000001011001010100000011");
        main.binarytoRISC();
        Assertions.assertEquals("lh a0,-2048(a1)", main.getRisc_V());

        // Testing lw
        // lw a2,0(a3) Testing an offset of 0
        main.clear();
        main.insertBinary("00000000000001101010011000000011");
        main.binarytoRISC();
        Assertions.assertEquals("lw a2,0(a3)", main.getRisc_V());

        // lw a4,4(a5) Testing a positive offset
        main.clear();
        main.insertBinary("00000000010001111010011100000011");
        main.binarytoRISC();
        Assertions.assertEquals("lw a4,4(a5)", main.getRisc_V());

        // lw a6,-8(a7) Testing a negative offset
        main.clear();
        main.insertBinary("11111111100010001010100000000011");
        main.binarytoRISC();
        Assertions.assertEquals("lw a6,-8(a7)", main.getRisc_V());

        // Testing lbu
        // lbu s2,0(s3) Testing an offset of 0
        main.clear();
        main.insertBinary("00000000000010011100100100000011");
        main.binarytoRISC();
        Assertions.assertEquals("lbu s2,0(s3)", main.getRisc_V());

        // lbu s4,257(s5) Testing a positive offset
        main.clear();
        main.insertBinary("00010000000110101100101000000011");
        main.binarytoRISC();
        Assertions.assertEquals("lbu s4,257(s5)", main.getRisc_V());

        // lbu s6,-786(s7) Testing a negative offset
        main.clear();
        main.insertBinary("11001110111010111100101100000011");
        main.binarytoRISC();
        Assertions.assertEquals("lbu s6,-786(s7)", main.getRisc_V());

        // Testing lhu
        // lhu s8,0(s9) Testing an offset of zero
        main.clear();
        main.insertBinary("00000000000011001101110000000011");
        main.binarytoRISC();
        Assertions.assertEquals("lhu s8,0(s9)", main.getRisc_V());

        // lhu s10,1003(s11) Testing a positive offset
        main.clear();
        main.insertBinary("00111110101111011101110100000011");
        main.binarytoRISC();
        Assertions.assertEquals("lhu s10,1003(s11)", main.getRisc_V());

        // lhu t3,-972(t4) Testing a negative offset
        main.clear();
        main.insertBinary("11000011010011101101111000000011");
        main.binarytoRISC();
        Assertions.assertEquals("lhu t3,-972(t4)", main.getRisc_V());

        // Testing lwu
        // lwu t5,0(t6) Testing a zero offset
        main.clear();
        main.insertBinary("00000000000011111110111100000011");
        main.binarytoRISC();
        Assertions.assertEquals("lwu t5,0(t6)", main.getRisc_V());

        // lwu t3,2035(t6) Testing a positive offset
        main.clear();
        main.insertBinary("01111111001111111110111000000011");
        main.binarytoRISC();
        Assertions.assertEquals("lwu t3,2035(t6)", main.getRisc_V());

        // lwu t5, -999(t4) Testing a negative offset
        main.clear();
        main.insertBinary("11000001100111101110111100000011");
        main.binarytoRISC();
        Assertions.assertEquals("lwu t5,-999(t4)", main.getRisc_V());

        // All 32 registers have been tested for and i-type immediate seems to be generated correctly. Going forward I will
        // only be testing once per instruction for I-type instructions (in most cases)

        // Testing addi
        // addi s7,sp,7 Testing a positive immediate
        main.clear();
        main.insertBinary("00000000011100010000101110010011");
        main.binarytoRISC();
        Assertions.assertEquals("addi s7,sp,7", main.getRisc_V());

        // Testing slli
        // slli gp,t6,25 Testing a valid immediate
        main.clear();
        main.insertBinary("00000001100111111001000110010011");
        main.binarytoRISC();
        Assertions.assertEquals("slli gp,t6,25", main.getRisc_V());

        // slli gp,t6,1024 Testing an invalid immediate
        main.clear();
        main.insertBinary("01000000000011111001000110010011");
        main.binarytoRISC();
        Assertions.assertEquals("Invalid Instruction", main.getRisc_V());

        // Testing slti
        // slti a1,s1,-75 Testing a negative immediate
        main.clear();
        main.insertBinary("11111011010101001010010110010011");
        main.binarytoRISC();
        Assertions.assertEquals("slti a1,s1,-75", main.getRisc_V());

        // Testing sltiu
        // sltiu t3,x0,0 Testing a zero immediate
        main.clear();
        main.insertBinary("00000000000000000011111000010011");
        main.binarytoRISC();
        Assertions.assertEquals("sltiu t3,x0,0", main.getRisc_V());

        // Testing xori
        // xori s0,ra,474 Testing a positive immediate
        main.clear();
        main.insertBinary("00011101101000001100010000010011");
        main.binarytoRISC();
        Assertions.assertEquals("xori s0,ra,474", main.getRisc_V());

        // Testing srli
        // srli s0,ra,474 Testing an invalid immediate
        main.clear();
        main.insertBinary("00011101101000001101010000010011");
        main.binarytoRISC();
        Assertions.assertEquals("Invalid Instruction", main.getRisc_V());

        // srli s0,ra,31 Testing an invalid immediate
        main.clear();
        main.insertBinary("00000001111100001101010000010011");
        main.binarytoRISC();
        Assertions.assertEquals("srli s0,ra,31", main.getRisc_V());

        // Testing srai
        // srai s11,s2,1 Testing a positive immediate
        main.clear();
        main.insertBinary("01000000000110010101110110010011");
        main.binarytoRISC();
        Assertions.assertEquals("srai s11,s2,1", main.getRisc_V());

        // srai s11,s2,1 Testing an invalid immediate (funct7)
        main.clear();
        main.insertBinary("11000000000110010101110110010011");
        main.binarytoRISC();
        Assertions.assertEquals("Invalid Instruction", main.getRisc_V());

        // Testing ori
        // ori gp,t3,-4 Testing a negative immediate
        main.clear();
        main.insertBinary("11111111110011100110000110010011");
        main.binarytoRISC();
        Assertions.assertEquals("ori gp,t3,-4", main.getRisc_V());

        // Testing andi
        // andi sp,sp,12
        main.clear();
        main.insertBinary("00000000110000010111000100010011");
        main.binarytoRISC();
        Assertions.assertEquals("andi sp,sp,12", main.getRisc_V());

        // Testing jalr
        // jalr x0,a0,4
        main.clear();
        main.insertBinary("00000000010001010000000001100111");
        main.binarytoRISC();
        Assertions.assertEquals("jalr x0,a0,4", main.getRisc_V());

        // Testing all U type instructions supported by RV32I
        // Testing auipc
        // auipc t6,524287 Testing a positive offset
        main.clear();
        main.insertBinary("01111111111111111111111110010111");
        main.binarytoRISC();
        Assertions.assertEquals("auipc t6,524287", main.getRisc_V());

        // auipc t6,0 Testing a positive offset
        main.clear();
        main.insertBinary("00000000000000000000111110010111");
        main.binarytoRISC();
        Assertions.assertEquals("auipc t6,0", main.getRisc_V());

        // Testing lui
        // lui ra,-100000 Testing a negative offset
        main.clear();
        main.insertBinary("11100111100101100000000010110111");
        main.binarytoRISC();
        Assertions.assertEquals("lui ra,-100000", main.getRisc_V());

        // Testing all S type instructions supported by RV32I
        // sb sp,234(gp) Testing a positive offset
        main.clear();
        main.insertBinary("00001110001000011000010100100011");
        main.binarytoRISC();
        Assertions.assertEquals("sb sp,234(gp)", main.getRisc_V());

        // sh sp,-532(gp) Testing a negative offset
        main.clear();
        main.insertBinary("11011110001000011001011000100011");
        main.binarytoRISC();
        Assertions.assertEquals("sh sp,-532(gp)", main.getRisc_V());

        // sw sp,0(gp) Testing a zero offset
        main.clear();
        main.insertBinary("00000000001000011010000000100011");
        main.binarytoRISC();
        Assertions.assertEquals("sw sp,0(gp)", main.getRisc_V());

        // Testing all UJ type instructions supported by RV32I
        // jal ra,1048572 Testing a positive offset and the implicit 0 at the LSB in the immediate encoding
        main.clear();
        main.insertBinary("01111111110111111111000011101111");
        main.binarytoRISC();
        Assertions.assertEquals("jal ra,1048572", main.getRisc_V());

        // jal ra,-984 Testing a negative offset and the implicit 0 at the LSB in the immediate encoding
        main.clear();
        main.insertBinary("11000010100111111111000011101111");
        main.binarytoRISC();
        Assertions.assertEquals("jal ra,-984", main.getRisc_V());
    }
}