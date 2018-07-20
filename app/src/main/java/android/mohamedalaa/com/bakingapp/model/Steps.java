package android.mohamedalaa.com.bakingapp.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;

/**
 * Created by Mohamed on 7/17/2018.
 *
 */
/*@Entity(foreignKeys = @ForeignKey(entity = Recipe.class,
        parentColumns = "id",
        childColumns = "childIdTwo",
        onDelete = ForeignKey.CASCADE))*/
public class Steps implements Serializable {

    /*@PrimaryKey
    private int childIdTwo;*/

    private String shortDescription;

    private String description;

    private String videoURL;

    private String thumbnailURL;

    public Steps(String shortDescription, String description, String videoURL, String thumbnailURL) {
        this.shortDescription = shortDescription;
        this.description = description;
        this.videoURL = videoURL;
        this.thumbnailURL = thumbnailURL;
    }

    /*public int getChildIdTwo() {
        return childIdTwo;
    }

    public void setChildIdTwo(int childIdTwo) {
        this.childIdTwo = childIdTwo;
    }*/

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVideoURL() {
        return videoURL;
    }

    public void setVideoURL(String videoURL) {
        this.videoURL = videoURL;
    }

    public String getThumbnailURL() {
        return thumbnailURL;
    }

    public void setThumbnailURL(String thumbnailURL) {
        this.thumbnailURL = thumbnailURL;
    }
}
