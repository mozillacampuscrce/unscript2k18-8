package com.codeblooded.chehra.teacher.ui.activities;

import android.support.v7.app.AppCompatActivity;

/**
 * Created by Aashish Nehete on 25-Mar-18.
 */

public class ImageActivity extends AppCompatActivity {

    /*private static final String LOG="ImageActivity";

    CropImageView cropImageView;
    Bitmap bitmap;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        cropImageView = findViewById(R.id.cropImageView);
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);

        cropImageView.setOnCropImageCompleteListener(new CropImageView.OnCropImageCompleteListener() {
            @Override
            public void onCropImageComplete(CropImageView view, CropImageView.CropResult result) {
                bitmap = cropImageView.getCroppedImage();
            }
        });

    }

    private void pickImage(int id) {
        CropImage.activity(null)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setActivityMenuIconColor(R.color.red)
                .setCropShape(CropImageView.CropShape.RECTANGLE)
                .setAspectRatio(1, 1)
                .start(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri uri = result.getUri();
                cropImageView.setImageUriAsync(uri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }*/
}
