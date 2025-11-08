package com.example.tareagrupal3pm1;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 101;
    private static final int REQUEST_PICK_IMAGE = 102;
    private static final int REQUEST_PERMISSION_CODE = 100;

    ImageView imgFoto;
    EditText etDescripcion;
    Button btnSalvar;
    ListView listViewFotos;

    DatabaseHelper dbHelper;
    ArrayList<Photograph> photoList;
    PhotographAdapter adapter;

    byte[] currentImageBytes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        imgFoto = findViewById(R.id.imgFoto);
        etDescripcion = findViewById(R.id.etDescripcion);
        btnSalvar = findViewById(R.id.btnSalvar);
        listViewFotos = findViewById(R.id.listViewFotos);


        dbHelper = new DatabaseHelper(this);
        photoList = new ArrayList<>();

        loadPhotosInListView();

        imgFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermissionsAndChooseImage();
            }
        });

        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePhotograph();
            }
        });
    }

    private void checkPermissionsAndChooseImage() {
        String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE};
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSION_CODE);
        } else {
            showImagePickDialog();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showImagePickDialog();
            } else {
                Toast.makeText(this, "Permisos denegados", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showImagePickDialog() {
        String[] options = {"Cámara", "Galería"};
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Seleccionar Imagen");
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            } else {
                Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhotoIntent, REQUEST_PICK_IMAGE);
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bitmap = null;

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE && data != null) {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    bitmap = (Bitmap) extras.get("data");
                }
            } else if (requestCode == REQUEST_PICK_IMAGE && data != null) {
                Uri selectedImageUri = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (bitmap != null) {
                imgFoto.setImageBitmap(bitmap);
                currentImageBytes = getBitmapAsByteArray(bitmap);
            }
        }
    }

    private void savePhotograph() {
        String descripcion = etDescripcion.getText().toString().trim();

        if (currentImageBytes == null) {
            Toast.makeText(this, "Por favor, seleccione una imagen", Toast.LENGTH_SHORT).show();
            return;
        }
        if (descripcion.isEmpty()) {
            Toast.makeText(this, "Por favor, ingrese una descripción", Toast.LENGTH_SHORT).show();
            return;
        }

        Photograph photo = new Photograph();
        photo.setImagen(currentImageBytes);
        photo.setDescripcion(descripcion);

        boolean isAdded = dbHelper.addPhotograph(photo);

        if (isAdded) {
            Toast.makeText(this, "Foto guardada exitosamente", Toast.LENGTH_SHORT).show();
            etDescripcion.setText("");
            imgFoto.setImageResource(R.mipmap.ic_launcher);
            currentImageBytes = null;
            loadPhotosInListView();
        } else {
            Toast.makeText(this, "Error al guardar la foto", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadPhotosInListView() {
        photoList.clear();
        photoList.addAll(dbHelper.getAllPhotographs());

        if(adapter == null) {
            adapter = new PhotographAdapter(this, R.layout.list_item_photograph, photoList);
            listViewFotos.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        return outputStream.toByteArray();
    }
}