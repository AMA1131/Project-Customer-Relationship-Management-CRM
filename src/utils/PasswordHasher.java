package utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordHasher {
    public static String hash(String password) {
        try{
            //MessageDigest è una classe che serve per Hashare. scelgo l'algoritmo SHA-256
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            //converto la password in un array di bytes che poi passo al metodo digest che mi calcola l'hash e mi torna un array di 32 bytes firmati
            byte[] hashedPassword = digest.digest(password.getBytes());

            //converto ogni byte di hashed password in una stringa di esadecimale
            StringBuilder hexaString = new StringBuilder();
            for (byte b : hashedPassword) {
                //come dicevo prima, i byte di hashedPassword sono firmati quindi non
                // si puo convertire un valore b<0 in hexa, per quello si fa un operazione
                // & logica per convertirlo in byte non firmato e pio convertirlo in stringa
                String byteToHexaString = Integer.toHexString(0xff & b);

                // per i 32 bytes forniti dal hash, ogni byte -> 2 caratteri esadecimale.
                // quindi mi aspetto a 64char hexa. pero alcuni byte danno uno solo carattere
                // in quel caso dobbiamo aggiungere un 0a fianco il carattere
                if(byteToHexaString.length() == 1) hexaString.append("0");
                hexaString.append(byteToHexaString);
            }
            return hexaString.toString();

        } catch (NoSuchAlgorithmException e){
            throw new RuntimeException("Error while Hashing" + e);
        }
    }
}
