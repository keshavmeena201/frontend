package com.dev.credbizz.dbHelper;

import com.dev.credbizz.models.TransactionModel;

import java.security.cert.CertificateException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;

public class Constants {
     private static String mobileNum = "";
     private static String orgname = "";
     private static ArrayList<TransactionModel>  transactionModelList= new ArrayList();
     private static LocalDateTime localDateTime = LocalDateTime.now();

     private static boolean isfirst = true;

     private static int amountToPay = 0;
     private static int amountToGive = 0;
     public String getMobileNum() {
         return mobileNum;
     }

     public void setMobileNum(String mobileNum) {
         Constants.mobileNum = mobileNum;
     }

     public String getorgName() {
         return Constants.orgname;
     }
     public void setOrgname(String orgname) {
         Constants.orgname = orgname;
     }

     public void addTransaction(TransactionModel transactionModel) {
         transactionModelList.add(transactionModel);
     }

     public void cleartransaction() {
         transactionModelList.clear();
     }

     public ArrayList<TransactionModel> getAllTransactons() {
         return transactionModelList;
     }

     public void settime() {
         localDateTime =LocalDateTime.now();
     }

     public Boolean isallowed() {
         LocalDateTime currenttime = LocalDateTime.now();
         long minutes = ChronoUnit.MINUTES.between( localDateTime,currenttime);
         System.out.println("lolololo" + minutes);
         if(Math.abs(minutes) < 3600){
             return true;
         } else{
             Constants.isfirst = true;
         }
         return false;
     }

     public void setfirst(){
         Constants.isfirst = false;
     }

     public Boolean isFirst() {
         return Constants.isfirst;
     }

     public void setamountToPay(int val){
         Constants.amountToPay = val;
     }
    public void setamountToGive(int val){
         Constants.amountToGive=val;
    }

    public int getamountToPay() {
         return Constants.amountToPay;
    }

    public int getamountToGive() {
        return Constants.amountToGive;
    }

    public OkHttpClient getUnsafeOkHttpClient() {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager)trustAllCerts[0]);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            OkHttpClient okHttpClient = builder.build();
            return okHttpClient;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
