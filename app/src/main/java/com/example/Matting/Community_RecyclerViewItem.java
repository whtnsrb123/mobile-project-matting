package com.example.Matting;

public class Community_RecyclerViewItem {
    private String mImgName;
    private String mMainText;
    private String mSubText;
    private String documentId;

    // 기본 생성자 (Firestore에서 객체 생성 시 필요)
    public Community_RecyclerViewItem() {}

    // Getter 및 Setter
    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getImgName() {
        return mImgName;
    }

    public void setImgName(String imgName) {
        this.mImgName = imgName;
    }

    public String getMainText() {
        return mMainText;
    }

    public void setMainText(String mainText) {
        this.mMainText = mainText;
    }

    public String getSubText() {
        return mSubText;
    }

    public void setSubText(String subText) {
        this.mSubText = subText;
    }
}