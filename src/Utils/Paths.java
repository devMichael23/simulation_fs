package Utils;

import java.io.File;
import java.util.Date;

public class Paths {
    public Date date = new Date();

    public File logs = new File("logs");
    public File logs_file = new File("logs", "logs.txt");
    public File files_dir = new File("files");
    public String logs_path = "logs";
    public String file_path = "files";

    public File backup = new File("backup");
    public File backup_file = new File("backup", "backup.txt");
    public File filebackup_dir = new File("backup/files_copy");
    public String filebackup_path = "backup/files_copy";
    public String backup_path = "backup";
}
