package com.dle.google.drive;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;

import com.google.api.services.drive.model.FileList;
import com.google.api.services.forms.v1.model.*;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;


import java.io.*;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.util.*;

public class DriveQuickstart {

    private static final String APPLICATION_NAME = "Google Drive API Java Quickstart";

    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    // Directory to store user credentials for this application.
    private static final java.io.File CREDENTIALS_FOLDER //
            = new java.io.File(System.getProperty("user.home"), "credentials");

    private static final String CLIENT_SECRET_FILE_NAME =
            "client_secret_2_1098452010498-bojdr4e337pm4ieef8len8s4cl35jsg5.apps.googleusercontent.com.json";

    //
    // Global instance of the scopes required by this quickstart. If modifying these
    // scopes, delete your previously saved credentials/ folder.
    //
    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE);

    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {

        java.io.File clientSecretFilePath = new java.io.File(CREDENTIALS_FOLDER, CLIENT_SECRET_FILE_NAME);

        if (!clientSecretFilePath.exists()) {
            throw new FileNotFoundException("Please copy " + CLIENT_SECRET_FILE_NAME //
                    + " to folder: " + CREDENTIALS_FOLDER.getAbsolutePath());
        }

        // Load client secrets.
        InputStream in = new FileInputStream(clientSecretFilePath);

        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY,
                clientSecrets, SCOPES).setDataStoreFactory(new FileDataStoreFactory(CREDENTIALS_FOLDER))
                .setAccessType("offline").build();

        return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
    }

    public static void main(String... args) throws IOException, GeneralSecurityException, URISyntaxException {

        System.out.println("CREDENTIALS_FOLDER: " + CREDENTIALS_FOLDER.getAbsolutePath());

        // 1: Create CREDENTIALS_FOLDER
        if (!CREDENTIALS_FOLDER.exists()) {
            CREDENTIALS_FOLDER.mkdirs();

            System.out.println("Created Folder: " + CREDENTIALS_FOLDER.getAbsolutePath());
            System.out.println("Copy file " + CLIENT_SECRET_FILE_NAME + " into folder above.. and rerun this class!!");
            return;
        }

        // 2: Build a new authorized API client service.
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        // 3: Read client_secret.json file & create Credential object.
        Credential credential = getCredentials(HTTP_TRANSPORT);

        // 5: Create Google Drive Service.
        Drive service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential) //
                .setApplicationName(APPLICATION_NAME).build();

        // Print the names and IDs for up to 10 files.
        FileList result = service.files().list().setPageSize(10).setFields("nextPageToken, files(id, name)").execute();
        List<com.google.api.services.drive.model.File> files = result.getFiles();
        if (files == null || files.isEmpty()) {
            System.out.println("No files found.");
        } else {
            System.out.println("Files:");
            for (com.google.api.services.drive.model.File file : files) {
                System.out.printf("%s (%s)\n", file.getName(), file.getId());
            }
        }



//        List<com.google.api.services.drive.model.File> googleRootFolders = GoogleDriveUtils.getGoogleSubFolders(GoogleDriveUtils.TIKTOK_DOCUMENT_FOLDER_ID);
//        for (com.google.api.services.drive.model.File folder : googleRootFolders) {
//            System.out.println("Folder ID: " + folder.getId() + " --- Name: " + folder.getName());
//        }

//        ListFormResponsesResponse formResponses = GoogleDriveUtils.readFormResponses("12XXqkRR7j-Fl_EvkwzDdbgCI24ufohWFIKJWa2JWnQU");
//        System.out.println();

//        List<com.google.api.services.drive.model.File> googleRootFolders1 = GoogleDriveUtils.getGoogleSubFolders(GoogleDriveUtils.TIKTOK_BY_KEM_STUDIO_1);
//        for (com.google.api.services.drive.model.File folder : googleRootFolders1) {
//            System.out.println("Folder ID: " + folder.getId() + " --- Name: " + folder.getName());
//        }


//        List<String> lines = Files.readAllLines(Paths.get("/Users/dle/IdeaProjects/tiktok/data.txt"), StandardCharsets.UTF_8);
//        Set<String> lineUnique = new HashSet<>();
//        Map<String, List<String>> commentByUsername = new HashMap<>();
//        List<String> comments = new ArrayList<>();
//        for (String line : lines) {
//            String[] data = line.split("\\|");
//            lineUnique.add(data[0]);
//            if (!commentByUsername.containsKey(data[0])) {
//                commentByUsername.put(data[0], new ArrayList<>());
//            }
//            commentByUsername.get(data[0]).add(data[1]);
//            comments.add(data[1]);
//        }
//        System.out.println();
//
//        Map<String, Integer> count = new TreeMap<String, Integer>();
//        for (int j = 0; j < comments.size(); j++) {
//            addElement(count, comments.get(j));
//        }
//
//        StringBuilder builder = new StringBuilder();
//        builder.append("Ten,").append("So comment,").append("Comment").append("\n");
//        commentByUsername.forEach((k,v) -> {
//            builder.append("\"").append(k).append("\"").append(",").append("\"").append(v.size()).append("\"").append(",").append("\"").append(String.join("|", v)).append("\"").append("\n");
//        });
//        //Files.write(Path.of(new URI("/Users/dle/IdeaProjects/tiktok/data.csv")), builder.toString().getBytes(Charset.forName("UTF-8")));
//
//        java.io.File file = new java.io.File("/Users/dle/IdeaProjects/tiktok/" + "user_"+commentByUsername.size()+"_"+"comments_"+comments.size()+"_"+"trung_" + (comments.size()-count.size()) + ".csv");
//        Writer writer = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
//        writer.write(builder.toString());
//        writer.close();
//        System.out.println();



//        Spreadsheet spreadsheet = GoogleDriveUtils.createSpreadsheet("Test Spreadsheet");
//        List<List<Object>> values = Arrays.asList(
//                Arrays.asList(
//                        "1"
//                ),
//                Arrays.asList(
//                        "4"
//                ),
//                Arrays.asList(
//                        "7"
//                )
//        );
//        GoogleDriveUtils.createSheet("SHOP", spreadsheet.getSpreadsheetId());
//        GoogleDriveUtils.writeSpreadsheet(spreadsheet.getSpreadsheetId(), "SHOP", values);

//        ListFormResponsesResponse response = GoogleDriveUtils.readFormResponses("1E_QGdf94XGVe00IcCcRng3xZSpuDPDgGkgjanQrcwYg");
//        //((FormResponse)response.getResponses().get(0)).getAnswers().get("71558398").getTextAnswers().getAnswers().get(0).getValue()
////        System.out.println(response.toPrettyString());
//        StringBuilder builder = new StringBuilder();
//        for (FormResponse res : response.getResponses()) {
////            Collection<Answer> answers = res.getAnswers().values();
////            for (Answer answer : answers) {
////                builder.append(answer.getTextAnswers().getAnswers().get(0).getValue()).append("\t");
////            }
////            builder.append("\n");
//            System.out.println(new FormResult(res));
//        }
//        System.out.println(builder.toString());
//        Form form = GoogleDriveUtils.getForm("1xdJ1T2wTboO5GzMaHqyU9QoKl-5E-ohJ0qoWCwcc0Z8");
//        System.out.println(form.toPrettyString());

    }

    public static void addElement(Map<String, Integer> map, String element) {
        if (map.containsKey(element)) {
            int count = map.get(element) + 1;
            map.put(element, count);
        } else {
            map.put(element, 1);
        }
    }

    static class FormResult {
        String orderId;
        String shopName;
        String shopCode;
        String productName;
        String productId;
        String originalSkuName;
        String originalSkuId;
        String chaneSkuName;
        String changeSkuId;

        public FormResult() {
        }

        public FormResult(FormResponse response) {
            Collection<Answer> answers = response.getAnswers().values();
            for (Answer answer : answers) {
                TextAnswer textAnswer = answer.getTextAnswers().getAnswers().get(0);
                if (textAnswer != null) {
                    String answerValue = textAnswer.getValue();
                    String codeOrId = StringUtils.substringBetween(answerValue, "[", "]");
                    String name = answerValue.substring(0, !answerValue.contains("[") ? 0 : answerValue.indexOf("[")).trim();
                    if (answerValue.indexOf("<SHOP>") > 0) {
                        this.shopCode = codeOrId;
                        this.shopName = name;
                    } else if (answerValue.indexOf("<PRODUCT>") > 0) {
                        this.productId = codeOrId;
                        this.productName = name;
                    } else if (answerValue.indexOf("<ORIGINAL>") > 0) {
                        this.originalSkuId = codeOrId;
                        this.originalSkuName = name;
                    } else if (answerValue.indexOf("<CHANGE>") > 0) {
                        this.changeSkuId = codeOrId;
                        this.chaneSkuName = name;
                    } else {
                        this.orderId = answerValue;
                    }
                }
            }
        }

        @Override
        public String toString() {
            return "FormResult{" +
                    "orderId='" + orderId + '\'' +
                    ", shopName='" + shopName + '\'' +
                    ", shopCode='" + shopCode + '\'' +
                    ", productName='" + productName + '\'' +
                    ", productId='" + productId + '\'' +
                    ", originalSkuName='" + originalSkuName + '\'' +
                    ", originalSkuId='" + originalSkuId + '\'' +
                    ", chaneSkuName='" + chaneSkuName + '\'' +
                    ", changeSkuId='" + changeSkuId + '\'' +
                    '}';
        }

        public String getOrderId() {
            return orderId;
        }

        public void setOrderId(String orderId) {
            this.orderId = orderId;
        }

        public String getShopName() {
            return shopName;
        }

        public void setShopName(String shopName) {
            this.shopName = shopName;
        }

        public String getShopCode() {
            return shopCode;
        }

        public void setShopCode(String shopCode) {
            this.shopCode = shopCode;
        }

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public String getProductId() {
            return productId;
        }

        public void setProductId(String productId) {
            this.productId = productId;
        }

        public String getOriginalSkuName() {
            return originalSkuName;
        }

        public void setOriginalSkuName(String originalSkuName) {
            this.originalSkuName = originalSkuName;
        }

        public String getOriginalSkuId() {
            return originalSkuId;
        }

        public void setOriginalSkuId(String originalSkuId) {
            this.originalSkuId = originalSkuId;
        }

        public String getChaneSkuName() {
            return chaneSkuName;
        }

        public void setChaneSkuName(String chaneSkuName) {
            this.chaneSkuName = chaneSkuName;
        }

        public String getChangeSkuId() {
            return changeSkuId;
        }

        public void setChangeSkuId(String changeSkuId) {
            this.changeSkuId = changeSkuId;
        }
    }
}
