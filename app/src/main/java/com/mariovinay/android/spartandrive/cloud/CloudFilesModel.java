package com.mariovinay.android.spartandrive.cloud;

/**
 * Created by mario on 11/29/2015.
 */
public class CloudFilesModel {
    private String name;
    private boolean isFolder;
    private String createdDate;
    private String modifiedDate;
    private int fileSize;
    private String fileType;
    private String owner;
    private String path;
    private boolean isShared;
    private String[] sharedNames;
    private String[] sharedEmails;
    private String[] permissionType;
    private int iconId;
    private int infoIconId;

    public String getDriveId() {
        return driveId;
    }
    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    private String mimeType;
    public void setDriveId(String driveId) {
        this.driveId = driveId;
    }

    private String driveId;

    public int getInfoIconId() {
        return infoIconId;
    }

    public void setInfoIconId(int infoIconId) {
        this.infoIconId = infoIconId;
    }

    public int getIconId() {
        return iconId;
    }

    public void setIconId(int iconId) {
        this.iconId = iconId;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isFolder() {
        return isFolder;
    }

    public void setIsFolder(boolean isFolder) {
        this.isFolder = isFolder;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(String modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public int getFileSize() {
        return fileSize;
    }

    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isShared() {
        return isShared;
    }

    public void setIsShared(boolean isShared) {
        this.isShared = isShared;
    }

    public String[] getSharedNames() {
        return sharedNames;
    }

    public void setSharedNames(String[] sharedNames) {
        this.sharedNames = sharedNames;
    }

    public String[] getSharedEmails() {
        return sharedEmails;
    }

    public void setSharedEmails(String[] sharedEmails) {
        this.sharedEmails = sharedEmails;
    }

    public String[] getPermissionType() {
        return permissionType;
    }

    public void setPermissionType(String[] permissionType) {
        this.permissionType = permissionType;
    }


}
