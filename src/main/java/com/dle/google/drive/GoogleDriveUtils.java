package com.dle.google.drive;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.*;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.Permission;
import com.google.api.services.forms.v1.Forms;
import com.google.api.services.forms.v1.model.Form;
import com.google.api.services.forms.v1.model.ListFormResponsesResponse;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.*;

public class GoogleDriveUtils {

    private static final String APPLICATION_NAME = "Google Drive API Java Quickstart";

    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    // Directory to store user credentials for this application.
    private static final java.io.File CREDENTIALS_FOLDER //
            = new java.io.File(System.getProperty("user.home"), "credentials");

    private static final String CLIENT_SECRET_FILE_NAME =
            "client_secret_2_1098452010498-bojdr4e337pm4ieef8len8s4cl35jsg5.apps.googleusercontent.com.json";

    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE);

    // Global instance of the {@link FileDataStoreFactory}.
    private static FileDataStoreFactory DATA_STORE_FACTORY;

    // Global instance of the HTTP transport.
    private static HttpTransport HTTP_TRANSPORT;

    private static Drive _driveService;
    private static Sheets _sheetsService;

    private static Forms _formsService;

    static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            DATA_STORE_FACTORY = new FileDataStoreFactory(CREDENTIALS_FOLDER);
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }

    public static String TIKTOK_DOCUMENT_FOLDER_ID = "1uiYO_S7vX3bGKTyqoolaQ5siR-BQKozo";
    public static String TIKTOK_BY_KEM_STUDIO_1 = "1of3RCbsVoWPCEqkqAqN6UmiUl8fMOYdb";
    public static String TIKTOK_BY_KEM_STUDIO_2 = "10c36N3DzwaNFEYvGlyCbaZOJSISRUu1i";
    public static String TIKTOK_BY_KEM_STUDIO_3 = "1jSn9WRB9GO927rGzIVH3b04e9s112Ste";
    public static String TIKTOK_BY_KEM_STUDIO_1_ARCHIVED_FOLDER = "1VDyARffK_QREhiAKXn6-LTgT13TUr16m";
    public static String TIKTOK_BY_KEM_STUDIO_2_ARCHIVED_FOLDER = "1oQY-UAapAYYsTdJhas-vAiVfd5o2MN1T";

    public static Credential getCredentials() throws IOException {

        java.io.File clientSecretFilePath = new java.io.File(CREDENTIALS_FOLDER, CLIENT_SECRET_FILE_NAME);

        if (!clientSecretFilePath.exists()) {
            throw new FileNotFoundException("Please copy " + CLIENT_SECRET_FILE_NAME //
                    + " to folder: " + CREDENTIALS_FOLDER.getAbsolutePath());
        }

        InputStream in = new FileInputStream(clientSecretFilePath);

        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY,
                clientSecrets, SCOPES).setDataStoreFactory(DATA_STORE_FACTORY).setAccessType("offline").build();
        Credential credential = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");

        return credential;
    }

    public static Drive getDriveService() throws IOException {
        if (_driveService != null) {
            return _driveService;
        }
        Credential credential = getCredentials();
        //
        _driveService = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential) //
                .setApplicationName(APPLICATION_NAME).build();
        return _driveService;
    }

    public static Sheets getSheetsService() throws IOException {
        if (_sheetsService != null) {
            return _sheetsService;
        }
        Credential credential = getCredentials();
        //
        _sheetsService = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential) //
                .setApplicationName(APPLICATION_NAME).build();
        return _sheetsService;
    }

    public static Forms getFormsService() throws IOException {
        if (_formsService != null) {
            return _formsService;
        }
        Credential credential = getCredentials();
        //
        _formsService = new Forms.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential) //
                .setApplicationName(APPLICATION_NAME).build();
        return _formsService;
    }

    public static Permission createPermissionForEmail(String googleFileId, String googleEmail) throws IOException {
        // All values: user - group - domain - anyone
        String permissionType = "user"; // Valid: user, group
        // organizer - owner - writer - commenter - reader
        String permissionRole = "reader";

        Permission newPermission = new Permission();
        newPermission.setType(permissionType);
        newPermission.setRole(permissionRole);

        newPermission.setEmailAddress(googleEmail);

        Drive driveService = GoogleDriveUtils.getDriveService();
        return driveService.permissions().create(googleFileId, newPermission).execute();
    }

    public static final File createGoogleFolder(String folderIdParent, String folderName) throws IOException {

        File fileMetadata = new File();

        fileMetadata.setName(folderName);
        fileMetadata.setMimeType("application/vnd.google-apps.folder");

        if (folderIdParent != null) {
            List<String> parents = Arrays.asList(folderIdParent);

            fileMetadata.setParents(parents);
        }
        Drive driveService = GoogleDriveUtils.getDriveService();

        // Create a Folder.
        // Returns File object with id & name fields will be assigned values
        File file = driveService.files().create(fileMetadata).setFields("id, name").execute();

        return file;
    }

    // PRIVATE!
    private static File _createGoogleFile(String googleFolderIdParent, String contentType, //
                                          String customFileName, AbstractInputStreamContent uploadStreamContent) throws IOException {

        File fileMetadata = new File();
        fileMetadata.setName(customFileName);

        List<String> parents = Arrays.asList(googleFolderIdParent);
        fileMetadata.setParents(parents);
        //
        Drive driveService = GoogleDriveUtils.getDriveService();

        File file = driveService.files().create(fileMetadata, uploadStreamContent)
                .setFields("id, webContentLink, webViewLink, parents").execute();

        return file;
    }

    // Create Google File from byte[]
    public static File createGoogleFile(String googleFolderIdParent, String contentType, //
                                        String customFileName, byte[] uploadData) throws IOException {
        //
        AbstractInputStreamContent uploadStreamContent = new ByteArrayContent(contentType, uploadData);
        //
        return _createGoogleFile(googleFolderIdParent, contentType, customFileName, uploadStreamContent);
    }

    // Create Google File from java.io.File
    public static File createGoogleFile(String googleFolderIdParent, String contentType, //
                                        String customFileName, java.io.File uploadFile) throws IOException {

        //
        AbstractInputStreamContent uploadStreamContent = new FileContent(contentType, uploadFile);
        //
        return _createGoogleFile(googleFolderIdParent, contentType, customFileName, uploadStreamContent);
    }

    // Create Google File from InputStream
    public static File createGoogleFile(String googleFolderIdParent, String contentType, //
                                        String customFileName, InputStream inputStream) throws IOException {

        //
        AbstractInputStreamContent uploadStreamContent = new InputStreamContent(contentType, inputStream);
        //
        return _createGoogleFile(googleFolderIdParent, contentType, customFileName, uploadStreamContent);
    }

    // com.google.api.services.drive.model.File
    public static final List<File> getGoogleSubFolders(String googleFolderIdParent) throws IOException {

        Drive driveService = GoogleDriveUtils.getDriveService();

        String pageToken = null;
        List<File> list = new ArrayList<File>();

        String query = null;
        if (googleFolderIdParent == null) {
            query = " mimeType = 'application/vnd.google-apps.folder' " //
                    + " and 'root' in parents";
        } else {
            query = " mimeType = 'application/vnd.google-apps.folder' " //
                    + " and '" + googleFolderIdParent + "' in parents";
        }

        do {
            FileList result = driveService.files().list().setQ(query).setSpaces("drive") //
                    // Fields will be assigned values: id, name, createdTime
                    .setFields("nextPageToken, files(id, name, createdTime)")//
                    .setPageToken(pageToken).execute();
            for (File file : result.getFiles()) {
                list.add(file);
            }
            pageToken = result.getNextPageToken();
        } while (pageToken != null);
        //
        return list;
    }

    /**
     * @param fileId   Id of file to be moved.
     * @param folderId Id of folder where the fill will be moved.
     * @return list of parent ids for the file.
     */
    public static List<String> moveFileToFolder(String fileId, String folderId)
            throws IOException {

        Drive service = GoogleDriveUtils.getDriveService();

        // Retrieve the existing parents to remove
        File file = service.files().get(fileId)
                .setFields("parents")
                .execute();
        StringBuilder previousParents = new StringBuilder();
        for (String parent : file.getParents()) {
            previousParents.append(parent);
            previousParents.append(',');
        }
        try {
            // Move the file to the new folder
            file = service.files().update(fileId, null)
                    .setAddParents(folderId)
                    .setRemoveParents(previousParents.toString())
                    .setFields("id, parents")
                    .execute();

            return file.getParents();
        } catch (GoogleJsonResponseException e) {
            // TODO(developer) - handle error appropriately
            System.err.println("Unable to move file: " + e.getDetails());
            throw e;
        }
    }

    public static Spreadsheet createSpreadsheet(String spreadsheetName) throws IOException {
        Spreadsheet spreadsheet = new Spreadsheet()
                .setProperties(new SpreadsheetProperties()
                        .setTitle(spreadsheetName));
        spreadsheet = getSheetsService().spreadsheets().create(spreadsheet)
                .setFields("spreadsheetId")
                .execute();
        System.out.println("Create Spreadsheet Name" + spreadsheetName + ", ID: " + spreadsheet.getSpreadsheetId());
        return spreadsheet;
    }

    public static void createSheet(String sheetName, String SPREADSHEET_ID) throws IOException {
        List<Request> requests = new ArrayList<>();
        requests.add(new Request().setAddSheet(new AddSheetRequest().setProperties(new SheetProperties()
                .setTitle(sheetName))));
        BatchUpdateSpreadsheetRequest body = new BatchUpdateSpreadsheetRequest().setRequests(requests);

        BatchUpdateSpreadsheetResponse response = getSheetsService().spreadsheets().batchUpdate(SPREADSHEET_ID, body)
                .execute();
        System.out.println("Create Sheet Name: " + sheetName + ", in Spreadsheet ID: " + SPREADSHEET_ID);
    }
    public static void writeSpreadsheet(String spreadsheetId, String sheetName, List<List<Object>> values) throws IOException, InterruptedException {
        ValueRange body = new ValueRange()
                .setValues(values);
        UpdateValuesResponse result =
                getSheetsService().spreadsheets().values().update(spreadsheetId, sheetName + "!A:A", body)
                        .setValueInputOption("USER_ENTERED")
                        .execute();
        System.out.println(result.getUpdatedCells() + " cells updated to sheetName: " + sheetName + ", spreadsheetId: " + spreadsheetId);
        Thread.sleep(1500);
    }

    public static ListFormResponsesResponse readFormResponses(String formId) throws IOException {
        //        System.out.println(response.toPrettyString());
        return getFormsService().forms().responses().list(formId).execute();
    }

    public static Form getForm(String formId) throws IOException {
        return getFormsService().forms().get(formId).execute();
    }

}
