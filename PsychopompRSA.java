import java.io.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

// TODO: clean up

public class PsychopompRSA {

    private BigInteger encryptionKey, decryptionKey, modulusN;

    public void testRSA (BigInteger bigInt) {
        System.out.println(bigInt);
        BigInteger tmp = bigInt.modPow(encryptionKey, modulusN);
        System.out.println(tmp);
        BigInteger returnVal = tmp.modPow(decryptionKey, modulusN);
        System.out.println(returnVal);
    }

    public void autoTestRSA (BigInteger bigInt) {

    }

    public void encryptFile (File f) throws IOException {
        encryptFile(f, f);
    }

    public void decryptFile (File f) throws IOException {
        decryptFile(f, f);
    }

    public void encryptFile (String fileURL) throws IOException {
        encryptFile(fileURL, fileURL);
    }

    public void decryptFile (String fileURL) throws IOException {
        decryptFile(fileURL, fileURL);
    }

    public void encryptFile (String srcFileURL, String encFileURL) throws IOException {
        encryptFile(new File(srcFileURL), new File(encFileURL));
    }

    public void decryptFile (String srcFileURL, String decFileURL) throws IOException {
        decryptFile(new File(srcFileURL), new File(decFileURL));
    }

    // core
    public void encryptFile (File srcFile, File encFile) throws IOException {
        BigInteger fileData = readBigIntegerFile( srcFile );
        BigInteger message = fileData.modPow( this.encryptionKey, this.modulusN );
        saveBigIntToTxt( encFile, message );

        if (readTxtAsBigInt(encFile).equals(message)) {
            System.out.println("FILE CTRL");
        } else {
            System.out.println("FILE ERROR");
            System.out.println("written file can not be read as intended");
            System.out.println(readTxtAsBigInt(encFile));
            System.out.println(message);
        }

        // System.out.println( "debug 1\n" + fileData );
        // System.out.println( "debug 2\n" + message );

        /* tmp debug code
        writeBigIntegerFile( encFile, fileData ); // THIS WORKS FINE
        this.testRSA(fileData); // */

    }

    // core
    public void decryptFile (File srcFile, File decFile) throws IOException {
        BigInteger fileData = readTxtAsBigInt( srcFile );
        // message = fileData.modPow( this.decryptionKey, this.modulusN );
        BigInteger message = fileData.modPow(decryptionKey, modulusN);
        writeBigIntegerFile( decFile, message );

        // System.out.println( "debug 3\n" + fileData );
        // System.out.println( "debug 4\n" + message );

    }


    public static BigInteger concatenate(String txt){
        BigInteger returnValue = BigInteger.ZERO;
        char[] txtChars = txt.toCharArray();

        for (char c:txtChars) {

            returnValue = returnValue.shiftLeft( 16 ).add( BigInteger.valueOf( (int) c ) );
        }

        return returnValue;
    }

    public static String unConcatenate(BigInteger bigInteger) {
        StringBuilder returnValue = new StringBuilder();
        int nextInt, shift;

        while(bigInteger.bitLength() > 0){
            shift = bigInteger.bitLength() - (bigInteger.bitLength()-1) % 16 - 1;

            nextInt = bigInteger.shiftRight( shift ).intValueExact();
            returnValue.append((char) nextInt);
            bigInteger = bigInteger.subtract( BigInteger.valueOf(nextInt).shiftLeft( shift ) );
        }

        // returnValue.append((char) bigInteger.intValueExact());

        return returnValue.toString();
    }

    /*
    public static String getFileTxt(String fileURL) throws IOException {
        return getFileTxt(new File(fileURL));
    }

    public static String getFileTxt(File file) throws IOException {
        FileReader fileReader = new FileReader(file);
        StringBuilder stringBuilder = new StringBuilder();
        int c = fileReader.read();

        while (c > -1 && c != 65535) {
            stringBuilder.append((char) c);
            c = (char) fileReader.read();
        }

        fileReader.close();
        return stringBuilder.toString();
    }

    public static byte[] getFileBytes(File file) throws IOException {
        return Files.readAllBytes(file.toPath()); // TODO i should probably implement this instead of getFileTxt
    }

    public static void setFileTxt(File file, char[] chars) throws IOException {
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write(chars);
        fileWriter.flush();
        fileWriter.close();
    }

    public static void setFileTxt(String fileURL, char[] chars) throws IOException {
        FileWriter fileWriter = new FileWriter(fileURL);
        fileWriter.write(chars);
        fileWriter.flush();
        fileWriter.close();
    }
    */

    public static BigInteger readBigIntegerFile(File file) throws FileNotFoundException {

        BigInteger bigInteger = BigInteger.ZERO;


        try (FileReader fileReader = new FileReader( file )) {

            int next2bytes = fileReader.read();

            while (next2bytes != -1) {
                bigInteger = bigInteger.shiftLeft(16).add(BigInteger.valueOf(next2bytes));
                next2bytes = fileReader.read();
            }

        } catch (IOException e) {

            throw new RuntimeException(e);
        }

        return bigInteger;
    }

    public static void writeBigIntegerFile(File file, BigInteger bigInteger) throws IOException {

        int next2bytes, shift;

        try (FileWriter fileWriter = new FileWriter(file)) {

            while (bigInteger.bitLength() > 0) {

                shift = bigInteger.bitLength() - (bigInteger.bitLength() - 1) % 16 - 1;
                // System.out.println(bigInteger.shiftRight( shift ));
                next2bytes = bigInteger.shiftRight( shift ).intValueExact();
                bigInteger = bigInteger.subtract( BigInteger.valueOf( next2bytes ).shiftLeft( shift ) );
                fileWriter.append( (char) next2bytes );

                // System.out.println( "debug " + (char) next2bytes );

            }
        }
    }

    public static void saveBigIntToTxt (File file, BigInteger bigInteger) throws IOException {
        int next2bytes, shift;
        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(bigInteger.toString());
        }
    }

    public static BigInteger readTxtAsBigInt (File file) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        try (FileReader fileReader = new FileReader(file)) {
            int next2bytes = fileReader.read();
            while (next2bytes != -1) {
                stringBuilder.append((char) next2bytes);
                next2bytes = fileReader.read();
            }
        }
        return new BigInteger(stringBuilder.toString());
    }

    public static char[] bigIntToChars(BigInteger bigInteger) {
        StringBuilder returnValue = new StringBuilder();
        int nextByte, shift;

        while(bigInteger.bitLength() > 16){
            shift = bigInteger.bitLength() - (bigInteger.bitLength()-1) % 16 - 1;

            nextByte = bigInteger.shiftRight( shift ).intValueExact();
            returnValue.append((char) nextByte);
            bigInteger = bigInteger.subtract( BigInteger.valueOf(nextByte).shiftLeft( shift ) );
        }

        returnValue.append((char) bigInteger.intValueExact());

        return returnValue.toString().toCharArray();
    }

    public BigInteger getDecryptionKey() {
        return decryptionKey;
    }

    public BigInteger getEncryptionKey() {
        return encryptionKey;
    }

    public BigInteger getModulusN() {
        return modulusN;
    }

    public void setDecryptionKey(BigInteger decryptionKey) {
        this.decryptionKey = decryptionKey;
    }

    public void setEncryptionKey(BigInteger encryptionKey) {
        this.encryptionKey = encryptionKey;
    }

    public void setModulusN(BigInteger modulusN) {
        this.modulusN = modulusN;
    }

    public void generateKeys (int primeSize) {
        Random rng = new Random();
        generateKeys(primeSize, rng);

    }

    public void generateKeys (int primeSize, long seed) {
        Random rng = new Random(seed);
        generateKeys(primeSize, rng);

    }

    public void generateKeys (int primeSize, Random rng) {
        generateKeys (primeSize,  rng, false);
    }

    public void generateKeys (int primeSize, Random rng, boolean ctrl) {
        BigInteger p = BigInteger.probablePrime(primeSize, rng);
        BigInteger q = BigInteger.probablePrime(primeSize, rng);

        System.out.println(p.isProbablePrime(10000));
        System.out.println(q.isProbablePrime(10000));


        generateKeys(p, q, ctrl);
    }

    public void generateKeys (BigInteger p, BigInteger q) {
        generateKeys(p, q, false);
    }

    public void generateKeys (BigInteger p, BigInteger q, boolean ctrl) {
        // 1000000000000066600000000000001

        BigInteger e, d, n, lambdaN;

        // modulus
        n = p.multiply(q);

        // lambda(n) = lambda(pq) = lcm(lambda(p),lambda(q)) = lcm((p-1),(q-1)); lambda is the Carmichael function;
        // lambdaN = ( p.subtract(BigInteger.ONE) ).gcd( q.subtract(BigInteger.ONE) ); // idk why i need this

        // it just is
        e = BigInteger.valueOf(65537);

        // TODO: replace legacy code because the function literally checks every number until it finds a suitable one
        // TLDR TODO: replace legacy code
        d = legacy_generateDecryptionKeys(p, q, e, 1)[0];

        setDecryptionKey(d);
        setEncryptionKey(e);
        setModulusN(n);

        if (ctrl) {
            System.out.println("p\n" + p);
            System.out.println("q\n" + q);

            System.out.println("d\n" + d);
            System.out.println("e\n" + e);
            System.out.println("n\n" + n);
        }
    }

    public static BigInteger[] legacy_generateDecryptionKeys(BigInteger p, BigInteger q, BigInteger e, int length){
        BigInteger[] returnKeys = new BigInteger[length];
        BigInteger phiOfN = ((p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE)));
        ArrayList<BigInteger> possibleKeys = new ArrayList<>();
        BigInteger i = (BigInteger.ONE);
        BigInteger d;

        while (possibleKeys.size() < length) {
            d = ((i.multiply(phiOfN).add(BigInteger.ONE)).divide(e));     // d = (1*Φ(n)+1)÷e

            if ((i.multiply(phiOfN).add(BigInteger.ONE)).mod(e).compareTo(BigInteger.ZERO) == 0) {
                BigInteger mod = d.multiply(e).mod(phiOfN);

                if (mod.compareTo(BigInteger.ONE) == 0) {       // probably redundant if-statement
                    possibleKeys.add(d);

                    /* debug    System.out.println(i + " - " + possibleKeys.size() + " - " + d);
                    // System.out.println("-------" + d + "-------");
                    // System.out.println(d + " = " + i + " * " + e + "    " + possibleKeys);
                    // System.out.println(mod);
                    // */
                }
            }

            i = i.add(BigInteger.ONE);
        }

        for (int j = 0; j < possibleKeys.size(); j++) {
            returnKeys[j] = possibleKeys.get(j);
        }

        return returnKeys;
    }

    public static BigInteger[] legacy_generateKeys(BigInteger lowerBound, BigInteger upperBound) throws IOException {
        long time;
        Date date = new Date();
        time = date.getTime();
        Random rng = new Random(time);
        BigInteger primeP;
        BigInteger primeQ;
        BigInteger e;  // encryption key
        BigInteger d;  // decryption key
        BigInteger n;
        // unnecessary  BigInteger phiN;
        // BigInteger upperBound = BigInteger.valueOf(100_000);    // can be adjusted
        // BigInteger lowerBound = BigInteger.valueOf(10_000);
        BigInteger range = upperBound.subtract(lowerBound);
        BigInteger[] returnMe = new BigInteger[3];
        final BigInteger[] primes = new PsychopompPrimes().makeshiftBigSieveOfEratosthenes(0x7FFF_FFFF);
        int lowestOrigin = primes.length;   // for use in 'rng.nextInt(lowestOrigin, primes.length - 1)'

        // debug    System.out.println(Arrays.toString(primes));

        while (primes[lowestOrigin - 1].compareTo(lowerBound) > 0) {
            lowestOrigin--; // the lowest selectable number's index to be used in 'rng.nextInt(lowestOrigin, primes.length - 1)'
        }

        primeP = primes[rng.nextInt(lowestOrigin, primes.length - 1)];

        do {
            primeQ = primes[rng.nextInt(lowestOrigin, primes.length - 1)];
        } while (primeQ.compareTo(primeP.subtract((range.divide(BigInteger.valueOf(4))))) >= 0 && primeQ.compareTo(primeP.add(range.divide(BigInteger.valueOf(4)))) <= 0);
        // note:    while (primeQ > (primeP - (range / 4) ) && primeQ < (primeP + (range / 4) ))    ensures that the chosen primes aren't too similar

        n = primeP.multiply(primeQ);
        // unnecessary  phiN = (primeP.subtract(BigInteger.ONE)).multiply(primeQ.subtract(BigInteger.ONE));

        e = BigInteger.valueOf(65537);
        d = legacy_generateDecryptionKeys(primeP, primeQ, e, 1)[0];

        // debug    System.out.println(Arrays.toString(generateDecryptionKeys(primeP, primeQ, e, 15)));

        /* debug
        System.out.println("\ne " + e);
        System.out.println("d " + d);

        BigInteger x = BigInteger.valueOf(123574567);
        BigInteger y = x.modPow(e, n);
        BigInteger z = y.modPow(d, n);
        System.out.println("x " + x);
        System.out.println("y " + y);
        System.out.println("z " + z);

        System.out.println("\n" + e.multiply(d));
        System.out.println(phiN);
        System.out.println("" + (e.multiply(d)).mod(phiN) + '\n');
        // */

        System.out.println("encryption(" + e + ", " + n + ")");
        System.out.println("decryption(" + d + ", " + n + ")");

        returnMe[0] = e;
        returnMe[1] = d;
        returnMe[2] = n;

        return returnMe;
    }

}
