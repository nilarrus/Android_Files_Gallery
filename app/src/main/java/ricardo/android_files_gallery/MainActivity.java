package ricardo.android_files_gallery;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.snappydb.SnappydbException;

import org.w3c.dom.Text;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import ricardo.android_files_gallery.ColorChoser.ColorinesActivity;
import ricardo.android_files_gallery.ColorChoser.Constant;
import ricardo.android_files_gallery.ColorChoser.Methods;
import ricardo.android_files_gallery.Database.DBAccess;
import ricardo.android_files_gallery.Database.Database;
import ricardo.android_files_gallery.Files.FileManager;
import ricardo.android_files_gallery.Permission.AbsRuntimePermision;

/* convercion
1 Kilobyte = 1,024 Bytes
1 Megabyte = 1,048,576 Bytes
1 Gigabyte = 1,073,741,824 Byte
1 Terabyte = 1,099,511,627,776 Bytes
*/
public class MainActivity extends AbsRuntimePermision
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final Pattern DIR_SEPORATOR = Pattern.compile("/");
    private static final int REQUEST_PERMISSION = 10;
    private boolean load = true;
    TextView InternalStorage, ExternalStorage;
    public static String rutaInterna = null;
    public static String[] rutaExterna = null;
    private boolean Permisions = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Database database = new Database(this);
        Integer color = null;
        try {
            color = database.getInt(DBAccess.COLOR);
            Log.d("asd", color.toString());
        } catch (SnappydbException e) {
            e.printStackTrace();
        }
        if (color == null) {
            try {
                database.putInt(DBAccess.COLOR, Constant.sky);
            } catch (SnappydbException e) {
                e.printStackTrace();
            }
            color = Constant.sky;
        }

        Methods.setColorTheme(this, color);

        setTheme(Constant.theme);
        setContentView(R.layout.activity_main);

        requestAppPermissions(new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE},
                R.string.msg, REQUEST_PERMISSION);


        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        rutaInterna = RutaInterna();
        rutaExterna = RutaExterna();


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    public void onPermissionsGranted(int requestCode) {

        final RelativeLayout phoneStorage = (RelativeLayout) findViewById(R.id.phone_storage);
        TextView TotalNumStorage = (TextView)findViewById(R.id.InternoNumTotal);
        TextView UsadoNumStorage = (TextView)findViewById(R.id.InternoNumUsado);
      //  final String numero = TamanyTotalMemoria(rutaInterna);
        //TotalNumStorage.setText(numero);
       // UsadoNumStorage.setText(TamayUtilitzatMemoria(rutaInterna));
        phoneStorage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //File manager <3
                Intent intent = new Intent(getApplicationContext(), FileManager.class);
                intent.putExtra("path", rutaInterna + "/"); //rutaInterna
                intent.putExtra("root", rutaInterna + "/");
                startActivity(intent);
            }
        });
        //carregem la sd si existeix la carrega amb el litener adecuat sino no surt
        RelativeLayout sdStorage = (RelativeLayout) findViewById(R.id.sd_storage);
        if(rutaExterna==null || rutaExterna[0].contains("sdcard")){
            sdStorage.setVisibility(View.GONE);
        }else {
            final File StadoMemoria = new File(rutaExterna[0]);
            TextView TotalSD_Storage = (TextView) findViewById(R.id.espacioSD_Total);
            TextView UsadoSD_Storage = (TextView) findViewById(R.id.espacioSD_Usado);
            final String numero1 = TamanyTotalMemoria(rutaExterna[0]);
            TotalSD_Storage.setText(numero1);
            UsadoSD_Storage.setText(TamayUtilitzatMemoria(rutaExterna[0]));

            if (Environment.getExternalStorageState(StadoMemoria).equalsIgnoreCase("removed")) {
                sdStorage.setVisibility(View.GONE);
            } else {
                sdStorage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //File manager SD External
                        Intent intent = new Intent(getApplicationContext(), FileManager.class);
                        intent.putExtra("path", rutaExterna[0] + "/"); //rutaInterna
                        intent.putExtra("root", rutaExterna[0] + "/");
                        startActivity(intent);
                    }
                });
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.phone_storage) {
            Intent intent = new Intent(getApplicationContext(), FileManager.class);
            intent.putExtra("path", rutaInterna + "/"); //rutaInterna
            startActivity(intent);
        } else if (id == R.id.sd_storage) {
            Intent intent = new Intent(getApplicationContext(), FileManager.class);
            intent.putExtra("path", rutaExterna[0] + "/"); //rutaInterna
            startActivity(intent);
        } else if (id == R.id.nav_galeria) {
            Intent intent = new Intent(getApplicationContext(), GalleryActivity.class);
            intent.putExtra("path", rutaExterna[0] + "/"); //rutaInterna
            startActivity(intent);
        } else if (id == R.id.nav_apariencia) {
            Intent searchIntent = new Intent(MainActivity.this, ColorinesActivity.class);
            startActivity(searchIntent);
            overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
        } else if (id == R.id.about_us) {
            Toast.makeText(this, "Ricardo THE BEST", Toast.LENGTH_LONG).show();
        } else if (id == R.id.blanco_switch) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private String RutaInterna() {
        String ruta = null;
        InternalStorage = (TextView) findViewById(R.id.textTituloInter);
        // ExternalStorage = (TextView) findViewById(R.id.textTituloExt);

        String IntStorage;

        IntStorage = Environment.getExternalStorageDirectory().getAbsolutePath();
        ruta = IntStorage;
        if (IntStorage.contains("emulated")) {
            InternalStorage.setText("Memoria Interna del Dispositivo");
        }

        return ruta;
    }

    private String[] RutaExterna() {
        // Final set of paths
        final Set<String> rv = new HashSet<String>();
        // Primary physical SD-CARD (not emulated)
        final String rawExternalStorage = System.getenv("EXTERNAL_STORAGE");
        // All Secondary SD-CARDs (all exclude primary) separated by ":"
        final String rawSecondaryStoragesStr = System.getenv("SECONDARY_STORAGE");
        // Primary emulated SD-CARD
        final String rawEmulatedStorageTarget = System.getenv("EMULATED_STORAGE_TARGET");
        if (TextUtils.isEmpty(rawEmulatedStorageTarget)) {
            // Device has physical external storage; use plain paths.
            if (TextUtils.isEmpty(rawExternalStorage)) {
                // EXTERNAL_STORAGE undefined; falling back to default.
                rv.add("/storage/sdcard0");
            } else {
                rv.add(rawExternalStorage);
            }
        } else {
            // Device has emulated storage; external storage paths should have
            // userId burned into them.
            final String rawUserId;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                rawUserId = "";
            } else {
                final String path = Environment.getExternalStorageDirectory().getAbsolutePath();
                final String[] folders = DIR_SEPORATOR.split(path);
                final String lastFolder = folders[folders.length - 1];
                boolean isDigit = false;
                try {
                    Integer.valueOf(lastFolder);
                    isDigit = true;
                } catch (NumberFormatException ignored) {
                }
                rawUserId = isDigit ? lastFolder : "";
            }
            // /storage/emulated/0[1,2,...]
            if (TextUtils.isEmpty(rawUserId)) {
                rv.add(rawEmulatedStorageTarget);
            } else {
                rv.add(rawEmulatedStorageTarget + File.separator + rawUserId);
            }
        }
        // Add all secondary storages
        if (!TextUtils.isEmpty(rawSecondaryStoragesStr)) {
            // All Secondary SD-CARDs splited into array
            final String[] rawSecondaryStorages = rawSecondaryStoragesStr.split(File.pathSeparator);
            Collections.addAll(rv, rawSecondaryStorages);
        }
        return rv.toArray(new String[rv.size()]);
    }

    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
public String TamayUtilitzatMemoria(String ruta){
    DecimalFormat df = new DecimalFormat("###.##");
    File arxiu = new File(ruta);
    //ESPAI TOTAL
    String numero = null;//bytes

    float auxNum = arxiu.getTotalSpace()-arxiu.getUsableSpace();//numero temporal per pasar el valors i fer la combercio
    if (arxiu.getTotalSpace() % 1024 != 0) { //kilobyte
        numero = String.valueOf(auxNum) + " B";

    } else if (arxiu.getTotalSpace() % 1024 == 0 && arxiu.getTotalSpace() / 1048576 == 0) {

        numero = df.format(auxNum / 1024) + " KB";

    } else if (arxiu.getTotalSpace() / 1048576 != 0 && arxiu.getTotalSpace() / 1073741824 == 0) {

        numero = df.format(auxNum / 1048576) + " MB";

    } else {

        numero = df.format(auxNum / 1073741824) + " GB";
    }
    return numero;
}
    public String TamanyTotalMemoria(String ruta) {
        DecimalFormat df = new DecimalFormat("###.##");
        File arxiu = new File(ruta);
        //ESPAI TOTAL
        String numero = null;//bytes
        float auxNum = arxiu.getTotalSpace();//numero temporal per pasar el valors i fer la combercio
        if (arxiu.getTotalSpace() % 1024 != 0) { //kilobyte
            numero = String.valueOf(auxNum) + " B";

        } else if (arxiu.getTotalSpace() % 1024 == 0 && arxiu.getTotalSpace() / 1048576 == 0) {

            numero = df.format(auxNum / 1024) + " KB";

        } else if (arxiu.getTotalSpace() / 1048576 != 0 && arxiu.getTotalSpace() / 1073741824 == 0) {

            numero = df.format(auxNum / 1048576) + " MB";

        } else {

            numero = df.format(auxNum / 1073741824) + " GB";
        }
        return numero;
    }
}
