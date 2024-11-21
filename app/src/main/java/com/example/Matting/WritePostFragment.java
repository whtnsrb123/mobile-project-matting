package com.example.Matting;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class WritePostFragment extends Fragment {

    private EditText postTitle;
    private EditText postContent;
    private Uri selectedImageUri;

    // Activity Result Launcher 선언
    private final ActivityResultLauncher<Intent> galleryLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    Toast.makeText(getActivity(), "이미지가 선택되었습니다.", Toast.LENGTH_SHORT).show();
                }
            });

    private OnPostUploadedListener postUploadedListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_write_post, container, false);

        postTitle = view.findViewById(R.id.postTitle);
        postContent = view.findViewById(R.id.postContent);
        Button selectImageButton = view.findViewById(R.id.selectImageButton);
        Button uploadPostButton = view.findViewById(R.id.uploadPostButton);

        // 이미지 선택 버튼 클릭 이벤트
        selectImageButton.setOnClickListener(v -> openGallery());

        // 업로드 버튼 클릭 이벤트
        uploadPostButton.setOnClickListener(v -> uploadPost());

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnPostUploadedListener) {
            postUploadedListener = (OnPostUploadedListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnPostUploadedListener");
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galleryLauncher.launch(intent); // 새로운 API로 갤러리 시작
    }

    private void uploadPost() {
        String title = postTitle.getText().toString().trim();
        String content = postContent.getText().toString().trim();

        if (title.isEmpty() || content.isEmpty() || selectedImageUri == null) {
            Toast.makeText(requireContext(), "모든 필드를 입력하세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        String encodedImage = encodeImageToBase64(selectedImageUri);

        if (encodedImage == null) {
            Toast.makeText(requireContext(), "이미지 인코딩에 실패했습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Firestore 데이터 준비
        Map<String, Object> postMap = new HashMap<>();
        postMap.put("username", "사용자"); // 실제 사용자 이름으로 변경 필요
        postMap.put("postContent", content);
        postMap.put("imageResource", encodedImage); // Base64가 아니라 URI를 사용
        postMap.put("timestamp", FieldValue.serverTimestamp());
        postMap.put("commentCount", 0);
        postMap.put("reactionCount", 0);
        postMap.put("reacted", false);

        // Firestore에 데이터 저장
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("posts")
                .add(postMap)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getContext(), "게시글이 업로드되었습니다.", Toast.LENGTH_SHORT).show();

                    // 성공 시 프래그먼트 종료
                    getParentFragmentManager().popBackStack();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "게시글 업로드 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    public interface OnPostUploadedListener {
        void onPostUploaded(Map<String, Object> newPost);
    }

    private String encodeImageToBase64(Uri imageUri) {
        try {
            ContentResolver contentResolver = requireContext().getContentResolver();
            InputStream inputStream = contentResolver.openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] imageBytes = byteArrayOutputStream.toByteArray();

            return Base64.encodeToString(imageBytes, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
