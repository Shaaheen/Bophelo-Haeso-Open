package capstone.bophelohaesoopen.HaesoAPI.Model;

/**
 * Object representation of a media file
 */
public class Media {
    String name;
    String filePath;
    public static String identifierPrefix = "";
    public static String mediaExtension = "";

    public enum MediaType{IMAGE, VIDEO, AUDIO}

    /**
     * Represents a media object
     * @param name Name of media (No extension or prefix here)
     * @param filePath Path to file in phone storage
     */
    public Media(String name, String filePath) {
        this.name = name;
        this.filePath = filePath;
    }

    /**
     * Gets name of file without prefix and extension
     * @return - file name without prefix and extension
     */
    public String getName() {
        int index = filePath.lastIndexOf("/");
        String filename = filePath.substring(index);
        if(!identifierPrefix.equals(""))
        {
            if(filename.contains(identifierPrefix))
            {
                int prefixlength = identifierPrefix.length();
                filename = filename.substring(prefixlength+1);
            }
        }

        // Length of media extension strings i.e. .3gp / .jpg / .mp4
        int extensionLength = 4;
        filename = filename.substring(0,(filename.length() - extensionLength));
        return filename;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getFileName(){
        return (identifierPrefix + name + mediaExtension);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public static void setIdentifierPrefix(String identifierPrefix) {
        Media.identifierPrefix = identifierPrefix;
    }
}
