package edu.lehigh.cse216.ducks.backend;

import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Map;
import java.util.Random;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.nio.file.Files;
import java.lang.InterruptedException;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.TimeoutException;

import com.google.gson.Gson;

import spark.Spark;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.Permission;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientBuilder;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.auth.AuthInfo;
import net.rubyeye.xmemcached.command.BinaryCommandFactory;
import net.rubyeye.xmemcached.exception.MemcachedException;
import net.rubyeye.xmemcached.utils.AddrUtil;

public class App {

    public static void main(String[] args) {
        final Gson gson = new Gson();
        final Database dataStore = getDatabaseConnection();
        final Drive service;

        try {
            service = getDriveService();
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
            return;
        }
        Spark.port(getIntFromEnv("PORT", DEFAULT_PORT_SPARK));

        String static_location_override = System.getenv("STATIC_LOCATION");
        if (static_location_override == null) {
            Spark.staticFileLocation("/web");
        } else {
            Spark.staticFiles.externalLocation(static_location_override);
        }

        if ("True".equalsIgnoreCase(System.getenv("CORS_ENABLED"))) {
            final String acceptCrossOriginRequestsFrom = "*";
            final String acceptedCrossOriginRoutes = "GET,PUT,POST,DELETE,OPTIONS";
            final String supportedRequestHeaders = "Content-Type,Authorization,X-Requested-With,Content-Length,Accept,Origin";
            enableCORS(acceptCrossOriginRequestsFrom, acceptedCrossOriginRoutes, supportedRequestHeaders);
        }

        Spark.get("/", (req, res) -> {
            res.redirect("/index.html");
            return "";
        });

        List<InetSocketAddress> servers = AddrUtil.getAddresses(System.getenv("MEMCACHIER_SERVERS").replace(",", " "));
        AuthInfo authInfo = AuthInfo.plain(System.getenv("MEMCACHIER_USERNAME"), System.getenv("MEMCACHIER_PASSWORD"));

        MemcachedClientBuilder builder = new XMemcachedClientBuilder(servers);

        // Configure SASL auth for each server
        for(InetSocketAddress server : servers) {
            builder.addAuthInfo(server, authInfo);
        }

        // Use binary protocol
        builder.setCommandFactory(new BinaryCommandFactory());
        // Connection timeout in milliseconds (default: )
        builder.setConnectTimeout(1000);
        // Reconnect to servers (default: true)
        builder.setEnableHealSession(true);
        // Delay until reconnect attempt in milliseconds (default: 2000)
        builder.setHealSessionInterval(2000);
   
        // comments
        Spark.get("posts/:PostID/comments", (request, response) -> {
            Integer postID = Integer.parseInt(request.params("PostID"));

            response.status(200);
            response.type("application/json");
            return gson.toJson(new StructuredResponse("ok", null, dataStore.selectCommentsForPost(postID)));
        });
        Spark.get("posts/:PostID/comments/:id", (request, response) -> {
            Integer commentID = Integer.parseInt(request.params("id"));

            response.status(200);
            response.type("application/json");
            RowData data = dataStore.selectOneComment(commentID);
            if (data == null) {
                return gson.toJson(new StructuredResponse("error", commentID + " not found", null));
            } else {
                return gson.toJson(new StructuredResponse("ok", null, data));
            }
        });
        Spark.post("posts/:PostID/comments", (request, response) -> {
            Integer postID = Integer.parseInt(request.params("PostID"));
            SimpleRequest req = gson.fromJson(request.body(), SimpleRequest.class);

            response.status(200);
            response.type("application/json");

            String fileURL = getFileURL(service, req.file, req.fileName, null);

            int commentID = dataStore.insertComment(postID, currentUser, req.Text, req.webURL, fileURL);
            if (commentID <= 0) {
                return gson.toJson(new StructuredResponse("error", "error performing insertion", null));
            } else {
                return gson.toJson(new StructuredResponse("ok", fileURL, commentID));
            }
        });
        Spark.put("posts/:PostID/comments/:id", (request, response) -> {
            Integer commentID = Integer.parseInt(request.params("id"));

            SimpleRequest req = gson.fromJson(request.body(), SimpleRequest.class);
            response.status(200);
            response.type("application/json");

            String fileURL = getFileURL(service, req.file, req.fileName, null);

            int result = dataStore.updateComment(commentID, req.Text, req.webURL, fileURL);

            if (result <= 0) {
                return gson.toJson(new StructuredResponse("error", "unable to update row " + commentID, null));
            } else {
                return gson.toJson(new StructuredResponse("ok", fileURL, dataStore.selectOneComment(commentID)));
            }
        });


        // votes
        Spark.get("posts/:PostID/votes", (request, response) -> {
            Integer postID = Integer.parseInt(request.params("PostID"));

            response.status(200);
            response.type("application/json");
            int vote = dataStore.selectVoteForPost(postID, currentUser);
            if (vote == Integer.MIN_VALUE) {
                return gson.toJson(new StructuredResponse("error", "error performing selection", null));
            }
            return gson.toJson(new StructuredResponse("ok", null, vote));
        });

        Spark.put("posts/:PostID/votes", (request, response) -> {
            Integer postID = Integer.parseInt(request.params("PostID"));
            SimpleRequest req = gson.fromJson(request.body(), SimpleRequest.class);

            response.status(200);
            response.type("application/json");
            int voteID = dataStore.updateVote(req.VoteType, postID, currentUser);
            if (voteID <= 0) {
                return gson.toJson(new StructuredResponse("error", "error performing update", null));
            } else {
                return gson.toJson(new StructuredResponse("ok", "" + voteID, voteID));
            }
        });

        // users
        Spark.get("/users", (request, response) -> {
            response.status(200);
            response.type("application/json");
            try {
                MemcachedClient mc = builder.build();
                return gson.toJson(new StructuredResponse("ok", null, dataStore.selectAll("users", mc, service)));
            } catch (IOException ioe) {
                System.err.println("Couldn't create a connection to MemCachier: " +
                                    ioe.getMessage());
            }
            return gson.toJson(new StructuredResponse("error", "error performing selection", null));
            
        });
        Spark.get("/users/:UserID", (request, response) -> {
            String idx = request.params("UserID");

            response.status(200);
            response.type("application/json");
            RowData data = dataStore.selectOneUser(idx);
            if (data == null) {
                return gson.toJson(new StructuredResponse("error", idx + " not found", null));
            } else {
                return gson.toJson(new StructuredResponse("ok", null, data));
            }
        });
        Spark.put("/users/:UserID", (request, response) ->  {
            String idx = request.params("UserID");
            SimpleRequest req = gson.fromJson(request.body(), SimpleRequest.class);

            response.status(200);
            response.type("application/json");
            int result = dataStore.updateUser(req.Name, req.Email, req.Role, req.GI, req.SO, idx);
            if (result <= 0) {
                return gson.toJson(new StructuredResponse("error", idx + " not found", null));
            } else {
                return gson.toJson(new StructuredResponse("ok", null, null));
            }
        });
        Spark.delete("/users/:UserID", (request, response) -> {
            String idx = request.params("UserID");

            response.status(200);
            response.type("application/json");

            int result = dataStore.deleteUser(idx);
            if (result <= 0) {
                return gson.toJson(new StructuredResponse("error", "unable to delete row " + idx, null));
            } else {
                return gson.toJson(new StructuredResponse("ok", result + " rows deleted", null));
            }
        });
         
        // posts
        Spark.get("/posts", (request, response) -> {
            response.status(200);
            response.type("application/json");
            ArrayList<RowData> posts = null;
            try {
                MemcachedClient mc = builder.build();
                posts = dataStore.selectAll("posts", mc, service);
            } catch (IOException ioe) {
                System.err.println("Couldn't create a connection to MemCachier: " +
                                    ioe.getMessage());
            }
            return gson.toJson(new StructuredResponse("ok", null, posts));
        });
        Spark.get("/posts/:PostID", (request, response) -> {
            Integer idx = Integer.parseInt(request.params("PostID"));
            response.status(200);
            response.type("application/json");
            
            RowData data = null;
            try {
                MemcachedClient mc = builder.build();
                data = dataStore.selectOnePost(idx, mc, service);
            } catch (IOException ioe) {
                System.err.println("Couldn't create a connection to MemCachier: " +
                                    ioe.getMessage());
            }
            if (data == null) {
                return gson.toJson(new StructuredResponse("error", idx + " not found", null));
            } else {
                return gson.toJson(new StructuredResponse("ok", null, data));
            }
        });
        Spark.post("/posts", (request, response) -> {
            SimpleRequest req = gson.fromJson(request.body(), SimpleRequest.class);

            response.status(200);
            response.type("application/json");
            
            
            String fileURL = null;
            try {
                MemcachedClient mc = builder.build();
                fileURL = getFileURL(service, req.file, req.fileName, mc);

            } catch (IOException ioe) {
                System.err.println("Couldn't create a connection to MemCachier: " +
                                    ioe.getMessage());
            }

            int postID = dataStore.insertPost(currentUser, req.Subject, req.Message, req.webURL, fileURL); 
            if (postID <= 0) {
                return gson.toJson(new StructuredResponse("error", "error performing insertion", null));
            } else {
                return gson.toJson(new StructuredResponse("ok", fileURL, postID));
            }
        });

        // oauth
        Spark.post("/oauth", (request, response) -> {
            // Extract the token from the request
            String idTokenString= request.queryParams("idToken");
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory()).setAudience(Collections.singletonList(CLIENT_ID)).build();

    
            // (Receive idTokenString by HTTPS POST)

            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken != null) {
                GoogleIdToken.Payload payload = idToken.getPayload();
                // Print user identifier
                String userID = payload.getSubject();
                String email = payload.getEmail();
                String name = (String) payload.get("name");
                String pictureUrl = (String) payload.get("picture");
                String userIDToSend = generateRandomString(10);

                User user = new User(userID, name, email, "user", "male", "heterosexual", pictureUrl);

                response.status(200);
                response.type("application/json");
                RowData data = dataStore.selectOneUser(userID);
                String param2 = "previous user";
                if (data == null) {
                    dataStore.insertUser(user);
                    data = dataStore.selectOneUser(userID);
                    param2 = "new user";
                } 
                currentUser = userID;
                ((User) data).UserID = userIDToSend;
                return gson.toJson(new StructuredResponse("ok", param2, data));

            } else {
                response.status(401);
                return gson.toJson(new StructuredResponse("error", "not a valid token", null));
            }

        });

    }
    private static String CLIENT_ID = "";
    private static String currentUser = "";
    private static final String FILE_UPLOAD_ROOT_FOLDER_ID = System.getenv("FILE_UPLOAD_ROOT_FOLDER_ID");
    private static final String DEFAULT_PORT_DB = "5432";
    private static final int DEFAULT_PORT_SPARK = 4567;

    private static Database getDatabaseConnection() {
        CLIENT_ID = System.getenv("CLIENT_ID");
        if (System.getenv("DATABASE_URL") != null) {
            return Database.getDatabase(System.getenv("DATABASE_URL"), DEFAULT_PORT_DB);
        }

        Map<String, String> env = System.getenv();
        String ip = env.get("POSTGRES_IP");
        String port = env.get("POSTGRES_PORT");
        String user = env.get("POSTGRES_USER");
        String pass = env.get("POSTGRES_PASS");
        return Database.getDatabase(ip, port, user, pass);
    }

    static int getIntFromEnv(String envar, int defaultVal) {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get(envar) != null) {
            return Integer.parseInt(processBuilder.environment().get(envar));
        }
        return defaultVal;
    }

    private static void enableCORS(String origin, String methods, String headers) {
        Spark.options("/*", (request, response) -> {
            String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null) {
                response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
            }
            String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
            if (accessControlRequestMethod != null) {
                response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
            }
            return "OK";
        });

        Spark.before((request, response) -> {
            response.header("Access-Control-Allow-Origin", origin);
            response.header("Access-Control-Request-Method", methods);
            response.header("Access-Control-Allow-Headers", headers);
        });
    }
    
    public static String getFileURL(Drive service, String base64, String fileName, MemcachedClient mc) {
        java.io.File fileToUpload = new java.io.File("./src/main/java/edu/lehigh/cse216/ducks/backend/uploadedfile/" + fileName);               
        String fileURL = null;
        try {
            String partSeparator = ",";
            if (base64.contains(partSeparator)) {
                base64 = base64.split(partSeparator)[1];
            }
            // Decode Base64 String to bytes
            byte[] decodedBytes = Base64.getDecoder().decode(base64);

            // Write bytes to file
            try (FileOutputStream fos = new FileOutputStream(fileToUpload)) {
                fos.write(decodedBytes);
            }
            System.out.println("File has been successfully written.");
            
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error writing file: " + e.getMessage());
        }
        try {
            String contentType = Files.probeContentType(fileToUpload.toPath());
            File file = uploadFile(service, fileToUpload, contentType, FILE_UPLOAD_ROOT_FOLDER_ID);
            fileURL = shareFile(service, file.getId());
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            mc.set(fileURL, 0, base64);
        } catch (TimeoutException te) {
            System.err.println("Timeout during set or get: " +
                            te.getMessage());
        } catch (InterruptedException ie) {
            System.err.println("Interrupt during set or get: " +
                            ie.getMessage());
        } catch (MemcachedException me) {
            System.err.println("Memcached error during get or set: " +
                            me.getMessage());
        }
        return fileURL;
    }

    public static File uploadFile(Drive service, java.io.File filePath, String contentType, String folderId) throws IOException {
        File fileMetadata = new File();
        fileMetadata.setName(filePath.getName());
        if (folderId != null) {
            fileMetadata.setParents(Collections.singletonList(folderId));
        }
        FileContent mediaContent = new FileContent(contentType, filePath);
        File file = service.files().create(fileMetadata, mediaContent).setFields("id, parents, webViewLink, webContentLink").execute();

        return file;
    }
    
    public static Drive getDriveService() throws GeneralSecurityException, IOException {
        // Load pre-authorized user credentials from the environment.
        GoogleCredentials credentials = GoogleCredentials.getApplicationDefault()
            .createScoped(Arrays.asList(DriveScopes.DRIVE_FILE));
        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(
            credentials);
        // Build a new authorized API client service.
        return new Drive.Builder(new NetHttpTransport(), GsonFactory.getDefaultInstance(), requestInitializer).setApplicationName("Drive Uploader").build();
    }

    public static String shareFile(Drive service, String fileId) throws IOException {
        Permission permission = new Permission()
            .setType("anyone")
            .setRole("reader");
        service.permissions().create(fileId, permission).execute();

        File updatedFile = service.files().get(fileId)
            .setFields("webViewLink")
            .execute();

        return updatedFile.getWebViewLink();
    }

    public static String generateRandomString(int length) {
        // Define the character set from which to generate the random string
        String charset = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();

        // Generate random characters from the charset
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(charset.length());
            sb.append(charset.charAt(index));
        }

        return sb.toString();
    }
}  