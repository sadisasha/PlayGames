package com.anyks.playgames.playgamesutils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.Signature;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PlayGamesServicesInfo {

    public static String getSHA1CertFingerprint(Context ctx) {
        try {
            Signature[] signatures = ctx.getPackageManager().getPackageInfo(
                    ctx.getPackageName(), PackageManager.GET_SIGNATURES).signatures;
            if (signatures.length == 0) {
                return "ERROR: NO SIGNATURE.";
            } else if (signatures.length > 1) {
                return "ERROR: MULTIPLE SIGNATURES";
            }
            byte[] digest = MessageDigest.getInstance("SHA1").digest(signatures[0].toByteArray());
            StringBuilder hexString = new StringBuilder();
            for (int i = 0; i < digest.length; ++i) {
                if (i > 0) {
                    hexString.append(":");
                }
                byteToString(hexString, digest[i]);
            }
            return hexString.toString();

        } catch (PackageManager.NameNotFoundException ex) {
            ex.printStackTrace();
            return "(ERROR: package not found)";
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
            return "(ERROR: SHA1 algorithm not found)";
        }
    }

    static void byteToString(StringBuilder sb, byte b) {
        int unsigned_byte = b < 0 ? b + 256 : b;
        int hi = unsigned_byte / 16;
        int lo = unsigned_byte % 16;
        sb.append("0123456789ABCDEF".substring(hi, hi + 1));
        sb.append("0123456789ABCDEF".substring(lo, lo + 1));
    }
}
