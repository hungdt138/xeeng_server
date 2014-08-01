package com.tv.xeeng.game.data;

//import com.googlecode.compress_j2me.gzip.Gzip;

//import com.googlecode.compress_j2me.lzc.LZCInputStream;
//import com.googlecode.compress_j2me.lzc.LZCOutputStream;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.imageio.ImageIO;

import com.tv.xeeng.databaseDriven.DBPoolConnection;


public class Utils {
    private static ArrayList<Long> superUsers = new ArrayList<>();
            //long[] {481629, 30703, 8 , 9, 16, 17, 287018};
    public static void reloadSuperUser() throws SQLException{
        superUsers.clear();
        String query = "{ call getSuperUser() }";
        Connection conn;
        conn = DBPoolConnection.getConnection();
        try {
            CallableStatement cs = conn.prepareCall(query);
            ResultSet rs = cs.executeQuery();
            if (rs != null) {
                while (rs.next()) {
                    long uid = rs.getLong("userid");
                    superUsers.add(uid);
                }
                rs.close();
            }
            cs.clearParameters();
            cs.close();
        } finally {
            conn.close();
        }
    }
	public static boolean isSuperUser(long s){
            int length = superUsers.size();
            for(int i = 0; i< length; i++)
            {
                if(superUsers.get(i)== s)
                {
                    return true;
                }
            }
		
            return false;
	}
        
        public  BufferedImage resizeImage(BufferedImage originalImage, int type, int width, int height) {
            BufferedImage resizedImage = new BufferedImage(width, height, type);
            Graphics2D g = resizedImage.createGraphics();
            g.drawImage(originalImage, 0, 0, width, height, null);
            g.dispose();
            return resizedImage;
        }

        public  String convertImageBase64(BufferedImage imageBuf) throws IOException {
            ByteArrayOutputStream f = new ByteArrayOutputStream();
            ImageIO.write(imageBuf, "jpg", f);
            byte[] arrByte = f.toByteArray();
//            ByteArrayInputStream in = new ByteArrayInputStream(arrByte);
//            f = new ByteArrayOutputStream();
//            LZCOutputStream.compress(in, f);
//            arrByte = f.toByteArray();
            
            return new String(com.sun.midp.io.Base64.encode(arrByte, 0, arrByte.length));

        }
            
            
            
         private static Cipher cipherEncrypt;
         private static Cipher cipherDecrypt;
        static{
            initSignature();
        }
    
        private static void initSignature()
        {
            try {
                byte[] rawKey = "9jW4fv0bqn6z6cCd".getBytes();
                SecretKeySpec skeySpec = new SecretKeySpec(rawKey, "AES");
                cipherEncrypt =  Cipher.getInstance("AES/ECB/PKCS5Padding", "SunJCE");
                cipherEncrypt.init(Cipher.ENCRYPT_MODE, skeySpec);
                
                cipherDecrypt =  Cipher.getInstance("AES/ECB/PKCS5Padding", "SunJCE");
                cipherDecrypt.init(Cipher.DECRYPT_MODE, skeySpec);
                

            } catch (InvalidKeyException ex) {
//                mLog.error(ex.getMessage(), ex);
            } catch (NoSuchAlgorithmException ex) {
//                mLog.error(ex.getMessage(), ex);
            } catch (NoSuchProviderException ex) {
//                mLog.error(ex.getMessage(), ex);
            } catch (NoSuchPaddingException ex) {
//                mLog.error(ex.getMessage(), ex);
            }

        }
        
        public static Cipher getCipherEncrypt()
        {
            return cipherEncrypt;
        }
        
        public static Cipher getCipherDecrypt()
        {
            return cipherDecrypt;
        }
        
        
        public static String convertToHexa(byte[] messageDigest)
        {
            //get hexa format
            StringBuilder hexString = new StringBuilder();
            for (int i=0;i<messageDigest.length;i++) {
                    hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            }
            
            return hexString.toString();
            
        }
        
//        public static String convertToStr(String hexa)
//        {
//            //get hexa format
//            StringBuilder hexString = new StringBuilder();
//            for (int i=0;i<messageDigest.length;i++) {
//                    hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
//            }
//            
//            return hexString.toString();
//            
//        }
         
            
            
         public static void main(String[] args)
         {
//             BufferedImage img = null;
//            try {
//                img = ImageIO.read(new File("chiec-non.png"));
//                
//                File fi = new File("kimcuongLine.png");
//                byte[] fileContent = Files.readAllBytes(fi.toPath());
//                
//                
//                
////                 System.out.println(HexDecoder.encode(fileContent));        
//               
////                System.out.println(convertImageBase64(img));
//                //System.out.println(new String(com.sun.midp.io.Base64.encode(fileContent, 0, fileContent.length)));
//                StringBuilder param = new StringBuilder();
//                        param.append("1103874").append("&").
//                                append("68").append("&")
//                                .append("098888");
//                        
////                        link = link + param.toString();
//                byte[] encrypted = null;
//            try {
//                encrypted = Utils.getCipherEncrypt().doFinal(param.toString().getBytes());
//            } catch (IllegalBlockSizeException ex) {
//                Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
//            } catch (BadPaddingException ex) {
//                Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
//            }
//            
//                String hello =  new String(new sun.misc.BASE64Encoder().encode(encrypted));
//                
//                System.out.println(hello);
//                byte[] arrByte = org.apache.commons.codec.binary.Base64.decodeBase64("IJspfGqa8hVC+qiCEVu+PYMeHAfOXlH9KurDROclzqs=");;
//                int i = 1;
//            } catch (IOException e) {
//            }
             
//             LZCInputStream stream = new LZCInputStream(null)
//             Gzip gzip = new Gzip();
//             String data = "iVBORw0KGgoAAAANSUhEUgAAAcIAAACWCAMAAAB6rngiAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAC1QTFRF9K19kZ2gUzo2cpo01ikqQkKY7N3LAASVD5sA/rka83Ah/wAAAAAA////////TfJkPAAAAA90Uk5T//////////////////8A1NyYoQAAJWtJREFUeNrMXYli3KoOlQArGSfc///chwS2WcRiz7R9bm+Sm7YZDQftG/z35kPyeP7FH+Lz3z99EkHlE2n7x1RlD5PzGargLZqagzoP7B8dlxzO6PlXMHbpYhzpX0A4Pap/cVwXTXy3UJ4tfiLM6P3LdF1nJbwHQMev7BDfuPPwiKbrdWFvn5y2v3Za6RX5pRm57at+NkbzOkz6q2TxpQKA3bmf8Fse/sq5PXw3I/2vQHi+HuI+ek7K/gaK8aXChQqHgi16OY4b0vG36W/hF15rT8BpjwD5/GrBsztF4Z7bffYEGP8GikKV4Beu9s8IwQzGP07WeVaI1hjzM3wC6Ts9JAoeMCDGi77ViJGCoj3fyZ86KvnpAOctdyVg4a41oG6Gpf0fBfF424jG/IanZkLYT4l6PgeIN6lah/D46eeJlGzIL69xov22fxJE+cGQn4YrEEO/hb9Rg2gzGUF/DsCA3296jKvQCofloWLFTD78AQgPBswPw5RCE7yKID9IfwbEJECrgyggpI2whnBzp+EllsanAUxnZU4AGwgdkw1UQmg2kfJ35SncuFVUnQSWCIJmnX6nx/4BuZU4sNYrpkCLedDXgjSj238axMNWyvBrJSm7FL7mQnOazrcuPDwGsObCYDYPIPw+5eknj0oBsFKG4l5sQ8I/TlWgy1YABjYsaRRG3Mvv4XXH8IY4hdVrpRh6W2V+7kMITxA/pm1UAGsIUaHbVm7s54S8/KhCguoQsjkzpHtLZvYnIOwCWN5muctjBBnETzEiU3UAWFt2buZWbG0s4jNCPl5RBb9GGYogrbjQlHRvqzcLlq5V51DslA2/6ycK5U+YMSdy4CuzIKd2ww2HcjRXiZ9wT1FHsFKGQMGYAejr8EOPL9wsWGfBIKAH11k3ZxoID7vmzctOlE4kSNMf511XIkXjfiRHD6foXUaMCNrf3xUInZjS+0R4RJVI70BI2fsPZhIRjiw7aiWp/VYefBPDQgvGpE0Xwo3Jr3yK06Uo4xFAb5GVtOAFWc2OhTLk+17pQ1PHkNKpT28WzGy+gwX9hq11nt3nIBZak/Rbfew7h8WXPTNjAgc6joz2uFBOdhvIUfKnPwtvkOUbITpyK4Baa6byhVIIBXGajYIxVaceCdeZfGOdu8wi0ARpA15iy+fCtEKQ7/NP7WHlIknRhbbU30C0v4th1PAlZOixb8+EF3ajiMRGX4famgpTWBGi8YcqRs1WWnVTOerpTWGquBLBMAiM5PQLLRZByYVbacZAHtoVYfoBIcoANphmknSXt7/3bDChm9CfKmx4VjC6V9Ogv8l1YcuFjTmK/hvtG8KU/1HtQwQO9I46klRia+X7yOUoCN2VVXPbqImWUI4WI2B+CXsQ8pWrhKkrzegvTm5uSxjCXA2eF4N/GA64cKoK6dsHr+K5QhQerHUIG1J9J7nlQpvzIPkdFMv0PoJlOMZGy3TrKUOIOcS+X19GJcQypXsQKgjy1ZB6Bl2vaK59LUeR3ULEx0YN/wNsUjRBrzRxjkIZYtceDRzY3Du4qxCFb1VfAnxh0eTKkNMke9ev33Tv4g6EIq9+mtAUOxWVX5FJ0rlTSOLaZ/9/77AoKpwmeeqIIzROVyxszfTkqERIQPMQb8hSkQwvBUJkNwt1LuQ8fTccwWGZFsQ+H0IXwZ8Gw2Cf1j98GyXtWyasMb2DIR1We41hcCoOihuxFGP+uhwNupuLkNRQzTKGgvfr1WBo+K39IqrKUKR1YUUXciNYo55a/umdFfQRbBPg7GSUcmkbRGdsjZe1b2BIp9FnalHqgjR0hZJ0Wbrwq/Rmc+0dMKROpOYGVYxgg6Ehw5qiNFPdCSGEiwddY8ZvG30t8yGo/qBr0h/nj6m0oc2r1oZM6L+tt4+dfMrNdlNDGIRpyYVbEWbU75zX02OHTbMcbX+9NAyDb89Z+y817Qtcj5g7R6aIy4jAUcJt+lmBhuDei/q02tCcEE7kaEAr+BT2YbCNyhtd+xUc9dZDHVvl2ZuyVLIDoV2kiql/vXQMxbUvtaG7rJmqpq3wZCmgpSVYOnYpKGR1C1EUbWi6grRES+JPlmoufMFa4qKWSVVkIyvP1Bxl3YTWi2Cveh9al6IahoHecEELZ8NkPkXh2WfHHEQ/euw44SpV0JIFg0qURhuegsnXd9qWPMi6sGZCG973kg1PTQ6g4kMOd5CiW9gezS3SrYrMdM0wXCCLSgQbPiRxKpSbxw5F4VSY/IixTQkd+RXQMISWqmECtdGGXUHahNaw5sH4vhcwJO/rmHGduyFXpJxcHuX2mtDoOEJXxdbULI3uRPGUREp0BjNGPJThLsU1uyY0ti954S+tbjIZyxMIG6VSY9hoQ5NZAH0mtN+sClse5Gdq0ogpM8y/AYXfhUGzXbI/v9P2EqMwcYVmJo2CYIUhkhHn8Lp+JteFmvVFHFHRikVs5vAMIazcKwXDRhvmxRfUgZDfyLdFjQcPDGeKcJxDdWU1cC6aCggvsb9DxxwtPB5aM0YzUVqovig9TLeApnEpdJewyFK3GEKFIEzqibDJgV8GXleOkrVel6IrolQTo7V/6ByUxDuV4LJ0rStEr0IfWleEGoab+f3C3A7TOytMVvQUXIptWK9FEwjhZ1oTpuebgm1QVnOXrgN+V6ZM8b6HGIqTpNcyFMFuCChqJOfWzCX1aSGghBOqXq8ZhinpdDm0LoaT9l6Kml3CJqm3NbGjLoSKIjzu+Fcdp6mORXgwt/BsdRjkNTX4WlCHHTFaXWqCoA1zS+z0CjOLdMtCa37Cg2NRqotRVZbaX1spw1i5Cq35rIXl21qfmipoxKhTyjNLDNErwsmXPRVNwYzt8iA/fnRY3aKwTLdArQQ2JeO7Ze4EzBEciNLIhOwLKxgWiUPDH+q4EhBk8ZPtdCeWSib3ynSARoyyzzsQ13Isedzxutq5kZedQ1s7077tvii9FOFYH9ZJKHO1om2tS6EJUqtWatFAjEIWm9H50AYJYnJl7pLMcFftkzMnayj1umYawoXWGi10imI1YayjKSGEsrPJKsUWAwT7VmkmRlVuNL3Uoaa/U4rJl9UWg2o7q5N1itHwkRSdeGKIKLpwO0uh0pWDPDjjsnK7hhHVVFBJFFRhGZHT4Fq7xpmqRnUrXZays8kW0ginCPbYML/ARjVr8nKGPDjouiKJAhO2clStl5QgTd+WCR/JvzTf4iDOmICi/d0ol6SBAXdq5KhEkmil5FXy6yqEYsuwZcfBjlaYZhhuuqWuytFvJMwx1BHssWH4pr30CmrZ8SzwSHnKqSuTWBFOy3xGDv7BhOCZD0EVpnXI+2TD1poxh+GMTX5CqztvRCnUsVFuXOTDaAsdf7IMDo5fx2ZWDIXfOEVQt2gKl9ATW3hbTx+yGMh1gOl5VyLFlngwWTRdhyKIUXrpzkVul5o8f8/Ecm99JS6iR4ijhodDgNRseEFIp10gZX2ac3FgiDNxnV1i5CrwBQRfoB8WXjfZyHWmvixVTfVaaUPp/Qx5UGVDyjNMp10DXbuUWJIg9pz77fQI8WuMoGSofcOGoCQoUom7a6vF9BzOyENm4YB9Z2LMhpUm5A/IVg31at3D5YYuhOaotGvakQcIKmxYevUCHZEiTrNQ6e8gxpaFZorI5dZIN4mepOhuRhWcpntZ8e+CRCW/hqEZhqmmPEi+pw0Lr34zMRHOv03lY5jLoKHSuVcjxbCGYPRla0+6YMIkTr2A6F+j1FMHwqJqZmTJ8GgAGegDVYwG1CxhULk/QR8qdmkfQujm60c8CNE0V9mwyRIG+ILKYFYMvOgbDKHstsBtSwOgxDW8qg9pCUFMlnRtlDahNZLgIn+gFQzjyCCZrzJITpe9O1IaSYUh1kLYhNbENnBQf99pU5XgHKVH8hL6mXTsUOiyIR9Wadjhb1CF5AXMtto9KyhyDs7hfvwxmMV2H2QHFQ7EI0NdsqEWWmPW8Iph05S1GbPBOUgvDoMyOoIltSwQwx3JDemLDUFlwuhd8OWqG79qDDdshrER2TUEIbxrYPyOQ6nYMPcoTrOUlaHoxIGAckDKIEScdpLnAOI3dw+0IRo1vh3I9+xjUCVMS/JlmAWVA/RwIajmJXpYV0VQDWEb3+ZeJcG+smpcwYD6MD1COw3JhLctF9eLaX6wIQ2YMOZvuOuLox1Y5aBMkXfqkLXGg9GGDm/Ct5HSXnw7oCMg9koxjFUnDxJptWq2Cah51rV6xgJaYyZrGgIntwV6obbBFEScICgilN90diJliKaXojCc2hZRyh2PNYZ9AOOMk3lQ7Ts175zeUH6zGmOmuJKNn5/cQ2MsDQZGYn8YjCg/EhlaxSMKCKmXJ4zTF3+aKUVTAOPgjj6CLHhEhL7KS+3Lwxq0PUdHn11mk2PoYDZjk2CIoOUCA/5r/DW2fkU3TyiMGC6m6h/aCVWoJTXFRJTGK9IKtfidZhBSpyCAYsTDKaknnM8jxUF6EKIITX6VFmUbZpkCept4+yav1DRjFkx3HvoIBg4k69FyF13p3s/k6EF+FCaU86ExOKcK9UrXgF2vzu5kQxjI0dOqkeqUvejDWUDwZESrcCD7wm31UC5JR6le6YP+3ZgTWZtcPWF+5cGOIWOlL5M7d8IPsmrqd8SEx32s3Au7RhWqtUjdnoHMr4AoR/vTMqM146rKlLWzihjaGiWWNh5Iu8/lYVk7YMMYuqHMPcRFqmKIVLFDAwcyDwZViJ0IzQTCI1TBduBldy8StRWt7wSxmAX2HhfuSThAkljGudG8TCoiyKsICoaVFBU7V+4p6KLohFCYtItiINn8Ft6FXaYqYKj0Pn4LB9o8pJvXoh9RLGvHccLYL3EqxRtEYWaKSt9cYEMYdI5R9CtAxHvsGDKuL05ZeR0BhXUEq1JLindU5ozAJNZN1z+12kCszSRxen7jBlW+cV1FcQsHWlRj3XQwYSqM7QbsmezLT8I7Z2Xt1UAehCgBjUf2+gRhkdDRUXSsVemwaxZshpyuHJ8YTgsuPU1j3VW7gumMxdpywXrjyXASqSnlWdZ76gXbDggvG83aoTA9r+SNs7qiuEEDAgGMhy5HSQqN2dCdQeyS1XOPrISEsJSn0yGcQdgqnZFivI1gjiEG84W9k2yoSi9dwfGiskq9J0zjW7Q3iTqsZU4qeQ/TwdkXhG0sz6n68Bq4ceeBU8dH8QkwtAXiYelA95jxd8Fu74tSLvj+Lt2IjjIMn9pROhqEcMuSyc7qUnNTAA+3Av7rzO5r7BuKboW7TRalSiHxfGfmHJwQko60zoybv/1QXqnMBV22nzNMvWrUEbQ9ZqT7RNljJNW+DmGv3L0peU+hUnhwWPHtUArIDJ+jsa8bx1KZ0dADqjCbh4OEXfjsy75OCPt/rSUTH1Dl+80CPXsGJh50LVP3B1T5kw1hjN9ujbnu+xDpAsX7YrRiQ+zDlwuHWhVOUKRHZ7UvPoE4K/bMDMIKRfeILHrNVGCEL1Wa0JoLfYlU84iqLkcdgNjSzJpBWFup8IgowjX4LmcHFGtmAOMzCP2M+8zFUyg3i6b/Js8EPGLCngOhS0WxZxRrpo/iMwTnbJhbzeLsQHcSsYbiQwQHHBVIaluB1iGMivEhVRpL9bw9yc8OUG9/xlMIB+68rbsbbIKQfpcf+5QsGMnOEkKK951ey89DJmwk6dhXX4cwG9j5MTa0qsv6AMLHhwVL8B2mCY0N0tcnzIZSkg5DLUd0nibqU5sk/+i+K/DZ7nDemxA+P6wl+HII17kQHlMVJekUvgbCyBEWR7YNPofQTrkvY3Xhwo5BqrURvXGzLstlpnKTyCpycK8XDKMBTyXpCnr5i5yVpfETCponovYTcvTgQjuD70hGnxAaeX7lhCWNw21EhueimtNefH6zWJIOma8cx5og5IxUFKmpJfOEMu/NfIOqG6yeQ2glGueDO8kzrfgcj71U56zA53JU/IoF9FoIY1O4NPDGMj8yJlbKXSOK3zgsNGZRWKdXJQnoSJX0UWnDaCZw48I7eE8V3rKZchs2Dn8gqUy2PuDJApYzxZwx/s4XjX3W2dFTKHC6hVLZJwV+EULzG5DjOjFz1rC8w4WrCB4QQqonopePGSov1W4+di/EvD+9qQo9wU0uPKc/SHEUx8cFQs4Sc7bD2yNQh+9wob0LocmGFSFtkQu5skgK30nKb99UhXdspqQLTwhjoVustvGQ+vl8LI59F8K7Zm88XKng9SwqbfxCeC98ET4evua/gZCPmYPRgf1MhNDIN8/iBkMfg5BwhQvDBxAapdBG+hYicMyMR2Z8COFs8TfcE6QpZY/Rysd4hDbqQoq1G3bFmqHbIYdFCIULpeSdAoTSDoZ59bsZ6+AhXVU2ZFBe+HvqQgmryn/xU9KFAu7ryCCPDVIcl9pJkcQajp3DFYsUz/YfuwQh4pgqfIcLvXSdRKNGPlGyTWcQBsN1TJYpenWjESUl90lkFxelE1+7EsbHsIIJhDSLvl3XAVK/Z++lFw2NhKAdFbozVfg4/t6EZzIIxZUw6VMsiEqTlFfcQrMKoQxaZTvpV1oFka2lQMNVi9aHUKvaHEOIkzwUxDIXeMXuAP5FbEF5OsokDyaly5x5N7xG5gs/J0g9DKPcVVXN8LDGBfo5F6YGQRbW/CkYBwKhqZ2KDwRnCKcWaWxIEjCjxSR+DBeCCpqvq+vqhqFhJwqalmK3sb02FkVadY5PgnCQazLrwZkJWZTdCkymLjcKSkk95Vxolrnw7eDMoQuTmcvsJ81Jse8a8lqRtGp0mT3gIxYpcfgnxhG4RJHBFc+FjkKtBCEum/ufsEiT2uP/UByWZPv+GS5ccO1B5GYUpF7iByJKpWVQwkNQR2dWJdzz6Myp6dBGphTHhcVV+AKjJezxL0OIdZycDAcRxP5lLpTv5X7hMoT2/egMnC6mdJgdUT2f6cKbENqPRGc4bBCnKQe+YxAl/EMpUHuo3HUIhz1yqxAaRIn4oGjDTQwmnpD0K13x97mQ5ck7VHU5G46PkLWC3OHCz0BoeYYkSa+A59iB/46ldkcgD5eTTSZG9N8gy7QaVvcNzbbKhTbTB59KYy6FuWcp+2NO2vuZ6CBSUWJ2jCSH1Ok79s0Fx4SyMDfNmM9+wHJYD5EupXyt/Uxy9Q6E85SvtbbctP6RYoIkmTFmKBExG/A6hTCH7z0Ib4RIZxA2eRj7ripcDJGOIbRW2dH9sZKeVlCfQ3EEQtOB74MlDrhe2yElyh2vQk+jPafK3oVQL+b+VgaiPFeGBNbeCM7oFWwqfG9lV92yID3Kn2oIB0nQp1RxoMvs70ForbX9vU/PFfRazveEEBXL5bNuNPHIo7Wk71mESCV89vPFYil8aOy+ZM0IhDbnPTtpeHhTQc9RxKMUGDuq73PFYkdv6QIzEpUQDq7j+Sf4pok1R7EuyB+j95ZBAz2zbQQhjWRn1W71lAmvaRlmGj44CvLtUJrw0OS3DJpqWfJQpMYeIpmJFNCzs4Yjes6GjZU8OoOzIJ+mzAfnTJxnrR71fGEzTvimZsxxjR/5a9owvsWEC8x4XCyac9+5BAPpU1Zy9yRENsBCyWYcR0eP2VAbidKD8dhYRhPbOqCH13SYJ3Ej7Njhe7/5eAnCa84IfIIJRzDaBCFNPdxUSPaYDd0P7KsoHqMjZ240V4/h9xtsSGbgDXfm4dCw4yGNOfA+TV9+pA3h7GpfQBFTl+8cQknHnKMa7zcf/4DvzdJoNGPWqE1dFW5FhtjvbJ8efgxBDcWj99h7P+FAnjRyjhq5LxzSjOGlqNQxiAOGrXxp5nSceEfPHAv6cZ0RbwozHqrwv34jmI+J0Ko3/uZpZeVXnTKeQqT6bIjDDMI964m4qw5jdH0cus1Q9AsQJvlZzvfDu9ZoYELnfqCP4+UzXss7VWXIHOjTrNfqj+9hmM1ti1+rqvlgRqAVCHn7BclMiKcQEia9Fex/oCmMaQ5HnP7U48Bjhn9+L+wtT5qdeu9kbt6IFQ9mvObotZI00M0cyJW3rZq0tzRPxnkUl7UidniRmREWuJAHHPjAgZDP+3G3WsjTeJc4yXem3WSr5zH9qcOGqYSkGTh9KxpCPDeDd7TGoTXjKWHOGCxmsFU+EMp4pmNGTIXgHesBr7QX+k2kN44U40WV73bhggyvL//Umq9tnRFP0MAvVUbCEMKTAzlF1SC4jiHhNU8xDh/yY1bMBr9XIXsWoDZyYNsUdiseQhcPcjFDah/B36mv2mVDYTweFVPC62TKKN6Sool7iCbtXJmNBeqAkKOIi+ohB/dCWrSZawSYLMBwfsiH+eb2mg1ZunyrdbJXtGtpwib8mCwWhL9bHG06i7z3IQSZudwMTLPHUrulm1XEV+iYa0rTiNE5FZia+bZq6tzeyg7wOkxTDDzhcYoDYeqqkbI+dyNQNpFqbZl3ApMkwT6TDd9DLqSLBex6WtPkg53rJlzZZgnarKZyxdUUQE4wtP44F+/MZp3VEB5uhMKBOYILFmAadFtsNpMZw9QdYOt8NcgcCzcC1YBNGS9BmLBgvEAmt0h5/4yUlOvidKtmcxcYyvYBMdZqBE02q3nGiMlLbfnI9znxHBoJ1bRiihOaVBPH3knTnRtpCwx3GarY48RyUcUpSYmrDiwizRFM45B7HHiNzjXNvHbpCqLWTTSYzXWuIeS6YSBlq6zZqmUetBJm2Mu4phSXgz659RrdCvnYQYI4LvRF2qAfe6PSLl8pbMohw47STqgJE0oq0x7uvLV6sMbma8WOc1XpompreqXvMJUjN2u9GMJi9UIGYTxjZWwotgtZOiBSPkO+lKVskUh7njq91RcQJoNGtg6JCFWnEC6XS1K1E9oVexPY9FY2erExU0J4sWG9zVlFMD9IrmgsOsAIwaX1jCqGSCiteRFF2xbz5ERdO1wEwqEeLEGk1uArl/mZxq4Un0Afge2rnU0kjga8esGaq0i8YAcLpeAKrvfWrCFyxSIXxx6Ua4M1Tln4E9kQe70MtlqKc5rzsgaFH5KtTZvgxy4LdPjQSGho+z23el1FkVTfKzi0oFSBU4vgtf+5WM5rjNt5OZKP+7MIleEDtU0jJo02ip7KhT+XJPW9WWlFkautrEHeLM0BXkBQ99d8lWvcvbgYe60QaybMDJrvOYIyjrzmgu3YRmaO9YZuEGK3JNzINs1m8sB7vQwsJpIkqaQ5+qceZOGZgShESKgjLd5St3K1EhPiqAjobHuEajttx4QtwlyUOWwV9W7f5hgeQ/dLP9/pyx7tGoJQhE0aIkx6hRiu7ejDOOv78DEye7RZX5gEN2h5i2xhLkrjLKpGAdOjBxJ2tbQ88KPXlz1WEE4RFB/tGv5oB2ogXw/dLs+Mfv7uBkw4YUNb+me5RGvvUFLEP8VmnLYIhPfPbGljqc6ElyjVQ23ZPkIeXLB9XWzI71WiD8kScJ1gUMcL7K1GgoVFNjkPVjHmvQdhubE53EB1uR75065x2uLjUWGsrXPlV3jLdMQAMz4V2WfNC8zbVX+VldonG6qx0swS4KkKjWUedU57g8zlzWhlTNIFebGYsgh2sE7qVc40ssWZtru0Ewf4ejNtjaH4+ZyeGTChYEgrPEhxxV9Pjh7nR806Rq1wgGjEhJlFo0y7KxaBFprwgDC7REc7XmyKzzZu9NLuR/S72E8G831SddmRco4+Y4EtGRJYLQRrMAw2KZFzAyYcsGGd6skNC9eFUPFG1fodGjDhKHlfvzLvya4EAfefVsrQiOtgRvpQnMTXWTnhtaXo6nZMzR20eTb9mhF9Xczt1ITlQrBGm0siEa5lJgoT5pHSEYJ7sdnIHqvb/XWCJndrYMKHBkkJj7aidBKSidvqyyu8kzsFz/naKDFSTlmaPh92tx7DeD1miaA9d+OUTFhECM2hyyUyOOLDn/00LTgwo1z3jkXTbCr2RbmDSdo4eIalLXEuvnEzu/AqIqAOUVMpuqlyIJaFQyVJ01iHzCU1+iohZc1jCWHLhraqWrT6UfrsWtpEssdWHw5TFDqEimNhteJbalyKIJxatwbaaiw3muinQ6iyoRnowYwAjoW4yqsR/IzNo+yD9ReVzoHBtu+aB3nmn9XEGftHlzGh+BQzDLGLoCZK9fK/xib2BQXm3NXQ7Hp348ryDlEwDKoRVmvq1e0t7pLcJIbU1LdICJIOYStKbb3iNqv6K84y7nvu24MTDN0AQYGQhjzItowG4RcpwfagCV2znth0N9D0iaJxaoLoq7Dn1HfurtsiVmmR67KDDp3/+hCWu67qLjoqc+ZWj42YYt/2giwdiNFWlFq9BL4NzWAMI5fOfVJHdfGA6XSM95iwZcPaFA03qORCd1br5YFFc8lsQk55zTFsTHeoDws6CLJmrxbDFSuD9fiMr8XpZu6J0cM5tAMhWhJgz+vD5kx7hqDVJjvNKh0gWGFYOxMk63k1OSrRGVDuTtpoawYhb1WMVhAW6rCSojy5hjo6iXeuw96GtwJ8W60RN3cXwUyUaj1h9aJUM5HhjkBZP+1uIhhzv9B5TcT6fZsrKMUU6y/LyteMY22K89VCSLo/yG1EZc7HFhHK7E5uX5lRSo2rZBRFODqsC0O1q4+oVEvbTA1r2rBNAdMEwkiU7tAHLtzKPFMGIdPrWi1s5A+QShd/AcEKwgvD2o7HNu9aQajwAfJktlobVraZoymCkSz87rcx5AGSnmtmqqWotTQocne4QJX0bmsIpskKm5qq4TT/rtwcg5yuDIxoB/oQaA7hoQ7bRgbbDPUvOIFAFWWBDTdf88XmbiKYljN326EL/zp7aa8aFJLsUjp1TFnxNKdKcg6lQy8vHK4tbajfn0iv+qLGiijdBqknVWCBduGhkaI8URCHQS5VGbIxg77N3rl1RZio4hYLxaWH3Jsp7w+XD5DXjtFJ1eBPH0OzfK/CWbVBtfCmu4Z4EOI7ZRi6WhfiL3bT+DpZMLT/sgxF5RJWCdciW5cVHighmgLDRQRzzVNmKLiOUw80o1Q3qJKUtaHWpmPyWXOrRDXKnl+5vrbuKlenIrTgyrYu/PrFqv344sPOxWohBBVDtHUZtS0FKeluBUcpCHsYol+67udxQRtYg5IJbR7iKtJ1tUuqFUKaiwdplahK6aVKp57YER+2V0lnN97sjp00fu+soHNY9rtaOT2Ro127nh3WVpTGN3UDwXRcTVyGqu8VTmmpC6sKHtKq6MzBg3SDqBxD+boT4j7D3HvPEo4NOqYC0WYj+tcgrDEk6SSiWQmuygo9bcjvKi5XX0VQ5UM2LUEvHvti57rkf1cUDZDae2y2dR48McxkqSTYNuwzYdCFhVdaKkO/WWXcGfOh75IFXbpsZo3SNw4zdlRtgN6+Km2oYIh4D8EWw1aM5penLD2qJOneax83QPeooiKOx1uBFeWfQUilLqyqL7h9UlkAYezgusN/UwzRcmPmkAlr9ijaCVCSh63Ov4vgYQJmRnBj4VTxWezFZ53XQjRBtAuCcJuoU5YGd2LDvhz9kRL+fkxBBfAYqd05LOgb8Zn2U2aPNe4ZDIJcbXXwEwQPExAuN4Z6Rbjai+b+KJd9OKgjpe6eFM0wPHWFEsvYhp2xpTLE/FM+EatPFgzlw2WPft/iQtvGDJs48AMED1kKqQ+tsVCz0MxXpaQae+ZHOgMKBNE/QPDAkM73Oq6iDYy496Ozhjbe1UpluJZGZMEo9jAY6r8PdaEbO0qbf4hgFA8nck0prs1fI0i0yo4yRfUVV5UWWumuGqwwxG0hScq5pgrCepdxJUwl6TUiCyY33q4UcUpDcFegcWPDV67j8TmCFYZ7X45u3N4xgpB7WsBVQvQpUb5KTvbkaJVrUgLsptKGUwRHECZFvcCEshcAup4hFbVsG70B4BkCnDkzcnOG+WYqGyzwLaqoFKZdOco3vS5mrXMkVVfOlCyYXy47hRCq6Exlz1AuY95iQdUw7crvry/aRkrpp2FBepMoT6NoYnJHqSwor1OVRa5iJVIE02C8Vk89HSZnO2rh6JN8B8F050dNYUWsZF5+9SYLlhhuQ1XIZsP+s1C5c1bvTC/WzAUq15f0gmveV1zodLWAnwDwMphHTEjc1bF9LXChw7dZsBCmFYim7pD1+1oB3dgbXIfwANEOIYSmUXLrA/gBBHVpassqQI9FrqnDhQnAjxBFR5d6N7ourfnzwp0rX7JwsRYCEb4BURtRALMKlo8CqErT4jWDH/PVJoJcAyB8hgULRuT+axVCzlO0/c2uv1xphSy4IeZtF0JfmzOtc382cv/3sScePnQ4f0MlQ1K68gcHevosTTmIVZnJDm3ZTq0MzbGhcu2w4JaYt71iXKi77Vx5mt5/+qxyEKMYN1/zx5QApgP/KEkniMkcHo670uIz24a3bjvcpIxDbcoYwNbK37Jw8x8C8CSLvVLoWsFqJaKz51n/GZoSK25zBKuqHbor2uG+iEAFwl7ad8NrwNDnAYwHFt+x55babZtAuMUY1zmZhvyfoSlbSLMA4dFnaOjY5HlH38B9ysgizOfEm4L/PqoD+wcmI1MGMG6bDfDBMZLmzxGVg+jcjBMdbwCU204P6ILHpwVjHIu9338QwJouhhEldV62OISHI32U6Sr6WzTBGEaew4rPDwveOi1QgWTzwv89/Fq6DigpTQ8irP+U/gZRVMqHILtk2szRms6/gk2FQarDO4cF70quBkOQucR/F7/2yMYDLelfkCRJP7lPATUEkJtF9Lawgsek0f/TUS3T9fdporWb9Zww+CO0sWUF//2rRwhr5Sr9gys1oqgg7Y0fDp+gLhKYhsP9u4OqKQP6vyIov/LJe/gIbf8TYABoBrFw/ZHQPgAAAABJRU5ErkJggg==";
//             System.out.println(" real length " + data.length());
//             ByteArrayOutputStream out = new ByteArrayOutputStream();
//             ByteArrayInputStream in = new ByteArrayInputStream(data.getBytes());
//             LZCInputStream lz = new LZCInputStream(in);
//             LZCOutputStream lzOut = new LZCOutputStream(out);
//             
////             Gzip.gzip(in, out);
//             Gzip.deflate(in, out);
//             ByteArrayInputStream in1 = new ByteArrayInputStream(out.toByteArray());
//             System.out.println("compress size " + out.toByteArray().length);
//             
//             out = new ByteArrayOutputStream();
//             Gzip.inflate(in1, out);
//             
//             String uncompress = new String(out.toByteArray());
//             System.out.println("Gzip uncompress " + uncompress);
////           
//             out = new ByteArrayOutputStream();
//             
//             in = new ByteArrayInputStream(data.getBytes());
//             LZCOutputStream.compress(in, out);
//             
////             LZCInputStream.uncompress(in, out);
//             int len = out.toByteArray().length;
//             
//             //System.out.println("len " + );
//             System.out.println("LZ compress length " + len );
//             
//             ByteArrayInputStream in2 = new ByteArrayInputStream(out.toByteArray());
//             out = new ByteArrayOutputStream();
//             LZCInputStream.uncompress(in2, out);
//             
//             uncompress = new String(out.toByteArray());
//             System.out.println("LZ uncompress " + uncompress);
             
             
//             System.out.println("LZ compress length " + len );
             
             
             
             
         }
}
