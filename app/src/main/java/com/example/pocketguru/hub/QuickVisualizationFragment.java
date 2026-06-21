package com.example.pocketguru.hub;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.pocketguru.R;
import com.example.pocketguru.views.AnnotationLineView;
import com.google.android.material.snackbar.Snackbar;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class QuickVisualizationFragment extends Fragment {

    private static final String TAG = "QuickVisHub";
    private ViewFlipper viewFlipper;
    private RelativeLayout containerHubContent;
    private AnnotationLineView annotationLine;
    private ImageView imageCapturedLeaf, imageStomataDiagram, gifGaseousExchange, imgChloroplast;
    private LinearLayout layoutChlorophyll;
    private Button btnAction, btnScan;
    private ProgressBar progressScanning;
    private TextView textHint;

    private Uri photoUri;
    private String currentPhotoPath;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    dispatchTakePictureIntent();
                } else {
                    Snackbar.make(requireView(), "Camera permission is required to scan a leaf.", Snackbar.LENGTH_LONG).show();
                }
            });

    private final ActivityResultLauncher<Uri> takePictureLauncher =
            registerForActivityResult(new ActivityResultContracts.TakePicture(), success -> {
                if (success) {
                    validateLeaf(currentPhotoPath);
                } else {
                    btnScan.setEnabled(true);
                }
            });

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!OpenCVLoader.initDebug()) {
            Log.e(TAG, "OpenCV initialization failed.");
        } else {
            Log.d(TAG, "OpenCV initialization succeeded.");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quick_visualization, container, false);

        viewFlipper = view.findViewById(R.id.view_flipper);
        
        // Stage 1 views
        btnScan = view.findViewById(R.id.btn_scan);
        progressScanning = view.findViewById(R.id.progress_scanning);
        view.findViewById(R.id.btn_close_stage1).setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        // Stage 2 views
        containerHubContent = view.findViewById(R.id.container_hub_content);
        annotationLine = view.findViewById(R.id.annotation_line);
        imageCapturedLeaf = view.findViewById(R.id.image_captured_leaf);
        imageStomataDiagram = view.findViewById(R.id.image_stomata_diagram);
        gifGaseousExchange = view.findViewById(R.id.gif_gaseous_exchange);
        imgChloroplast = view.findViewById(R.id.img_chloroplast);
        layoutChlorophyll = view.findViewById(R.id.layout_chlorophyll);
        btnAction = view.findViewById(R.id.btn_action);
        textHint = view.findViewById(R.id.text_hint);
        view.findViewById(R.id.btn_close_stage2).setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        setupListeners();

        return view;
    }

    private void setupListeners() {
        btnScan.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();
            } else {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA);
            }
        });

        imageCapturedLeaf.setOnClickListener(v -> showLeafPopup(v));
        
        imageStomataDiagram.setOnClickListener(v -> {
            showStomataPopup(v);
            
            // Fade out stomata diagram and transition to GIF
            ObjectAnimator fadeOut = ObjectAnimator.ofFloat(imageStomataDiagram, "alpha", 1f, 0f);
            fadeOut.setDuration(300);
            fadeOut.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    gifGaseousExchange.setVisibility(View.VISIBLE);
                    gifGaseousExchange.setAlpha(0f);
                    
                    Glide.with(requireContext())
                        .asGif()
                        .load(R.drawable.gaseous_exchange)
                        .into(gifGaseousExchange);
                    
                    ObjectAnimator fadeIn = ObjectAnimator.ofFloat(gifGaseousExchange, "alpha", 0f, 1f);
                    fadeIn.setDuration(300);
                    fadeIn.start();
                }
            });
            fadeOut.start();
        });

        gifGaseousExchange.setOnClickListener(v -> {
            // Fade out GIF and return to static diagram
            ObjectAnimator fadeOut = ObjectAnimator.ofFloat(gifGaseousExchange, "alpha", 1f, 0f);
            fadeOut.setDuration(300);
            fadeOut.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    gifGaseousExchange.setVisibility(View.GONE);
                    
                    ObjectAnimator fadeIn = ObjectAnimator.ofFloat(imageStomataDiagram, "alpha", 0f, 1f);
                    fadeIn.setDuration(300);
                    fadeIn.start();
                }
            });
            fadeOut.start();
        });
        
        imgChloroplast.setOnClickListener(v -> {
            if (layoutChlorophyll.getVisibility() == View.GONE) {
                layoutChlorophyll.setVisibility(View.VISIBLE);
                ObjectAnimator.ofFloat(layoutChlorophyll, "alpha", 0f, 1f).setDuration(300).start();
                
                btnAction.setVisibility(View.VISIBLE);
                ObjectAnimator.ofFloat(btnAction, "alpha", 0f, 1f).setDuration(300).start();
                
                textHint.setText("Watch photosynthesis in action");
            }
        });

        btnAction.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.VideoPlayerFragment);
        });
    }

    private void dispatchTakePictureIntent() {
        btnScan.setEnabled(false);
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
            btnScan.setEnabled(true);
            Snackbar.make(requireView(), "Error creating file.", Snackbar.LENGTH_LONG).show();
        }

        if (photoFile != null) {
            photoUri = FileProvider.getUriForFile(requireContext(),
                    requireContext().getPackageName() + ".fileprovider",
                    photoFile);
            takePictureLauncher.launch(photoUri);
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void validateLeaf(String imagePath) {
        progressScanning.setVisibility(View.VISIBLE);
        executorService.execute(() -> {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            if (bitmap == null) {
                mainHandler.post(() -> onValidationResult(false));
                return;
            }

            Mat mat = new Mat();
            Utils.bitmapToMat(bitmap, mat);

            Mat hsvMat = new Mat();
            Imgproc.cvtColor(mat, hsvMat, Imgproc.COLOR_RGB2HSV);

            Mat mask = new Mat();
            // Threshold for green leaf (OpenCV uses HSV range H: 0-180, S: 0-255, V: 0-255)
            // Green is roughly 35-85 in OpenCV hue scale
            Core.inRange(hsvMat, new Scalar(35, 50, 50), new Scalar(85, 255, 255), mask);

            List<MatOfPoint> contours = new ArrayList<>();
            Mat hierarchy = new Mat();
            Imgproc.findContours(mask, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

            double maxArea = 0;
            for (MatOfPoint contour : contours) {
                double area = Imgproc.contourArea(contour);
                if (area > maxArea) {
                    maxArea = area;
                }
            }

            double totalArea = mat.width() * mat.height();
            boolean isLeaf = maxArea > (totalArea * 0.08);

            mainHandler.post(() -> onValidationResult(isLeaf));
        });
    }

    private void onValidationResult(boolean success) {
        progressScanning.setVisibility(View.GONE);
        if (success) {
            Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath);
            imageCapturedLeaf.setImageBitmap(bitmap);
            viewFlipper.setDisplayedChild(1); // Transition to Stage 2
            setupAnnotationLine();
        } else {
            btnScan.setEnabled(true);
            Snackbar.make(requireView(), "We couldn't find a leaf! Try in better lighting or move closer.", Snackbar.LENGTH_LONG).show();
        }
    }

    private void setupAnnotationLine() {
        containerHubContent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                containerHubContent.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                // Start point: left-ish portion of stomata diagram
                int[] stomataLocation = new int[2];
                imageStomataDiagram.getLocationInWindow(stomataLocation);
                int[] lineLocation = new int[2];
                annotationLine.getLocationInWindow(lineLocation);

                float startX = (stomataLocation[0] - lineLocation[0]) + imageStomataDiagram.getWidth() * 0.2f;
                float startY = (stomataLocation[1] - lineLocation[1]) + imageStomataDiagram.getHeight() * 0.8f;

                // End point: top center of chloroplast ImageView
                int[] chloroplastLocation = new int[2];
                imgChloroplast.getLocationInWindow(chloroplastLocation);

                float endX = (chloroplastLocation[0] - lineLocation[0]) + imgChloroplast.getWidth() / 2f;
                float endY = (chloroplastLocation[1] - lineLocation[1]);

                annotationLine.setCoordinates(startX, startY, endX, endY);
            }
        });
    }

    private void showLeafPopup(View anchor) {
        showPopup(anchor, "Your Leaf", "This is your real leaf! Leaves contain chlorophyll in their chloroplasts, which gives them their green colour and powers photosynthesis.");
    }

    private void showStomataPopup(View anchor) {
        showPopup(anchor, "Stomata", "These tiny openings on the underside of leaves allow CO₂ to enter and O₂ to exit during photosynthesis.");
    }

    private void showPopup(View anchor, String title, String content) {
        View popupView = LayoutInflater.from(requireContext()).inflate(R.layout.layout_keyword_tooltip, null);
        PopupWindow popupWindow = new PopupWindow(popupView, 
                ViewGroup.LayoutParams.WRAP_CONTENT, 
                ViewGroup.LayoutParams.WRAP_CONTENT, true);

        TextView tvTitle = popupView.findViewById(R.id.text_keyword_name);
        TextView tvContent = popupView.findViewById(R.id.text_keyword_definition);
        tvTitle.setText(title);
        tvContent.setText(content);
        
        // Hide TTS icon for these simple popups
        popupView.findViewById(R.id.btn_speak).setVisibility(View.GONE);

        popupWindow.setElevation(10);
        // Show above the anchor
        popupWindow.showAsDropDown(anchor, 0, -anchor.getHeight() - 200, Gravity.TOP);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}
