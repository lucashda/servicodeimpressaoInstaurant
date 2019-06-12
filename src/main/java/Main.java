import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.EventListener;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreException;
import com.google.cloud.firestore.ListenerRegistration;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.Transaction;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.database.annotations.Nullable;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.EventQueue;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class Main {

    private static ArrayList<Pedido> pedidos = new ArrayList<>();
    private static Firestore db;
    private final static Timer[] timer = {new Timer()};
    private static TimerTask tarefa;

    public static void main(String[] args) throws IOException {
        FileInputStream serviceAccount = new FileInputStream("resto-ondeline-firebase-adminsdk-x27hh-f446870400.json");

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://resto-ondeline.firebaseio.com")
                .build();

        FirebaseApp.initializeApp(options);
        db = FirestoreClient.getFirestore();

        timer[0] = new Timer();
        tarefa = new TimerTask() {
            @Override
            public void run() {
                try {
                    getDatabase();
                } catch (ExecutionException | InterruptedException | IOException e) {
                    e.printStackTrace();
                }
            }
        };
        EventQueue.invokeLater(() -> {
            try {
                TelaPrincipal frame = new TelaPrincipal();
                frame.setVisible(true);
                timer[0].schedule(tarefa, 10000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static void getDatabase() throws ExecutionException, InterruptedException, IOException {
        System.out.println("run...");
        ApiFuture<QuerySnapshot> future =
                db.collection("Pedidos").whereEqualTo("printed", false).get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        for (QueryDocumentSnapshot document : documents) {
            System.out.println(document.getId());
            Pedido pedido = document.toObject(Pedido.class);
            boolean isPrinted = imprimirNotaFiscal(downloadNotaFiscal(pedido, document.getId()));
            pedido.setPrinted(isPrinted);
            updadeDatabase(document, pedido);
        }
    }

    private static void updadeDatabase(QueryDocumentSnapshot querySnapshot, Pedido pedido) {
             db.runTransaction(
                        transaction1 -> {
                            transaction1.update(querySnapshot.getReference(), "printed", pedido.isPrinted());
                            return null;
                        });
    }

    public static File downloadNotaFiscal(Pedido pedido, String id) throws IOException {

        URL url = new URL(pedido.getDownloadUrl());
        File file = new File(id + ".pdf");

        InputStream is = url.openStream();
        FileOutputStream fos = new FileOutputStream(file);

        int bytes;

        while ((bytes = is.read()) != -1) {
            fos.write(bytes);
        }

        is.close();

        fos.close();

        return file;
    }

    public static boolean imprimirNotaFiscal(File file) {

        Desktop desktop = Desktop.getDesktop();
        try {
            desktop.print(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    static class TelaPrincipal extends JFrame{
        private JPanel contentPane;
        public TelaPrincipal() {
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setBounds(100, 100, 450, 300);
            contentPane = new JPanel();
            contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
            contentPane.setLayout(new BorderLayout(0, 0));
            setContentPane(contentPane);
        }
    }
}
